package org.poem.controller;


import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.poem.config.SystemEnums;
import org.poem.services.MinioService;
import org.poem.utils.Byte2InputStreamUtils;
import org.poem.utils.SnowflakeIdWorker;
import org.poem.vo.BaseResponse;
import org.poem.vo.FileUploadRespVo;
import org.poem.vo.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author sangfor
 */
@RestController
@RequestMapping("/v1/file")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private MinioService minioService;


    @RequestMapping(value = "/download/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> downloadImage(@PathVariable("fileId") String fileId) {
        if (fileId == null) {
            return null;
        }
        try {
            HttpHeaders heads = new HttpHeaders();
            heads.add("Content-Disposition", "attachment; filename=\"" + new String(fileId.getBytes(StandardCharsets.UTF_8), "ISO8859-1") + "\"");
            InputStream inputStream = minioService.get(fileId);
            byte[] filebyte = Byte2InputStreamUtils.inputStream2byte(inputStream);
            logger.info("Down File fileId:" + fileId);
            ResponseEntity<byte[]> result = new ResponseEntity<byte[]>(filebyte, heads, HttpStatus.OK);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param fileVoList
     * @return
     */

    public BaseResponse<List<FileUploadRespVo>> saveFile(List<FileVO> fileVoList) throws Exception {
        List<FileUploadRespVo> respVoList = new ArrayList<>();
        for (FileVO fileVo : fileVoList) {
            if (fileVo.getData() == null || fileVo.getDataLength() == 0) {
                continue;
            }
            InputStream data = fileVo.getData();
            String fileName = fileVo.getName();
            //文件上传时的文件名需要支持常见的特殊字符
            String fileType = FilenameUtils.getExtension(fileName);
            String filePath = fileName + "-" + RandomStringUtils.randomAlphanumeric(6) + "." + fileType;
            try {
                this.minioService.upload(filePath, data);
                Long fileId = fileVo.getFileId();
                if (fileId == null) {
                    fileId = SnowflakeIdWorker.generateId();
                }

                FileUploadRespVo respVo = new FileUploadRespVo();
                respVo.setFileId(fileId);
                respVo.setFilePath(filePath);
                respVo.setFileType(fileType);
                respVo.setName(fileName);
                respVoList.add(respVo);
            } catch (Exception e) {
                this.minioService.remove(filePath);
                throw new Exception(e);
            }
        }
        return new BaseResponse<>(SystemEnums.SUCCESS, respVoList);
    }

    /**
     * 上传文件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiOperation(value = "0102_上传文件", notes = "上传文件", httpMethod = "POST")
    @ResponseBody
    public BaseResponse<List<FileUploadRespVo>> fileUpload(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (!multipartResolver.isMultipart(request)) {
            return new BaseResponse<>(SystemEnums.SYSTEM_ERROR, "", null);
        }
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        List<FileVO> fileVoList = new ArrayList<>();
        Collection<MultipartFile> files = multiRequest.getFileMap().values();
        try {

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty() || StringUtils.isEmpty(file.getOriginalFilename())) {
                    continue;
                }
                FileVO fileVo = new FileVO();
                fileVo.setData(file.getInputStream());
                fileVo.setName(file.getOriginalFilename());
                fileVo.setSize(file.getSize());
                fileVo.setDataLength(file.getBytes().length);
                fileVoList.add(fileVo);
            }
            return saveFile(fileVoList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error(e.getMessage(), e);
            return new BaseResponse<>(SystemEnums.SYSTEM_ERROR, e.getMessage(), null);
        }
    }


    /**
     * 删除文件
     *
     * @param fileId
     * @return
     */
    @RequestMapping(value = "/deleteFile/{fileId}", method = RequestMethod.POST)
    @ApiOperation(value = "0103_删除文件", notes = "删除文件", httpMethod = "POST")
    public BaseResponse<String> deleteFile(@PathVariable("fileId") String fileId) {
        try {
            minioService.remove(fileId);
            return new BaseResponse<>(SystemEnums.SUCCESS.getCode(), "操作完成！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            return new BaseResponse<>(SystemEnums.SYSTEM_ERROR.getCode(), e.getMessage());
        }
    }
}
