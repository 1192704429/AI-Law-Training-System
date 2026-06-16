package com.law.train.module.exam_wrong_question_book.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_wrong_question_book.entity.ExamWrongQuestionBook;
import com.law.train.module.exam_wrong_question_book.service.ExamWrongQuestionBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 学生错题本Controller
 */
@RestController
@RequestMapping("/exam_wrong_question_book")
public class ExamWrongQuestionBookController {

    @Autowired
    private ExamWrongQuestionBookService examWrongQuestionBookService;

    /**
     * 分页查询学生错题本列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamWrongQuestionBook entity) {
        if (entity == null) {
            entity = new ExamWrongQuestionBook();
        }
        return Result.success(examWrongQuestionBookService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有学生错题本列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamWrongQuestionBook entity) {
        if (entity == null) {
            entity = new ExamWrongQuestionBook();
        }
        List<ExamWrongQuestionBook> list = examWrongQuestionBookService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询学生错题本
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamWrongQuestionBook examWrongQuestionBook = examWrongQuestionBookService.getById(id);
        if (examWrongQuestionBook == null) {
            return Result.error("学生错题本不存在");
        }
        return Result.success(examWrongQuestionBook);
    }

    /**
     * 新增学生错题本
     */
    @PostMapping
    public Result save(@RequestBody ExamWrongQuestionBook examWrongQuestionBook) {
        examWrongQuestionBook.setCreateTime(LocalDateTime.now());
        boolean success = examWrongQuestionBookService.save(examWrongQuestionBook);
        if (success) {
            return Result.success("新增成功", examWrongQuestionBook);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新学生错题本
     */
    @PutMapping
    public Result update(@RequestBody ExamWrongQuestionBook examWrongQuestionBook) {
        boolean success = examWrongQuestionBookService.updateById(examWrongQuestionBook);
        if (success) {
            return Result.success("更新成功", examWrongQuestionBook);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除学生错题本
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examWrongQuestionBookService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除学生错题本
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examWrongQuestionBookService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 下拉数据接口：exam_course
     */
    @GetMapping("/select/listExamCourse")
    public Result listExamCourse() {
        List<SelectVO> list = examWrongQuestionBookService.listExamCourse();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_question_practice_record
     */
    @GetMapping("/select/listExamQuestionPracticeRecord")
    public Result listExamQuestionPracticeRecord() {
        List<SelectVO> list = examWrongQuestionBookService.listExamQuestionPracticeRecord();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_student
     */
    @GetMapping("/select/listExamStudent")
    public Result listExamStudent() {
        List<SelectVO> list = examWrongQuestionBookService.listExamStudent();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_question
     */
    @GetMapping("/select/listExamQuestion")
    public Result listExamQuestion() {
        List<SelectVO> list = examWrongQuestionBookService.listExamQuestion();
        return Result.success(list);
    }

}
