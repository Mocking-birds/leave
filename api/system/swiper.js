import request from '@/utils/request'

// 查询轮播图管理列表
export function listSwiper(query) {
  return request({
    url: '/system/swiper/list',
    method: 'get',
    params: query
  })
}