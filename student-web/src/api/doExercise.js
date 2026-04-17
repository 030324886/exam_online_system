// src/api/doExercise.js
import request from './request' // 引入封装好的axios实例

/**
 * 按章节ID获取题目列表（核心刷题接口）
 * @param {Object} params - 请求参数
 * @param {string/number} params.chapterId - 章节ID（必传）
 * @param {string/number} [params.knowledgeId] - 知识点ID（可选，用于筛选）
 * @returns {Promise} 接口返回的题目列表数据
 */
export const getQuestionListByChapter = (params) => {
  return request({
    url: '/student/question/listByChapter', // 后端A提供的按章节查题目接口，需替换为实际地址
    method: 'get',
    params
  })
}

/**
 * 提交单题答案（自动保存/手动提交都用这个接口）
 * @param {Object} data - 请求体参数
 * @param {string/number} data.questionId - 题目ID
 * @param {string/number} data.userId - 用户ID（登录后本地存储的ID）
 * @param {string/array} data.answer - 用户答案（单选/填空是字符串，多选是数组）
 * @param {string/number} data.chapterId - 章节ID
 * @returns {Promise} 提交结果
 */
export const submitAnswer = (data) => {
  return request({
    url: '/student/answer/submit', // 后端A提供的答题提交接口，需替换为实际地址
    method: 'post',
    data // POST请求参数放在请求体
  })
}

/**
 * 可选扩展：获取单题详情（用于错题再练）
 * @param {Object} params - 请求参数
 * @param {string/number} params.questionId - 题目ID
 * @returns {Promise} 单题完整信息
 */
export const getQuestionDetail = (params) => {
  return request({
    url: '/student/question/detail', // 后端A提供的单题详情接口
    method: 'get',
    params
  })
}

/**
 * 可选扩展：获取答题记录（恢复上次未完成的刷题）
 * @param {Object} params - 请求参数
 * @param {string/number} params.chapterId - 章节ID
 * @param {string/number} params.userId - 用户ID
 * @returns {Promise} 历史答题记录
 */
export const getAnswerRecord = (params) => {
  return request({
    url: '/student/answer/record', // 后端A提供的答题记录接口
    method: 'get',
    params
  })
}