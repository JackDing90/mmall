package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/5/5.
 */

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse response = iUserService.login(username,password);
        if(response.isSucess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
         return response;
    }

    /**
     * 用户登出
     * @param session
     * @return
     */
    @RequestMapping(value="logout.do", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value="register.do", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 注册检验
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value="check_valid.do", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> CheckValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value="get_user_info.do", method= RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServerResponse.createBySucess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，请登录后再试");
    }

    /**
     * 找回密码
     * @param username
     * @return
     */
    @RequestMapping(value="forget_get_question.do", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 验证密保
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value="forget_check_answer.do", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.answerQuestion(username,question,answer);
    }

    /**
     * 忘记密码状态下的重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value="forget_reset_password.do", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>  forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    /**
     * 登录状态下的重置密码
     * @param session
     * @param passwordNew
     * @param passwordOld
     * @return
     */
    @RequestMapping(value="reset_password.do", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordNew,String passwordOld){
       User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordNew,passwordOld,user);
    }

    /**
     * 更新用户信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value="update_information.do", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        User cuurUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(cuurUser == null) {
            return ServerResponse.createByErrorMessage("请登录后重试");
        }
        user.setId(cuurUser.getId());
        ServerResponse<User> userServerResponse = iUserService.updateInformation(user);
        if(userServerResponse.isSucess()){
            session.setAttribute(Const.CURRENT_USER,userServerResponse.getData());
        }
        return userServerResponse;
    }

    /**
     * 获取用户详细信息
     * @param session
     * @return
     */
    @RequestMapping(value="get_information.do", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User cuurUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(cuurUser == null) {
            return ServerResponse.createByErrorMessage("请登录后重试");
        }
        return iUserService.getInformation(cuurUser.getId());
    }
}
