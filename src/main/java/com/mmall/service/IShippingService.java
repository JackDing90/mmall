package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * Created by Administrator on 2017/5/20.
 */
public interface IShippingService {
     ServerResponse add(Integer userId,Shipping shipping);
     ServerResponse<String> del(Integer userId,Integer id);
     ServerResponse<String> update(Integer id, Shipping shipping);
     ServerResponse<Shipping> select(Integer id, Integer shippingId);
     ServerResponse<PageInfo> list(Integer id, Integer pageNum, Integer pageSize);
}
