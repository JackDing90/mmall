package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by Administrator on 2017/5/15.
 */
public interface IFileService {
    Map upload(MultipartFile multipartFile , String path);
}
