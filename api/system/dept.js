import request from '@/utils/request'

// 查询部门列表
export function getDeptList(query) {
  return request({
    url: '/system/dept/list',
    method: 'get',
	params: query
  })
}