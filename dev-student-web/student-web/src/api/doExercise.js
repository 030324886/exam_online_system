import request2 from './request2'

export const getQuestionListByChapter = (params) => {
  return request2({
    url: '/question/list',
    method: 'get',
    params
  })
}

export const submitAnswer = (data) => {
  return request2({
    url: '/answer/submit',
    method: 'post',
    data
  })
}

export const getQuestionDetail = (params) => {
  return request2({
    url: '/question/list',
    method: 'get',
    params
  })
}

export const getAnswerRecord = (params) => {
  return request2({
    url: '/answer/submit',
    method: 'post',
    data: params
  })
}
