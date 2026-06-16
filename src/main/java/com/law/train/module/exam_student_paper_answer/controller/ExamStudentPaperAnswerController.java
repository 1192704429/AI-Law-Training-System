package com.law.train.module.exam_student_paper_answer.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_student_paper_answer.entity.ExamStudentPaperAnswer;
import com.law.train.module.exam_student_paper_answer.service.ExamStudentPaperAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 学生试卷答案Controller
 */
@RestController
@RequestMapping("/exam_student_paper_answer")
public class ExamStudentPaperAnswerController {

    @Autowired
    private ExamStudentPaperAnswerService examStudentPaperAnswerService;

    /**
     * 分页查询学生试卷答案列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamStudentPaperAnswer entity) {
        if (entity == null) {
            entity = new ExamStudentPaperAnswer();
        }
        return Result.success(examStudentPaperAnswerService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有学生试卷答案列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamStudentPaperAnswer entity) {
        if (entity == null) {
            entity = new ExamStudentPaperAnswer();
        }
        List<ExamStudentPaperAnswer> list = examStudentPaperAnswerService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询学生试卷答案
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamStudentPaperAnswer examStudentPaperAnswer = examStudentPaperAnswerService.getById(id);
        if (examStudentPaperAnswer == null) {
            return Result.error("学生试卷答案不存在");
        }
        return Result.success(examStudentPaperAnswer);
    }

    /**
     * 新增学生试卷答案
     */
    @PostMapping
    public Result save(@RequestBody ExamStudentPaperAnswer examStudentPaperAnswer) {
        examStudentPaperAnswer.setCreateTime(LocalDateTime.now());
        boolean success = examStudentPaperAnswerService.save(examStudentPaperAnswer);
        if (success) {
            return Result.success("新增成功", examStudentPaperAnswer);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新学生试卷答案
     */
    @PutMapping
    public Result update(@RequestBody ExamStudentPaperAnswer examStudentPaperAnswer) {
        boolean success = examStudentPaperAnswerService.updateById(examStudentPaperAnswer);
        if (success) {
            return Result.success("更新成功", examStudentPaperAnswer);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除学生试卷答案
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examStudentPaperAnswerService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除学生试卷答案
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examStudentPaperAnswerService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 下拉数据接口：exam_student
     */
    @GetMapping("/select/listExamStudent")
    public Result listExamStudent() {
        List<SelectVO> list = examStudentPaperAnswerService.listExamStudent();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_paper
     */
    @GetMapping("/select/listExamPaper")
    public Result listExamPaper() {
        List<SelectVO> list = examStudentPaperAnswerService.listExamPaper();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_question
     */
    @GetMapping("/select/listExamQuestion")
    public Result listExamQuestion() {
        List<SelectVO> list = examStudentPaperAnswerService.listExamQuestion();
        return Result.success(list);
    }

}
