import request2 from './request2'

export const getSubjectList = () => {
  return request2({
    url: '/question/list',
    method: 'get'
  })
}

export const getChapterList = (params) => {
  return request2({
    url: '/question/list',
    method: 'get',
    params
  })
}

export const getKnowledgeList = (params) => {
  return request2({
    url: `/question/knowledge/${params?.point || ''}`,
    method: 'get',
    params
  })
}
