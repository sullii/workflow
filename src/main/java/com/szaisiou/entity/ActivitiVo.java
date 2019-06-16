package com.szaisiou.entity;

import lombok.Data;

/**
 * 页面所需要的数据
 */
@Data
public class ActivitiVo extends ActivitiDto{

    private String id;
    private String taskId;
    private String taskName;
    private String processId;
    private String result;
}
