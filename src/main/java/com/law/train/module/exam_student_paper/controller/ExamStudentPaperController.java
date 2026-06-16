package com.law.train.module.exam_student_paper.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_student_paper.entity.ExamStudentPaper;
import com.law.train.module.exam_student_paper.service.ExamStudentPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 学生试卷作答Controller
 */
@RestController
@RequestMapping("/exam_student_paper")
public class ExamStudentPaperController {

    @Autowired
    private ExamStudentPaperService examStudentPaperService;

    /**
     * 分页查询学生试卷作答列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamStudentPaper entity) {
        if (entity == null) {
            entity = new ExamStudentPaper();
        }
        return Result.success(examStudentPaperService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有学生试卷作答列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamStudentPaper entity) {
        if (entity == null) {
            entity = new ExamStudentPaper();
        }
        List<ExamStudentPaper> list = examStudentPaperService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询学生试卷作答
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamStudentPaper examStudentPaper = examStudentPaperService.getById(id);
        if (examStudentPaper == null) {
            return Result.error("学生试卷作答不存在");
        }
        return Result.success(examStudentPaper);
    }

    /**
     * 新增学生试卷作答
     */
    @PostMapping
    public Result save(@RequestBody ExamStudentPaper examStudentPaper) {
        examStudentPaper.setCreateTime(LocalDateTime.now());
        boolean success = examStudentPaperService.save(examStudentPaper);
        if (success) {
            return Result.success("新增成功", examStudentPaper);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新学生试卷作答
     */
    @PutMapping
    public Result update(@RequestBody ExamStudentPaper examStudentPaper) {
        boolean success = examStudentPaperService.updateById(examStudentPaper);
        if (success) {
            return Result.success("更新成功", examStudentPaper);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除学生试卷作答
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examStudentPaperService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除学生试卷作答
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examStudentPaperService.removeByIds(ids);
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
        List<SelectVO> list = examStudentPaperService.listExamStudent();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_paper
     */
    @GetMapping("/select/listExamPaper")
    public Result listExamPaper() {
        List<SelectVO> list = examStudentPaperService.listExamPaper();
        return Result.success(list);
    }

}
