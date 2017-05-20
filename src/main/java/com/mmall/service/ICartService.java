package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * Created by Administrator on 2017/5/18.
 */
public interface ICartService {
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> update(Integer userIdFromSession, Integer productId, Integer count);
    ServerResponse<CartVo> deleteProduct(Integer userIdFromSession, String productId);
    ServerResponse<CartVo> checkSelectStatus(Integer userIdFromSession,Integer productId);
    ServerResponse<CartVo> checkUnSelectStatus(Integer userIdFromSession,Integer productId);
    ServerResponse<Integer> getCartProductCount(Integer userIdFromSession);
    ServerResponse<CartVo> list(Integer userId);


}
