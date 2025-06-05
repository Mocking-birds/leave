import request from '@/utils/request'

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