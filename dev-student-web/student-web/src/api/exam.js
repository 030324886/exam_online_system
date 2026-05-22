import request from './request'

export const getPaperList = () => {
  return request({
    url: '/paper/list',
    method: 'get'
  })
}

export const getPaperDetail = (id) => {
  return request({
    url: `/paper/detail/${id}`,
    method: 'get'
  })
}

export const submitExam = (data) => {
  return request({
    url: '/submissions/submit',
    method: 'post',
    data
  })
}

export const getExamRecordDetail = (id) => {
  return request({
    url: `/exam/record/detail/${id}`,
    method: 'get'
  })
}

export const getClassScoreStats = (params) => {
  return request({
    url: '/statistics/class-score',
    method: 'get',
    params
  })
}

export const getWeakKnowledge = (params) => {
  return request({
    url: '/statistics/weak-knowledge',
    method: 'get',
    params
  })
}

export const objectiveGrade = (data) => {
  return request({
    url: '/papers/objective/grade',
    method: 'post',
    data
  })
}
