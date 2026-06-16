package com.law.train.module.exam_student_paper.entity;

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
 * @description: 学生试卷作答实体类
 */
@Data
@TableName("exam_student_paper")
public class ExamStudentPaper implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 学生 */
    private Long studentId;
    /** 试卷 */
    private Long paperId;
    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    /** 交卷时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
    /** 考试耗时秒数 */
    private Integer durationSec;
    /** 总得分 */
    private BigDecimal totalScore;
    /** 考试状态 */
    private Integer examStatus;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 姓名 */
    @TableField(exist = false)
    private String realName;
    /** 试卷名称 */
    @TableField(exist = false)
    private String paperName;
    /** 课程名称（关联试卷所属课程） */
    @TableField(exist = false)
    private String courseName;
    /** 试卷总分 */
    @TableField(exist = false)
    private BigDecimal paperTotalScore;


}
