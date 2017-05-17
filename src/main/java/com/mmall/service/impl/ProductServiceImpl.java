package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        String subImages = product.getSubImages();
        if (StringUtils.isNotBlank(subImages)) {
            String[] split = subImages.split(",");
            product.setMainImage(split[0]);
        }
        if (product.getId() == null) {
            int insertCount = productMapper.insert(product);
            if (insertCount > 0) {
                return ServerResponse.createBySuccessMessage("新增产品成功");
            }else{
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        } else {
            int updateCount = productMapper.updateByPrimaryKeySelective(product);
            if (updateCount > 0) {
                return ServerResponse.createBySuccessMessage("更新产品成功");
            }else{
                return ServerResponse.createByErrorMessage("更新的产品失败");
            }
        }

    }

    @Override
    public ServerResponse setSaleStatus(Integer id,Integer status){
        if(id==null||status==null){
           return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product p = new Product();
        p.setId(id);
        p.setStatus(status);
        int count = productMapper.updateByPrimaryKeySelective(p);
        if(count>0){
            return ServerResponse.createBySuccessMessage("设置商品上下架成功");
        }
        return ServerResponse.createByErrorMessage(" 设置商品上下架失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> managerProductDetail(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("产品己下架或者已经删除");
        }
        ProductDetailVo detailVo = assembleProductDetailVO(product);
        return ServerResponse.createBySucess(detailVo);
    }

    private ProductDetailVo assembleProductDetailVO(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImange(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        Category category = categoryMapper.selectByPrimaryKey(product.getId());
        if(category==null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> getList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectList();
        if(products==null){
            return ServerResponse.createByErrorMessage("无商品信息");
        }
        PageInfo pageinfo = new PageInfo(products);
        List<ProductListVo> productListVos = assembleProductListVO(products);
        pageinfo.setList(productListVos);
        return ServerResponse.createBySucess(pageinfo);
    }

    private List<ProductListVo> assembleProductListVO(List<Product> products){
        List<ProductListVo> productListVos = Lists.newArrayList();
        for(Product product:products){
            ProductListVo productListVo = new ProductListVo();
            productListVo.setId(product.getId());
            productListVo.setName(product.getName());
            productListVo.setCategoryId(product.getCategoryId());
            productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
            productListVo.setMainImage(product.getMainImage());
            productListVo.setPrice(product.getPrice());
            productListVo.setSubtitle(product.getSubtitle());
            productListVo.setStatus(product.getStatus());
            productListVos.add(productListVo);
        }
        return productListVos;
    }

    @Override
    public ServerResponse<PageInfo> searchByNameAndProductId(String productName,Integer productId,int pageNum,int pageSize){
        if(StringUtils.isBlank(productName)&&productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数传递错误");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.searchByNameAndProductId(productName,productId);
        if(products==null){
            return ServerResponse.createByErrorMessage("无商品信息");
        }
        PageInfo pageinfo = new PageInfo(products);
        List<ProductListVo> productListVos = assembleProductListVO(products);
        pageinfo.setList(productListVos);
        return ServerResponse.createBySucess(pageinfo);
    }

    @Override
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("商品已经下架或者不存在");
        }else{
            if(product.getStatus()== Const.ProductStatusEnum.ON_SALE.getCode()){
                ProductDetailVo productDetailVo = assembleProductDetailVO(product);
                return ServerResponse.createBySucess(productDetailVo);
            }else{
                return ServerResponse.createByErrorMessage("商品不在销售状态");
            }
        }

    }

    @Override
    public ServerResponse<PageInfo> getProductNameAndCategoryId(String productName, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        List categoryList = Lists.newArrayList();
        PageInfo pageInfo = new PageInfo();
        if(StringUtils.isBlank(productName)&&categoryId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        }else {
            if(categoryId!=null){
                Category category = categoryMapper.selectByPrimaryKey(categoryId);
                if(category==null){
                    //返回一个空的pageInfo到前台，不报错
                    return ServerResponse.createBySucess(pageInfo);
                }else{
                    PageHelper.startPage(pageNum,pageSize);
                    //查询分类子目录
                    List<Integer> categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
                    //组装参数
                    productName = new StringBuilder().append("%").append(productName).append("%").toString();
                    //orderBy排序
                    if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                        String[] cateArray =orderBy.split("_");
                        pageInfo.setOrderBy(cateArray[0]+" "+cateArray[1]);
                    }
                    pageInfo.setList(categoryList);
                    List<Product> productList = productMapper.getProductNameAndCategoryId(productName, categoryIdList);
                    List<ProductListVo> productListVos = assembleProductListVO(productList);
                    pageInfo.setList(productListVos);
                    return ServerResponse.createBySucess(pageInfo);
                }
            }
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
