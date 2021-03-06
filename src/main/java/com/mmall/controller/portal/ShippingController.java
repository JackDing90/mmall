package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/20.
 */
@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping(value = "/add.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Integer> add(HttpSession session , Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.add(user.getId(),shipping);
    }

    @RequestMapping(value = "/del.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> del(HttpSession session , Integer id){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.del(user.getId(),id);
    }

    @ModelAttribute
    public void getShipping(@RequestParam(value = "id",required = false) Integer id,
                            HttpSession session,
                            Map<String,Object> map){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        if(id!=null){
            ServerResponse<Shipping> select = iShippingService.select(user.getId(),id);
            Shipping shipping=select.getData();
            map.put("shipping",shipping);
        }
    }

    @RequestMapping(value = "/update.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> update(HttpSession session , Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping(value = "/select.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session , Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping(value = "/list.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session ,
                                         @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.list(user.getId(),pageNum,pageSize);
    }




}
