package com.dahuntun.wxshop.service;

import com.dahuntun.api.DataStatus;
import com.dahuntun.wxshop.controller.ShoppingCartController;
import com.dahuntun.wxshop.dao.ShoppingCartQueryMapper;
import com.dahuntun.wxshop.entity.HttpException;
import com.dahuntun.api.data.PageResponse;
import com.dahuntun.wxshop.entity.ShoppingCartData;
import com.dahuntun.wxshop.entity.ShoppingCartGoods;
import com.dahuntun.wxshop.generate.Goods;
import com.dahuntun.wxshop.generate.GoodsMapper;
import com.dahuntun.wxshop.generate.ShoppingCart;
import com.dahuntun.wxshop.generate.ShoppingCartMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static java.util.stream.Collectors.*;

@Service
public class ShoppingCartService {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);
    private final ShoppingCartQueryMapper shoppingCartQueryMapper;
    private final GoodsMapper goodsMapper;
    private final SqlSessionFactory sqlSessionFactory;
    private final GoodsService goodsService;

    @Inject
    public ShoppingCartService(ShoppingCartQueryMapper shoppingCartQueryMapper,
                               GoodsMapper goodsMapper,
                               SqlSessionFactory sqlSessionFactory,
                               GoodsService goodsService) {
        this.shoppingCartQueryMapper = shoppingCartQueryMapper;
        this.goodsMapper = goodsMapper;
        this.sqlSessionFactory = sqlSessionFactory;
        this.goodsService = goodsService;
    }

    public PageResponse<ShoppingCartData> getShoppingCartOfUser(Long userId, int pageNum, int pageSize) {

        int offset = (pageNum - 1) * pageSize;

        int totalNum = shoppingCartQueryMapper.countHowManyShopsInUserShoppingCart(userId);

        List<ShoppingCartData> pagedData = shoppingCartQueryMapper.selectShoppingCartDataByUserId(userId, pageSize, offset)
                .stream()
                .collect(groupingBy(shoppingCartData -> shoppingCartData.getShop().getId()))
                .values()
                .stream()
                .map(this::merge)
                .collect(toList());


        int totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;

        return PageResponse.pageData(pageNum, pageSize, totalPage, pagedData);
    }

    private ShoppingCartData merge(List<ShoppingCartData> goodsOfSameShop) {
        ShoppingCartData result = new ShoppingCartData();
        result.setShop(goodsOfSameShop.get(0).getShop());
        List<ShoppingCartGoods> goods = goodsOfSameShop.stream()
                .map(ShoppingCartData::getGoods)
                .flatMap(List::stream)
                .collect(toList());
        result.setGoods(goods);
        return result;
    }

    public ShoppingCartData addToShoppingCart(ShoppingCartController.AddToShoppingCartRequest request,
                                                        long userId) {
        List<Long> goodsId = request.getGoods()
                .stream()
                .map(ShoppingCartController.AddToShoppingCartItem::getId)
                .collect(toList());

        if (goodsId.isEmpty()) {
            throw HttpException.badRequest("商品ID为空！");
        }

        Map<Long, Goods> idToGoodsMap = goodsService.getIdToGoodsMap(goodsId);

        if (idToGoodsMap.values().stream().map(Goods::getShopId).collect(toSet()).size() != 1) {
            logger.debug("非法请求：{}, {}", goodsId, idToGoodsMap.values());
            throw HttpException.badRequest("商品ID非法！");
        }

        List<ShoppingCart> shoppingCartRows = request.getGoods()
                .stream()
                .map(item -> toShoppingCartRow(item, idToGoodsMap))
                .filter(Objects::nonNull)
                .collect(toList());

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            shoppingCartRows.forEach(mapper::insert);
            sqlSession.commit();
        }

        return getLatestShoppingCartDataByUserIdShopId(new ArrayList<>(idToGoodsMap.values()).get(0).getShopId(), userId);
    }

    private ShoppingCartData getLatestShoppingCartDataByUserIdShopId(long shopId, long userId) {
        List<ShoppingCartData> resultRows = shoppingCartQueryMapper.selectShoppingCartDataByUserIdShopId(userId, shopId);
        return merge(resultRows);
    }

    private ShoppingCart toShoppingCartRow(ShoppingCartController.AddToShoppingCartItem item,
                                           Map<Long, Goods> idToGoodsMap) {

        Goods goods = idToGoodsMap.get(item.getId());
        if (goods == null) {
            return null;
        }

        ShoppingCart result = new ShoppingCart();
        result.setGoodsId(item.getId());
        result.setNumber(item.getNumber());
        result.setUserId(UserContext.getCurrentUser().getId());
        result.setShopId(goods.getShopId());
        result.setStatus(DataStatus.OK.toString().toLowerCase());
        result.setCreatedAt(new Date());
        result.setUpdatedAt(new Date());
        return result;
    }

    public ShoppingCartData deleteGoodsInShoppingCart(long goodsId, long userId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        if (goods == null) {
            throw HttpException.notFound("商品未找到：" + goodsId);
        }
        shoppingCartQueryMapper.deleteShoppingCart(goodsId, userId);
        return getLatestShoppingCartDataByUserIdShopId(goods.getShopId(), userId);
    }
}
