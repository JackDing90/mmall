package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Administrator on 2017/5/5.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    public UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int count = userMapper.checkUserName(username);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> srUserName = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!srUserName.isSucess()) {
            return srUserName;
        }
        ServerResponse<String> srEmail = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!srEmail.isSucess()) {
            return srEmail;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int insert = userMapper.insert(user);
        if (insert > 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNoneBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int checkUserName = userMapper.checkUserName(str);
                if (checkUserName > 0) {
                    return ServerResponse.createByErrorMessage("用户名己存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int checkEmail = userMapper.checkEmail(str);
                if (checkEmail > 0) {
                   return ServerResponse.createByErrorMessage("邮箱己被注册");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("检验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSucess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestion(username);
        if (StringUtils.isNoneBlank(question)) {
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("找回密码是空的");
    }

    @Override
    public ServerResponse<String> answerQuestion(String username, String question, String answer) {
        int answerQuestion = userMapper.answerQuestion(username, question, answer);
        if (answerQuestion > 0) {
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey("token_" + username, forgetToken);
            return ServerResponse.createBySucess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("密保问题验证失败");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSucess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("前端传过来的token无效");
        }
        String cacheToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(cacheToken)){
            return ServerResponse.createByErrorMessage("缓存中的token无效");
        }
        if(StringUtils.equals(cacheToken,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int count = userMapper.updatePasswordByUsername(username, md5Password);
            if(count>0){
                return ServerResponse.createBySucess("重置密码成功 ");
            }
        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordNew,String passwordOld,User user){
        String oldMd5Password = MD5Util.MD5EncodeUtf8(passwordOld);
        int oldCount = userMapper.checkPassword(user.getId(), oldMd5Password);
        if(oldCount==0) {
            return ServerResponse.createByErrorMessage("用户密码不正确");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int newCount = userMapper.updateByPrimaryKeySelective(user);
        if(newCount==1){
            return ServerResponse.createBySucess("重置密码成功");
        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user){
        int count = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
        if(count>0){
            ServerResponse.createByErrorMessage("email已经被占用");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userid){
        User user = userMapper.selectByPrimaryKey(userid);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySucess(user);
    }

    @Override
    public ServerResponse<String> checkAdminRole(User user){
        if(user.getRole().intValue()== Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }else{
            return ServerResponse.createByError();
        }
    }
}
