package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count){
        Cart fromDbCart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(fromDbCart==null){
            Cart newCart = new Cart();
            newCart.setQuantity(count);
            newCart.setUserId(userId);
            newCart.setProductId(productId);
            newCart.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(newCart);
        }else{
            fromDbCart.setQuantity(fromDbCart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(fromDbCart);
        }
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userIdFromSession, Integer productId, Integer count){
        if(productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userIdFromSession, productId);
        if(cart!=null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }else {
            return ServerResponse.createByErrorMessage("购物车为空，更新失败");
        }
        return this.list(userIdFromSession);
    }

   @Override
   public ServerResponse<CartVo> deleteProduct(Integer userIdFromSession, String productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List productIdList = Splitter.on(",").splitToList(productId);
        int resultCount = cartMapper.deleteByUserIdProductId(userIdFromSession, productIdList);
        if(resultCount>0){
            return this.list(userIdFromSession);
        }else{
            return ServerResponse.createByErrorMessage("删除购物车商品失败");
        }
    }

    @Override
    public ServerResponse<CartVo> checkSelectStatus(Integer userIdFromSession,Integer productId){
        int selectCount = cartMapper.updateCheckSelectOrUnselct(userIdFromSession, Const.Cart.CHECKED, productId);
        if(selectCount>0){
            return this.list(userIdFromSession);
        }else{
            return ServerResponse.createByErrorMessage("全选或单选操作失败");
        }
    }

    @Override
    public ServerResponse<CartVo> checkUnSelectStatus(Integer userIdFromSession,Integer productId){
        int unSelectCount = cartMapper.updateCheckSelectOrUnselct(userIdFromSession, Const.Cart.UN_CHECKED, productId);
        if(unSelectCount>0){
            return this.list(userIdFromSession);
        }else{
            return ServerResponse.createByErrorMessage("全反选或反单选操作失败");
        }
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userIdFromSession){
        Integer sum = cartMapper.getCartProductCount(userIdFromSession);
        return ServerResponse.createBySuccess(sum);
    }


    @Override
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVoLimit = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVoLimit);
    }


    /**
     * 1.pojo转换vo
     * 2.减少库存或者说更新库存
     * 3.计算购物车中某个商品的价格和购物车的总价格
     * @param userId
     * @return
     */
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartListFromDb = cartMapper.selectCartByUserId(userId);//查询当前用户的当前购物车情况
        List cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");//购物车的总价格
        if(CollectionUtils.isNotEmpty(cartListFromDb)){
            //解析从后端查询到的数据到前端
            for(Cart cartItem:cartListFromDb){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());//根据pojo cart当中的productId去查询pojo product
                if(product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());///
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int stockLimit = 0;
                    if(product.getStock()>=cartItem.getQuantity()){//商品的总库存大于等于购物车中的需要的数量时
                        stockLimit = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        stockLimit = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(product.getId());
                        cartForQuantity.setQuantity(stockLimit);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(stockLimit);
                    //计算购物车中某个商品的总价格
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.multiply(stockLimit,product.getPrice().doubleValue()));
                    cartProductVo.setProductCheck(cartItem.getChecked());
                }
                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //计算购物车的总价格
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllchecked(this.getAllCheckedStatus(userId));//判断是不是全选状态
        cartVo.setImagHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
       return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckStatusByUserId(userId)>0;
    }



}
