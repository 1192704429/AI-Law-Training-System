package com.law.train.module.exam_review_plan_detail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wuming
 * @description: 复习计划明细实体类
 */
@Data
@TableName("exam_review_plan_detail")
public class ExamReviewPlanDetail implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 复习计划 */
    private Long planId;
    /** 学生 */
    private Long studentId;
    /** 计划日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planDate;
    /** 课程 */
    private Long courseId;
    /** 题目 */
    private Long questionId;
    /** 完成状态 */
    private Integer completeStatus;
    /** 作答记录 */
    private Long practiceRecordId;
    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 计划名称 */
    @TableField(exist = false)
    private String planName;
    /** 姓名 */
    @TableField(exist = false)
    private String realName;
    /** 课程名称 */
    @TableField(exist = false)
    private String courseName;
    /** 题目标题 */
    @TableField(exist = false)
    private String questionTitle;
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    @TableField(exist = false)
    private Long id2;

    // 范围查询字段
    /** 计划日期 开始 */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planDateStart;
    /** 计划日期 结束 */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planDateEnd;

}
