package org.poem.vo;


import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class FileUploadRespVo {
    private Long fileId;

    private String filePath;

    private String name;

    private String fileType;
}
