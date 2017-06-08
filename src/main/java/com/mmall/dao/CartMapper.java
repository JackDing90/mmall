package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int selectCartProductCheckStatusByUserId(Integer userId);

    int deleteByUserIdProductId(@Param("userId") Integer userIdFromSession, @Param("productIdList") List productIdList);

    int updateCheckSelectOrUnselct(@Param("userId") Integer userIdFromSession, @Param("checked") int checked, @Param("productId") Integer productId);

    int getCartProductCount(Integer userId);

    List<Cart> selectCheckCartByUserId(Integer userId);
}