package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/11.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     *新增OR更新产品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "/save.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse saveOrUpdateProduct(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，请登录后重试");
        }else{
            if (iUserService.checkAdminRole(user).isSuccess()) {
                return iProductService.saveOrUpdateProduct(product);
            } else {
                return ServerResponse.createByErrorMessage("无管理员权限");
            }
        }
    }

    /**
     * 产品上下架
     * @param session
     * @return
     */
    @RequestMapping(value = "/set_sale_status.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，请登录后重试");
        }else{
            if (iUserService.checkAdminRole(user).isSuccess()) {
                return iProductService.setSaleStatus(productId,status);
            } else {
                return ServerResponse.createByErrorMessage("无管理员权限");
            }
        }
    }

    /**
     * 获取产品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "/detail.do", method=RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，请登录后重试");
        }else{
            if (iUserService.checkAdminRole(user).isSuccess()) {
                return iProductService.managerProductDetail(productId);
            } else {
                return ServerResponse.createByErrorMessage("无管理员权限");
            }
        }
    }

    /**
     * 获取产品列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list.do",method=RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue="1") int pageNum, @RequestParam(value = "pageSize",defaultValue="10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，请登录后重试");
        }else{
            if (iUserService.checkAdminRole(user).isSuccess()) {
                return iProductService.getList(pageNum,pageSize);
            } else {
                return ServerResponse.createByErrorMessage("无管理员权限");
            }
        }
    }

    @RequestMapping(value = "/search.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> searchByNameAndProductId(HttpSession session, @RequestParam(value = "productName",required = false) String productName,
                                                             @RequestParam(value = "productId",required = false) Integer productId,
                                                             @RequestParam(value = "pageNum",defaultValue="1") int pageNum,
                                                             @RequestParam(value = "pageSize",defaultValue="10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录后再试");
        }else{
            if (iUserService.checkAdminRole(user).isSuccess()) {
                return iProductService.searchByNameAndProductId(productName,productId,pageNum,pageSize);
            } else {
                return ServerResponse.createByErrorMessage("无管理员权限");
            }
        }
    }

    @RequestMapping(value = "/upload.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse uploadFile(HttpSession session,@RequestParam(value="upload_file",required = false) MultipartFile multipartFile, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录后再试");
        }else{
            if (iUserService.checkAdminRole(user).isSuccess()) {
                String realPath = request.getSession().getServletContext().getRealPath("upload");
                Map map = iFileService.upload(multipartFile, realPath);
                if(map == null){
                    return ServerResponse.createByErrorMessage("上传失败");
                }
                return ServerResponse.createBySuccess(map);
            }else{
                return ServerResponse.createByErrorMessage("无管理员权限");
            }
        }
    }

    @RequestMapping(value = "/richTestUpload.do",method=RequestMethod.POST)
    @ResponseBody
    public Map richTextUploadFile(HttpSession session, @RequestParam(value="upload_file",required = false) MultipartFile multipartFile, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }else{
            if (iUserService.checkAdminRole(user).isSuccess()) {
                String realPath = request.getSession().getServletContext().getRealPath("upload");
                Map uploadMap = iFileService.upload(multipartFile, realPath);
                if(uploadMap==null){
                    resultMap.put("success",false);
                    resultMap.put("msg","上传失败");
                    return resultMap;
                }
                String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+ uploadMap.get("uri");
                resultMap.put("success",true);
                resultMap.put("msg","上传成功");
                resultMap.put("file_path",url);
                response.addHeader("Access-Control-Allow-Headers","X-File_Name");
                return resultMap;
            }else{
                resultMap.put("success",false);
                resultMap.put("msg","无权限操作");
                return resultMap;
            }
        }
    }
}
