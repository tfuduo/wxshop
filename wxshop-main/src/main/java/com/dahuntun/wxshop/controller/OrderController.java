package com.dahuntun.wxshop.controller;

import com.dahuntun.api.DataStatus;
import com.dahuntun.api.data.OrderInfo;
import com.dahuntun.api.generate.Order;
import com.dahuntun.wxshop.entity.HttpException;
import com.dahuntun.wxshop.entity.OrderResponse;
import com.dahuntun.api.data.PageResponse;
import com.dahuntun.wxshop.entity.Response;
import com.dahuntun.wxshop.service.OrderService;
import com.dahuntun.wxshop.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // @formatter:off
    /**
     * @api {get} /order 获取当前用户名下的所有订单
     * @apiName GetOrder
     * @apiGroup 订单
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiParam {Number} pageNum 页数，从1开始
     * @apiParam {Number} pageSize 每页显示的数量
     * @apiParam {String=PENDING/PAID/DELIVERED/RECEIVED} [status] 订单状态：PENDING 待付款 PAID 已付款 DELIVERED 物流中 RECEIVED 已收货
     *
     * @apiSuccess {Number} pageNum 页数，从1开始
     * @apiSuccess {Number} pageSize 每页显示的数量
     * @apiSuccess {Number} totalPage 共有多少页
     * @apiSuccess {Order} data 订单列表
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "pageNum": 1,
     *       "pageSize": 10,
     *       "totalPage": 5,
     *       "data": [
     *          {
     *           "id": 12345,
     *           "expressCompany": null,
     *           "expressId": null,
     *           "status": "PENDING",
     *           "totalPrice": 10000,
     *           "address": "XXX",
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "address": "XXX",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         },
     *         {
     *              ...
     *         }
     *       ]
     *     }
     *
     * @apiError 401 Unauthorized 若用户未登录
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    // @formatter:on

    /**
     *
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @param status 订单状态
     * @return 响应
     */
    @GetMapping("/order")
    public PageResponse<OrderResponse> getOrder(@RequestParam("pageNum") Integer pageNum,
                                                @RequestParam("pageSize") Integer pageSize,
                                                @RequestParam(value = "status", required = false) String status) {
        if (status != null && DataStatus.fromStatus(status) == null) {
            throw HttpException.badRequest("非法status: " + status);
        }

        return orderService.getOrder(UserContext.getCurrentUser().getId(), pageNum, pageSize, DataStatus.fromStatus(status));
    }


    // @formatter:off
    /**
     * @api {post} /order 下订单
     * @apiName CreateOrder
     * @apiGroup 订单
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     * @apiParamExample {json} Request-Example:
     *            {
     *              "goods": [
     *                {
     *                    "id": 12345,
     *                    "number": 10,
     *                },
     *                {
     *                    ...
     *                }
     *            }
     *
     * @apiSuccess {Order} data 刚刚创建完成的订单
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 201 Created
     *     {
     *       "data": {
     *           "id": 12345,
     *           "expressCompany": null,
     *           "expressId": null,
     *           "status": "PENDING",
     *           "address": "XXX",
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "address": "XXX",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         }
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求中包含错误
     * @apiError 401 Unauthorized 若用户未登录
     * @apiError 404 Not Found 若商品未找到
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 400 Bad Request
     *     {
     *       "message": "商品已经售完"
     *     }
     */
    // @formatter:on

    /**
     * @param orderInfo 订单信息
     * @return 响应
     */
    @PostMapping("/order")
    public Response<OrderResponse> createOrder(@RequestBody OrderInfo orderInfo) {
        orderService.deductStock(orderInfo);
        return Response.of(orderService.createOrder(orderInfo, UserContext.getCurrentUser().getId()));
    }

    // @formatter:off
    /**
     * @api {PATCH} /order/:id 更新订单(只能更新物流信息/签收状态)
     * @apiName UpdateOrder
     * @apiGroup 订单
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     *
     * @apiParam {Number} id 订单ID
     * @apiParamExample {json} Request-Example:
     *          {
     *              "id": 12345,
     *              "expressCompany": "圆通",
     *              "expressId": "YTO1234",
     *          }
     *          {
     *              "id": 12345,
     *              "status": "RECEIVED"
     *          }
     *
     *
     * @apiSuccess {Order} data 更新后的订单
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "data": {
     *           "id": 12345,
     *           "expressCompany": null,
     *           "expressId": null,
     *           "status": "PENDING",
     *           "address": "XXX",
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "address": "XXX",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         }
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求中包含错误
     * @apiError 401 Unauthorized 若用户未登录
     * @apiError 403 Forbidden 若用户修改非自己店铺的订单
     * @apiError 404 Not Found 若订单未找到
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 404 Not Found
     *     {
     *       "message": "Not Found"
     *     }
     */
    // @formatter:on

    /**
     *
     * @param id 订单id
     * @param order 更新的订单
     * @return 响应
     */
    @RequestMapping(value = "/order/{id}", method = {RequestMethod.POST, RequestMethod.PATCH})
    public Response<OrderResponse> updateOrder(@PathVariable("id") long id, @RequestBody Order order) {
        order.setId(id);
        if (order.getExpressCompany() != null) {
            return Response.of(orderService.updateExpressInformation(order, UserContext.getCurrentUser().getId()));
        } else {
            return Response.of(orderService.updateOrderStatus(order, UserContext.getCurrentUser().getId()));
        }
    }

    // @formatter:off
    /**
     * @api {DELETE} /order/:id 删除订单
     * @apiName DeleteOrder
     * @apiGroup 订单
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiSuccess {Order} data 刚刚删除的订单
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 204 No Content
     *     {
     *       "data": {
     *           "id": 12345,
     *           "expressCompany": null,
     *           "expressId": null,
     *           "status": "PENDING",
     *           "address": "XXX",
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "address": "XXX",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         }
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求中包含错误
     * @apiError 401 Unauthorized 若用户未登录
     * @apiError 403 Forbidden 若用户删除非自己订单
     * @apiError 404 Not Found 若订单未找到
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 404 Not Found
     *     {
     *       "message": "Not Found"
     *     }
     */
    // @formatter:on

    /**
     *
     * @param orderId 订单id
     * @return 删除订单的响应
     */
    @DeleteMapping("/order/{id}")
    public Response<OrderResponse> deleteOrder(@PathVariable("id") long orderId) {
        return Response.of(orderService.deleteOrder(orderId, UserContext.getCurrentUser().getId()));
    }
}
