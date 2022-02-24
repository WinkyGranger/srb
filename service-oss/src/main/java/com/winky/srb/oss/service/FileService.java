package com.winky.srb.oss.service;

import java.io.InputStream;

/**
 * @auther Li Wenjie
 * @create 2022-02-23 10:30
 */
public interface FileService {

    /**
     * 文件上传到阿里云
     * @param inputStream 上传信息流
     * @param module 一个文件夹命名的模块，例如身份证，房产证等等
     * @param fileName 文件命名
     * @return
     */
    String upload(InputStream inputStream,String module,String fileName);

    void remove(String url);
}
