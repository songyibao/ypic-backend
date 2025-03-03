package com.syb.ypic.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.syb.ypic.annotation.AuthCheck;
import com.syb.ypic.common.BaseResponse;
import com.syb.ypic.common.ResultUtils;
import com.syb.ypic.constant.UserConstant;
import com.syb.ypic.exception.BusinessException;
import com.syb.ypic.exception.ErrorCode;
import com.syb.ypic.exception.ThrowUtils;
import com.syb.ypic.manager.CosManager;
import com.syb.ypic.model.enums.UserRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;

    @AuthCheck(mustRole = UserConstant.ADMIN)
    @PostMapping("/test/upload")
    public BaseResponse testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", filename);
        File file = null;
        try {
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error,filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.error("delete file error,filepath = " + filepath);
                }
            }
        }
    }

    @AuthCheck(mustRole = UserConstant.ADMIN)
    @GetMapping("/test/download")
    public void testDownloadFile(String filepath, HttpServletResponse response) {
        COSObjectInputStream cosObjectInputStream = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInputStream = cosObject.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(cosObjectInputStream);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error,filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInputStream != null) {
                try {
                    cosObjectInputStream.close();
                } catch (Exception e) {
                    log.error("close cosObjectInputStream error", e);
                }
            }
        }

    }

}
