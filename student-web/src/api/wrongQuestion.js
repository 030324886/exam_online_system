// src/api/wrongQuestion.js
import request from './request' // 引入封装好的axios实例

/**
 * 获取当前用户的错题列表（核心接口）
 * @param {Object} params - 请求参数
 * @param {string/number} params.userId - 用户ID（必传，登录后本地存储）
 * @param {string/number} [params.subjectId] - 科目ID（可选，筛选科目）
 * @param {string/number} [params.chapterId] - 章节ID（可选，筛选章节）
 * @returns {Promise} 错题列表数据
 */
export const getWrongQuestionList = (params) => {
  return request({
    url: '/student/wrongQuestion/list', // 后端A提供的错题列表接口，替换为实际地址
    method: 'get',
    params
  })
}

/**
 * 收藏/取消收藏错题
 * @param {Object} data - 请求体参数
 * @param {string/number} data.questionId - 错题ID
 * @param {string/number} data.userId - 用户ID
 * @param {boolean} data.collectStatus - 收藏状态（true=收藏，false=取消）
 * @returns {Promise} 操作结果
 */
export const toggleCollectWrong = (data) => {
  return request({
    url: '/student/wrongQuestion/toggleCollect', // 后端A提供的收藏接口，替换为实际地址
    method: 'post',
    data
  })
}

/**
 * 删除错题（从错题本移除）
 * @param {Object} data - 请求体参数
 * @param {string/number} data.questionId - 错题ID
 * @param {string/number} data.userId - 用户ID
 * @returns {Promise} 删除结果
 */
export const deleteWrongQuestion = (data) => {
  return request({
    url: '/student/wrongQuestion/delete', // 后端A提供的删除错题接口，替换为实际地址
    method: 'post',
    data
  })
}

/**
 * 可选扩展：错题重新作答后标记为已掌握（从错题本移除）
 * @param {Object} data - 请求体参数
 * @param {string/number} data.questionId - 错题ID
 * @param {string/number} data.userId - 用户ID
 * @returns {Promise} 操作结果
 */
export const markMastered = (data) => {
  return request({
    url: '/student/wrongQuestion/markMastered', // 后端A提供的标记已掌握接口
    method: 'post',
    data
  })
}