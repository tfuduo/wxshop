package com.dahuntun.api.generate;

import java.io.Serializable;

public class OrderGoods implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_GOODS.ID
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_GOODS.GOODS_ID
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    private Long goodsId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_GOODS.NUMBER
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    private Long number;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table ORDER_GOODS
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_GOODS.ID
     *
     * @return the value of ORDER_GOODS.ID
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_GOODS.ID
     *
     * @param id the value for ORDER_GOODS.ID
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_GOODS.GOODS_ID
     *
     * @return the value of ORDER_GOODS.GOODS_ID
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    public Long getGoodsId() {
        return goodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_GOODS.GOODS_ID
     *
     * @param goodsId the value for ORDER_GOODS.GOODS_ID
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_GOODS.NUMBER
     *
     * @return the value of ORDER_GOODS.NUMBER
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    public Long getNumber() {
        return number;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_GOODS.NUMBER
     *
     * @param number the value for ORDER_GOODS.NUMBER
     *
     * @mbg.generated Sat May 09 03:22:24 CST 2020
     */
    public void setNumber(Long number) {
        this.number = number;
    }
}