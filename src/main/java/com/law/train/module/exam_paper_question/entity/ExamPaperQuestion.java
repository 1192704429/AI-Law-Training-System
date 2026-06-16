package com.law.train.module.exam_paper_question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wuming
 * @description: 试卷题目关联实体类
 */
@Data
@TableName("exam_paper_question")
public class ExamPaperQuestion implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 试卷 */
    private Long paperId;
    /** 题目 */
    private Long questionId;
    /** 题目分值 */
    private BigDecimal score;
    /** 排序号 */
    private Integer sortNo;
    /** 试卷名称 */
    @TableField(exist = false)
    private String paperName;
    /** 题目标题 */
    @TableField(exist = false)
    private String questionTitle;
    /** 题目类型 */
    @TableField(exist = false)
    private Integer questionType;
    /** 题目难度 */
    @TableField(exist = false)
    private Integer difficultyLevel;


}
