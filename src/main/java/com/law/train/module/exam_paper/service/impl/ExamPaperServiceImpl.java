package com.law.train.module.exam_paper.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.common.Result;
import com.law.train.module.exam_paper.entity.ExamPaper;
import com.law.train.module.exam_paper.mapper.ExamPaperMapper;
import com.law.train.module.exam_paper.service.ExamPaperService;
import com.law.train.module.exam_paper_question.entity.ExamPaperQuestion;
import com.law.train.module.exam_paper_question.service.ExamPaperQuestionService;
import com.law.train.module.exam_question.entity.ExamQuestion;
import com.law.train.module.exam_question.service.ExamQuestionService;
import com.law.train.util.AiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.law.train.vo.SelectVO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wuming
 * @description: 试卷Service实现类
 */
@Service
public class ExamPaperServiceImpl extends ServiceImpl<ExamPaperMapper, ExamPaper> implements ExamPaperService {

    @Autowired
    private ExamPaperMapper examPaperMapper;

    @Autowired
    private ExamPaperQuestionService examPaperQuestionService;

    @Autowired
    private ExamQuestionService examQuestionService;

    @Autowired
    private AiChatClient aiChatClient;

    @Override
    public IPage<ExamPaper> pageQuery(Integer current, Integer size, ExamPaper entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamPaper> records = examPaperMapper.pageQuery(entity, offset, limit);
        Long total = examPaperMapper.countQuery(entity);

        Page<ExamPaper> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamPaper> listQuery(ExamPaper entity) {
        return examPaperMapper.listQuery(entity);
    }

    @Override
    public ExamPaper getById(Long id) {
        return examPaperMapper.getById(id);
    }

    /**
     * 下拉数据：exam_course
     */
    @Override
    public List<SelectVO> listExamCourse() {
        return examPaperMapper.listExamCourse();
    }

    /**
     * 批量添加题目到试卷
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addQuestions(Long paperId, List<Long> questionIds) {
        if (paperId == null) {
            return Result.error("试卷ID不能为空");
        }
        if (questionIds == null || questionIds.isEmpty()) {
            return Result.error("题目ID列表不能为空");
        }

        ExamPaper examPaper = this.getById(paperId);
        if (examPaper == null) {
            return Result.error("试卷不存在");
        }

        // 查询已存在的题目，避免重复添加
        ExamPaperQuestion queryEntity = new ExamPaperQuestion();
        queryEntity.setPaperId(paperId);
        List<ExamPaperQuestion> existingQuestions = examPaperQuestionService.listQuery(queryEntity);
        List<Long> existingQuestionIds = existingQuestions.stream()
                .map(ExamPaperQuestion::getQuestionId)
                .collect(Collectors.toList());

        // 过滤掉已存在的题目
        List<Long> newQuestionIds = questionIds.stream()
                .filter(id -> !existingQuestionIds.contains(id))
                .collect(Collectors.toList());

        if (newQuestionIds.isEmpty()) {
            return Result.error("所选题目已全部存在于试卷中");
        }

        // 查询题目信息，获取默认分值
        List<ExamQuestion> questions = (List<ExamQuestion>) examQuestionService.listByIds(newQuestionIds);
        
        // 获取当前最大排序号
        int maxSortNo = existingQuestions.stream()
                .mapToInt(q -> q.getSortNo() != null ? q.getSortNo() : 0)
                .max()
                .orElse(0);

        // 批量添加题目
        for (int i = 0; i < newQuestionIds.size(); i++) {
            Long questionId = newQuestionIds.get(i);
            ExamPaperQuestion paperQuestion = new ExamPaperQuestion();
            paperQuestion.setPaperId(paperId);
            paperQuestion.setQuestionId(questionId);
            
            // 查找对应的题目，使用默认分值
            ExamQuestion question = questions.stream()
                    .filter(q -> q.getId().equals(questionId))
                    .findFirst()
                    .orElse(null);
            
            if (question != null && question.getScore() != null) {
                paperQuestion.setScore(question.getScore());
            } else {
                paperQuestion.setScore(BigDecimal.ZERO);
            }
            
            paperQuestion.setSortNo(maxSortNo + i + 1);
            examPaperQuestionService.save(paperQuestion);
        }

        // 计算并更新试卷信息
        return calculatePaperInfo(paperId);
    }

    /**
     * 计算并更新试卷总分和题目数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result calculatePaperInfo(Long paperId) {
        if (paperId == null) {
            return Result.error("试卷ID不能为空");
        }

        ExamPaper examPaper = this.getById(paperId);
        if (examPaper == null) {
            return Result.error("试卷不存在");
        }

        // 查询试卷下的所有题目
        ExamPaperQuestion queryEntity = new ExamPaperQuestion();
        queryEntity.setPaperId(paperId);
        List<ExamPaperQuestion> paperQuestions = examPaperQuestionService.listQuery(queryEntity);

        // 计算总分和题目数量
        BigDecimal totalScore = paperQuestions.stream()
                .map(q -> q.getScore() != null ? q.getScore() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int questionCount = paperQuestions.size();

        // 更新试卷信息
        examPaper.setTotalScore(totalScore);
        examPaper.setQuestionCount(questionCount);
        boolean success = this.updateById(examPaper);

        if (success) {
            return Result.success("计算成功", examPaper);
        }
        return Result.error("计算失败");
    }

    /**
     * AI组卷
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result aiGeneratePaper(Long paperId) {
        if (paperId == null) {
            return Result.error("试卷ID不能为空");
        }

        ExamPaper examPaper = this.getById(paperId);
        if (examPaper == null) {
            return Result.error("试卷不存在");
        }

        // 检查试卷类型是否为AI组卷
        if (examPaper.getPaperType() == null || examPaper.getPaperType() != 2) {
            return Result.error("只有AI组卷类型的试卷才能使用AI组卷功能");
        }

        // 检查试卷状态，已启用的试卷不能重新组卷
        if (examPaper.getStatus() != null && examPaper.getStatus() == 1) {
            return Result.error("已启用的试卷不能重新组卷，请先停用");
        }

        // 获取课程ID和难度
        Long courseId = examPaper.getCourseId();
        Integer difficultyLevel = examPaper.getDifficultyLevel();

        if (courseId == null) {
            return Result.error("试卷所属课程不能为空");
        }

        // 查询该课程下的所有题目（状态为启用）
        ExamQuestion questionQuery = new ExamQuestion();
        questionQuery.setCourseId(courseId);
        questionQuery.setStatus(1); // 只查询启用的题目
        List<ExamQuestion> allQuestions = examQuestionService.listQuery(questionQuery);

        if (allQuestions == null || allQuestions.isEmpty()) {
            return Result.error("该课程下没有可用的题目，请先添加题目");
        }

        // 构建题目信息数组
        List<Map<String, Object>> questionInfoList = new ArrayList<>();
        for (ExamQuestion question : allQuestions) {
            Map<String, Object> questionInfo = new HashMap<>();
            questionInfo.put("id", question.getId());
            questionInfo.put("type", question.getQuestionType());
            questionInfo.put("difficulty", question.getDifficultyLevel());
            questionInfoList.add(questionInfo);
        }

        // 构建难度等级映射
        Map<Integer, String> difficultyMap = new HashMap<>();
        difficultyMap.put(1, "简单");
        difficultyMap.put(2, "中等");
        difficultyMap.put(3, "困难");

        // 题目类型映射：当前系统仅支持 1单选 2多选 5主观题；3填空 4判断保留供后续使用
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(1, "单选");
        typeMap.put(2, "多选");
        typeMap.put(3, "填空");
        typeMap.put(4, "判断");
        typeMap.put(5, "主观题");

        // 构建提示词
        String systemPrompt = "你是一个专业的试卷组卷助手。你的任务是根据提供的题目信息，智能地选择合适数量的题目组成试卷。";
        
        String userPrompt = String.format(
            "请根据以下要求进行AI组卷：\n\n" +
            "试卷难度要求：%s\n\n" +
            "可用题目列表（JSON格式）：\n%s\n\n" +
            "题目类型说明（当前主要使用单选、多选、主观题）：\n" +
            "- 1: 单选\n" +
            "- 2: 多选\n" +
            "- 3: 填空\n" +
            "- 4: 判断\n" +
            "- 5: 主观题\n\n" +
            "难度等级说明：\n" +
            "- 1: 简单\n" +
            "- 2: 中等\n" +
            "- 3: 困难\n\n" +
            "组卷要求：\n" +
            "1. 根据试卷难度要求，从可用题目中选择合适数量和类型的题目\n" +
            "2. 题目类型应多样化，优先包含单选、多选、主观题（填空、判断保留）\n" +
            "3. 题目难度应该与试卷难度要求相匹配，可以适当包含一些相邻难度的题目以增加试卷的层次感\n" +
            "4. 题目数量要合理，建议在20左右\n" +
            "5. 注意题目的难度分布要合理\n\n" +
            "请返回一个JSON数组，数组中只包含选中的题目ID（数字），格式如下：\n" +
            "[1, 2, 3, 4, 5, ...]\n\n" +
            "请直接返回JSON数组，不要包含任何其他文字说明。",
            difficultyMap.getOrDefault(difficultyLevel, "未知"),
            JSON.toJSONString(questionInfoList)
        );

        // 调用AI进行组卷
        String aiResponse = aiChatClient.chat(systemPrompt, userPrompt);
        
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return Result.error("AI组卷失败：未收到AI响应");
        }

        // 解析AI返回的题目ID数组
        List<Long> selectedQuestionIds = new ArrayList<>();
        try {
            // 清理响应文本，提取JSON数组
            String cleanedResponse = aiResponse.trim();
            // 移除可能的markdown代码块标记
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7);
            }
            if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3);
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();
            
            // 尝试解析JSON数组
            JSONArray jsonArray = JSON.parseArray(cleanedResponse);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    Object item = jsonArray.get(i);
                    if (item instanceof Number) {
                        selectedQuestionIds.add(((Number) item).longValue());
                    } else if (item instanceof String) {
                        try {
                            selectedQuestionIds.add(Long.parseLong((String) item));
                        } catch (NumberFormatException e) {
                            // 忽略无法解析的项
                        }
                    }
                }
            } else {
                // 如果不是JSON数组，尝试提取数字
                String[] parts = cleanedResponse.replaceAll("[^0-9,]", "").split(",");
                for (String part : parts) {
                    if (!part.trim().isEmpty()) {
                        try {
                            selectedQuestionIds.add(Long.parseLong(part.trim()));
                        } catch (NumberFormatException e) {
                            // 忽略无法解析的项
                        }
                    }
                }
            }
        } catch (Exception e) {
            return Result.error("AI组卷失败：解析AI响应失败 - " + e.getMessage() + "，响应内容：" + aiResponse);
        }

        if (selectedQuestionIds.isEmpty()) {
            return Result.error("AI组卷失败：未选择任何题目，AI响应：" + aiResponse);
        }

        // 验证选中的题目ID是否都在可用题目列表中
        List<Long> availableQuestionIds = allQuestions.stream()
                .map(ExamQuestion::getId)
                .collect(Collectors.toList());
        
        List<Long> invalidIds = selectedQuestionIds.stream()
                .filter(id -> !availableQuestionIds.contains(id))
                .collect(Collectors.toList());
        
        if (!invalidIds.isEmpty()) {
            return Result.error("AI组卷失败：选中的题目ID不在可用题目列表中：" + invalidIds);
        }

        // 删除试卷原有的题目
        ExamPaperQuestion deleteQuery = new ExamPaperQuestion();
        deleteQuery.setPaperId(paperId);
        List<ExamPaperQuestion> existingQuestions = examPaperQuestionService.listQuery(deleteQuery);
        if (!existingQuestions.isEmpty()) {
            List<Long> existingIds = existingQuestions.stream()
                    .map(ExamPaperQuestion::getId)
                    .collect(Collectors.toList());
            examPaperQuestionService.removeByIds(existingIds);
        }

        // 添加AI选中的题目到试卷
        Result addResult = this.addQuestions(paperId, selectedQuestionIds);
        if (addResult.getCode() != 200) {
            return Result.error("AI组卷失败：添加题目失败 - " + addResult.getMsg());
        }

        return Result.success("AI组卷成功，共选择 " + selectedQuestionIds.size() + " 道题目", selectedQuestionIds);
    }

}
