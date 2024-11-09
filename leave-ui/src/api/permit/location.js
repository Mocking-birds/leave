import request from '@/utils/request'

// 查询位置信息列表
export function listLocation(query) {
  return request({
    url: '/permit/location/list',
    method: 'get',
    params: query
  })
}

// 查询位置信息详细
export function getLocation(locationId) {
  return request({
    url: '/permit/location/' + locationId,
    method: 'get'
  })
}

// 新增位置信息
export function addLocation(data) {
  return request({
    url: '/permit/location',
    method: 'post',
    data: data
  })
}

// 修改位置信息
export function updateLocation(data) {
  return request({
    url: '/permit/location',
    method: 'put',
    data: data
  })
}

// 删除位置信息
export function delLocation(locationId) {
  return request({
    url: '/permit/location/' + locationId,
    method: 'delete'
  })
}
