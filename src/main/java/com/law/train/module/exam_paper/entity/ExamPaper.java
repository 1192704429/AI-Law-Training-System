package com.law.train.module.exam_paper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * @author wuming
 * @description: 试卷实体类
 */
@Data
@TableName("exam_paper")
public class ExamPaper implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 试卷名称 */
    private String paperName;
    /** 所属课程 */
    private Long courseId;
    /** 试卷类型 */
    private Integer paperType;
    /** 试卷难度 */
    private Integer difficultyLevel;
    /** 总分 */
    private BigDecimal totalScore;
    /** 题目数量 */
    private Integer questionCount;
    /** 考试时长分钟 */
    private Integer durationMinutes;
    /** 试卷说明 */
    private String paperDesc;
    /** 状态 */
    private Integer status;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 课程名称 */
    @TableField(exist = false)
    private String courseName;


}
