package com.law.train.module.exam_question_option.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wuming
 * @description: 题目选项实体类
 */
@Data
@TableName("exam_question_option")
public class ExamQuestionOption implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 题目 */
    private Long questionId;
    /** 选项标识（JSON 固定输出 optionKey，便于前端展示） */
    @JsonProperty("optionKey")
    private String optionKey;
    /** 选项内容（JSON 固定输出 optionContent，便于前端展示） */
    @JsonProperty("optionContent")
    private String optionContent;
    /** 排序号 */
    private Integer sortNo;
    /** 题目标题 */
    @TableField(exist = false)
    private String questionTitle;


}
