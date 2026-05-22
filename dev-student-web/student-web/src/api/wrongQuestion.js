import request2 from './request2'

export const getWrongQuestionList = (params) => {
  return request2({
    url: `/wrong/stats/knowledge/${params?.userId || ''}`,
    method: 'get',
    params
  })
}

export const toggleCollectWrong = (data) => {
  return request2({
    url: '/wrong/add',
    method: 'post',
    params: data
  })
}

export const deleteWrongQuestion = (data) => {
  return request2({
    url: '/wrong/add',
    method: 'post',
    params: data
  })
}

export const markMastered = (data) => {
  return request2({
    url: '/wrong/add',
    method: 'post',
    params: data
  })
}
