package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse<Integer> add(Integer userId,Shipping shipping) {
        shipping.setUserId(userId);
        int insert = shippingMapper.insert(shipping);
        if(insert>0){
            return ServerResponse.createBySuccess(shipping.getId());
        }
        return ServerResponse.createByErrorMessage("收货地址插入失败");
    }

    @Override
    public ServerResponse<String> del(Integer userId,Integer id) {
        int deleteCount = shippingMapper.deleteByPrimaryKey(userId,id);
        if(deleteCount>0){
            return ServerResponse.createBySuccess("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }


    @Override
    public ServerResponse<String> update(Integer id, Shipping shipping) {
        shipping.setUserId(id);
        int count = shippingMapper.updateByPrimaryKey(shipping);
        if(count>0){
            return ServerResponse.createBySuccess("更新成功");
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer id) {
        Shipping sh  = shippingMapper.selectByPrimaryKey(userId,id);
        if(sh != null){
            return ServerResponse.createBySuccess(sh);
        }
        return ServerResponse.createByErrorMessage("查询失败");
    }

    @Override
    public ServerResponse<PageInfo> list(Integer id, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(id);
        PageInfo pageInfo = new PageInfo(shippingList);
        pageInfo.setList(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}




