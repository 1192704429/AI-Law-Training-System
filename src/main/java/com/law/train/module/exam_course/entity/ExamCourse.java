package com.law.train.module.exam_course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wuming
 * @description: 课程实体类
 */
@Data
@TableName("exam_course")
public class ExamCourse implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 课程名称 */
    private String courseName;
    /** 课程封面 */
    private String cover;
    /** 课程难度 */
    private Integer difficultyLevel;
    /** 排序号 */
    private Integer sortNo;
    /** 课程简介 */
    private String intro;
    /** 状态 */
    private Integer status;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


}
