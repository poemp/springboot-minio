package org.poem.vo;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.InputStream;


/**
 * @author sangfor
 */
@ApiModel(value = "文件存储对象")
@Data
public class FileVO {

    private Long fileId;

    private String path;

    private String name;

    private Long size;

    private String createTime;

    private String createUser;

    private Integer dataLength;

    private InputStream data;

}
