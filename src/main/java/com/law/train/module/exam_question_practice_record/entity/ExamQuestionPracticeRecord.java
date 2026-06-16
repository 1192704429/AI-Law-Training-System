package com.law.train.module.exam_question_practice_record.entity;

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
 * @description: 题目作答记录实体类
 */
@Data
@TableName("exam_question_practice_record")
public class ExamQuestionPracticeRecord implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 学生 */
    private Long studentId;
    /** 课程 */
    private Long courseId;
    /** 题目 */
    private Long questionId;
    /** 练习类型 */
    private Integer practiceType;
    /** 题目来源 */
    private Integer sourceType;
    /** 试卷 */
    private Long paperId;
    /** 复习计划 */
    private Long planId;
    /** 学生答案 */
    private String studentAnswer;
    /** 标准答案 */
    private String standardAnswer;
    /** 是否正确 */
    private Integer isCorrect;
    /** 本题得分 */
    private BigDecimal score;
    /** 得分率 */
    private BigDecimal scoreRate;
    /** 作答耗时秒数 */
    private Integer useTimeSec;
    /** 判题方式 */
    private Integer judgeType;
    /** 判题结果说明 */
    private String judgeResult;
    /** 题目难度 */
    private Integer difficultyLevel;
    /** 作答时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime answerTime;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 姓名 */
    @TableField(exist = false)
    private String realName;
    /** 课程名称 */
    @TableField(exist = false)
    private String courseName;
    /** 题目标题 */
    @TableField(exist = false)
    private String questionTitle;
    /** 试卷名称 */
    @TableField(exist = false)
    private String paperName;
    /** 计划名称 */
    @TableField(exist = false)
    private String planName;


}
