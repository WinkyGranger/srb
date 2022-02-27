package com.winky.srb.oss.controller.api;

import com.winky.common.exception.BusinessException;
import com.winky.common.result.R;
import com.winky.common.result.ResponseEnum;
import com.winky.srb.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @auther Li Wenjie
 * @create 2022-02-23 12:18
 */

@RestController()
@RequestMapping("/api/oss/file")
@Api(tags = "阿里云文件管理")
//@CrossOrigin //跨域
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public R upload(@ApiParam(value = "文件",required = true)
                    @RequestParam("file")
                    MultipartFile file,
                    @ApiParam(value = "模块",required = true)
                    @RequestParam("module") String module){
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String url = fileService.upload(inputStream, module, originalFilename);
            return R.ok().message("文件上传成功").data("url",url);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }

    @DeleteMapping("/remove")
    @ApiOperation("删除文件")
    public R remove(@ApiParam(value = "要删除的文件",required = true)
                    @RequestParam("url") String url){
        fileService.remove(url);
        return R.ok().message("删除成功");
    }

}
