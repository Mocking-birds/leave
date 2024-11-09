import request from '@/utils/request'

// 查询假条信息列表
export function listPermit(query) {
  return request({
    url: '/permit/permit/list',
    method: 'get',
    params: query
  })
}

// 新增假条信息
export function listPermitByUserId(query,id) {
  return request({
    url: '/permit/permit/list/' + id,
    method: 'get',
    params: query
  })
}

// 查询假条信息详细
export function getPermit(leaveId) {
  return request({
    url: '/permit/permit/' + leaveId,
    method: 'get'
  })
}

// 修改假条信息
export function updatePermit(data) {
  return request({
    url: '/permit/permit',
    method: 'put',
    data: data
  })
}

// 新增假条信息
export function addPermit(data) {
  return request({
    url: '/permit/permit',
    method: 'post',
    data: data
  })
}
