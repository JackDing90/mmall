package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by Administrator on 2017/5/5.
 */
public interface IUserService {
    ServerResponse<User> login (String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse<String> selectQuestion(String username);
    ServerResponse<String> answerQuestion(String username,String password,String answer);
    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);
    ServerResponse<String> resetPassword(String passwordNew,String passwordOld,User user);
    ServerResponse<User> updateInformation(User user);
    ServerResponse<User> getInformation(Integer userid);
    ServerResponse<String> checkAdminRole(User user);
    }
