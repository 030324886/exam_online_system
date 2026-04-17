// src/api/questionBank.js
import request from './request' // 引入封装好的axios实例

/**
 * 获取科目列表（对接后端A的题库分类接口）
 * @returns {Promise} 接口返回的科目列表数据
 */
export const getSubjectList = () => {
  return request({
    url: '/student/subject/list', // 后端A提供的科目列表接口地址，需确认后修改
    method: 'get'
  })
}

/**
 * 获取章节列表（根据科目ID筛选）
 * @param {Object} params - 请求参数
 * @param {string/number} params.subjectId - 科目ID
 * @returns {Promise} 接口返回的章节列表数据
 */
export const getChapterList = (params) => {
  return request({
    url: '/student/chapter/list', // 后端A提供的章节列表接口地址，需确认后修改
    method: 'get',
    params // GET请求参数拼在URL上
  })
}

/**
 * 可选扩展：根据知识点筛选题目分类
 * @param {Object} params - 请求参数
 * @param {string/number} params.chapterId - 章节ID
 * @returns {Promise} 知识点列表数据
 */
export const getKnowledgeList = (params) => {
  return request({
    url: '/student/knowledge/list', // 后端A提供的知识点接口地址
    method: 'get',
    params
  })
}