import request from '@/utils/request'

// 查询部门列表
export function getDeptList() {
  return request({
    url: '/system/dept/list',
    method: 'get'
  })
}