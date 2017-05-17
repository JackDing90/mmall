package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 */
public class FtpUtil {

    private static Logger logger = LoggerFactory.getLogger(FtpUtil.class);
    private static final String  FTP_SERVER_IP = PropertiesUtil.getProperty("ftp.server.ip");
    private static final String  FTP_USER = PropertiesUtil.getProperty("ftp.user");
    private static final String  FTP_PASS = PropertiesUtil.getProperty("ftp.pass");


    public FtpUtil(String ip,Integer port,String userName,String passWord){
        this.ip= ip;
        this.port = port;
        this.userName = userName;
        this.passWord = passWord;
    }
    public static boolean uploadFile(List<File> files) throws IOException {
        FtpUtil ftpUtil = new FtpUtil(FTP_SERVER_IP,21,FTP_USER,FTP_PASS);
        logger.info("开始连接FTP服务器");
        boolean result = ftpUtil.uploadFile("img", files);
        logger.info("开始连接FTP服务器，开始上传，上传结果为{}");
        return result;
    }

    private boolean uploadFile(String romateFile,List<File> files) throws IOException {
        boolean uploaded = false;
        FileInputStream fileInputStream = null;
        if(connectFtpServer(this.ip,this.port,this.userName,this.passWord)){
            uploaded = true;
            try {
                ftpClient.changeWorkingDirectory(romateFile);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem:files){
                    fileInputStream = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fileInputStream);
                }
            } catch (IOException e) {
                logger.error("文件上传异常",e);
                e.printStackTrace();
            }finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    public boolean connectFtpServer(String ip,Integer port,String userName,String passWord){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(userName, passWord);
        } catch (IOException e) {
            logger.error("ftp连接失败");
        }
        return isSuccess;
    }

    private String ip;
    private Integer port;
    private String userName;
    private String passWord;
    private FTPClient ftpClient;

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
