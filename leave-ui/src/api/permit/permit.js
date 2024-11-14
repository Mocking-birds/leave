import request from '@/utils/request'

// 查询假条信息列表
export function listPermit(query) {
  return request({
    url: '/permit/permit/list',
    method: 'get',
    params: query
  })
}

// 查询销假信息列表（isBack为0的假条信息）
export function listBackPermit(query,id,back) {
  return request({
    url: '/permit/permit/list/isBack',
    method: 'get',
    params: {
      pageNum:query.pageNum,
      pageSize:query.pageSize,
      userId:id,
      isBack:back
    }
  })
}

// 查询对应系部或专业对应假条信息（通过部门id）
export function listPermitByDept(query,id,specialConditions){
  return request({
    url: '/permit/permit/list/dept',
    method: 'get',
    params: {
      pageNum:query.pageNum,
      pageSize:query.pageSize,
      deptId:id,
      specialConditions:specialConditions
    }
  })
}

// 查询假条信息详细
export function getPermit(leaveId) {
  return request({
    url: '/permit/permit/' + leaveId,
    method: 'get'
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

// 修改假条信息
export function updatePermit(data) {
  return request({
    url: '/permit/permit',
    method: 'put',
    data: data
  })
}

// 删除假条信息
export function delPermit(leaveId) {
  return request({
    url: '/permit/permit/' + leaveId,
    method: 'delete'
  })
}
