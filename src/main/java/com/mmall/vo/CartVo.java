package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */
public class CartVo {
    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice;
    private boolean allchecked;//是否已经全部勾选
    private String imagHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isAllchecked() {
        return allchecked;
    }

    public void setAllchecked(boolean allchecked) {
        this.allchecked = allchecked;
    }

    public String getImagHost() {
        return imagHost;
    }

    public void setImagHost(String imagHost) {
        this.imagHost = imagHost;
    }
}
