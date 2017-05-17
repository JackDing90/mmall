package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by Administrator on 2017/5/11.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse setSaleStatus(Integer id,Integer status);
    ServerResponse managerProductDetail(Integer productId);
    ServerResponse<PageInfo> getList(int pageNum, int pageSize);
    ServerResponse<PageInfo> searchByNameAndProductId(String productName,Integer productId,int pageNum,int pageSize);
    ServerResponse<ProductDetailVo> detail(Integer productId);
    ServerResponse<PageInfo> getProductNameAndCategoryId(String productName, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
