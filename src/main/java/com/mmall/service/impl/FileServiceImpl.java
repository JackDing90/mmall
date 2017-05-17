package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.service.IFileService;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/5/15.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public Map upload(MultipartFile multipartFile , String path){
        Map voMap = null;
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtendName = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uploadFileName = UUID.randomUUID().toString()+fileExtendName;
        logger.info("原始文件台为{},上传路径为{},上传的文件名为{}",originalFilename,path,uploadFileName);
        File file = new File(path,uploadFileName);
        if(!file.exists()){
            file.setWritable(true);
            file.mkdirs();
        }
        //将文件上传至项目
        try {
            multipartFile.transferTo(file);
            //将项目的图片上传至FTP服务器
            List list = Lists.newArrayList();
            list.add(file);
            boolean result = FtpUtil.uploadFile(list);
            if(result){
                voMap = Maps.newHashMap();
                String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+file.getName();
                voMap.put("uri",file.getName());
                voMap.put("url",url);
            }
            file.delete();
        } catch (IOException e) {
            logger.error("文件上传异常");
            return  null;
        }
        return voMap;
    }
}
