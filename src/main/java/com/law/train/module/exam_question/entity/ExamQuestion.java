package com.law.train.module.exam_question.entity;

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
 * @description: 题目实体类
 */
@Data
@TableName("exam_question")
public class ExamQuestion implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 所属课程 */
    private Long courseId;
    /** 题目标题 */
    private String questionTitle;
    /** 题目类型：1=单选 2=多选 3=填空 4=判断 5=主观题；当前系统仅支持 1、2、5，3、4 保留 */
    private Integer questionType;
    /** 难度等级 */
    private Integer difficultyLevel;
    /** 知识点 */
    private String knowledgePoint;
    /** 标准答案 */
    private String standardAnswer;
    /** 答案解析 */
    private String answerAnalysis;
    /** 默认分值 */
    private BigDecimal score;
    /** 状态 */
    private Integer status;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 课程名称 */
    @TableField(exist = false)
    private String courseName;
    /** 题目类型列表（用于IN查询） */
    @TableField(exist = false)
    private java.util.List<Integer> questionTypeList;


}
