package com.stu.backendserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stu.backendserver.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    @Select("SELECT id, type, content, answer FROM question WHERE type = #{type} ORDER BY RAND() LIMIT #{count}")
    List<Question> selectRandomByType(@Param("type") String type, @Param("count") Integer count);

    @Select("SELECT q.id, CAST(q.type AS CHAR) as type, q.content, q.options, q.answer, q.analysis, " +
           "q.knowledge_point as knowledgePoint, q.difficulty " +
           "FROM question q WHERE q.id = #{id}")
    Map<String, Object> selectFullById(@Param("id") Long id);

    @Select("SELECT pq.id as pq_id, pq.score as pq_score, pq.sort_no as sort_no, " +
           "q.id, CAST(q.type AS CHAR) as type, q.content, q.options, q.answer, q.analysis, " +
           "q.knowledge_point as knowledgePoint, q.difficulty " +
           "FROM paper_question pq " +
           "LEFT JOIN question q ON q.id = pq.question_id " +
           "WHERE pq.paper_id = #{paperId} " +
           "ORDER BY pq.sort_no ASC")
    List<Map<String, Object>> selectPaperQuestionsWithDetail(@Param("paperId") Long paperId);
}
