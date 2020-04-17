package com.dahuntun.wxshop.dao;

import com.dahuntun.wxshop.generate.Goods;
import com.dahuntun.wxshop.generate.GoodsMapper;
import com.dahuntun.wxshop.generate.User;
import com.dahuntun.wxshop.generate.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GoodsDAO {
    private final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public GoodsDAO(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public Goods insertGoods(Goods goods) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            GoodsMapper goodsMapper = sqlSession.getMapper(GoodsMapper.class);
            long id = goodsMapper.insert(goods);
            goods.setId(id);
            return goods;
        }
    }
}
