package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/5/10.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if(parentId==null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int count = categoryMapper.insert(category);
        if(count>0){
            return ServerResponse.createBySuccessMessage("添加分类成功");
        }
            return ServerResponse.createByErrorMessage("添加分类失败");
    }

    @Override
    public ServerResponse<String> updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId==null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int count = categoryMapper.updateByPrimaryKeySelective(category);
        if(count>0){
            return ServerResponse.createBySuccessMessage("修改品类名字成功");
        }
        return ServerResponse.createByErrorMessage("修改品类名字失败");
    }

    @Override
    public ServerResponse getChildrenParallelCategory(Integer categoryId){
        if(categoryId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categories)){
            return ServerResponse.createByErrorMessage("获取品类子节点失败");
        }
        return ServerResponse.createBySucess(categories);
    }

    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> hashSet = Sets.newHashSet();
        findChildCategory(hashSet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId!=null){
            for(Category categoryItem2:hashSet){
                categoryIdList.add(categoryItem2.getId());
            }
        }
        return ServerResponse.createBySucess(categoryIdList);
    }

    //递归算法
    private Set<Category> findChildCategory(Set<Category> categories ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categories.add(category);
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem:categoryList){
            findChildCategory(categories,categoryItem.getId());
        }
        return categories;
    }
}
