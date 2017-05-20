package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/5/18.
 */

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("/add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer productId, Integer count) {
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer userIdFromSession = sessionUser.getId();
        return iCartService.add(userIdFromSession, productId, count);
    }

    @RequestMapping("/update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer productId, Integer count) {
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer userIdFromSession = sessionUser.getId();
        return iCartService.update(userIdFromSession, productId, count);
    }

    @RequestMapping("/delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productId) {
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer userIdFromSession = sessionUser.getId();
        return iCartService.deleteProduct(userIdFromSession, productId);
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session) {
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer userIdFromSession = sessionUser.getId();
        return iCartService.list(userIdFromSession);
    }


    @RequestMapping("/select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer userIdFromSession = sessionUser.getId();
        return iCartService.checkSelectStatus(userIdFromSession,null);
    }

    @RequestMapping("/un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer userIdFromSession = sessionUser.getId();
        return iCartService.checkUnSelectStatus(userIdFromSession,null);
    }

    @RequestMapping("/select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session,Integer productId){
        if(productId==null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer userIdFromSession = sessionUser.getId();
        return iCartService.checkSelectStatus(userIdFromSession,productId);
    }

    @RequestMapping("/un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
        if(productId==null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer userIdFromSession = sessionUser.getId();
        return iCartService.checkUnSelectStatus(userIdFromSession,productId);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        Integer userIdFromSession = sessionUser.getId();
        return iCartService.getCartProductCount(userIdFromSession);
    }


}
