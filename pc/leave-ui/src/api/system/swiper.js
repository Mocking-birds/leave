import request from '@/utils/request'

// 查询轮播图管理列表
export function listSwiper(query) {
  return request({
    url: '/system/swiper/list',
    method: 'get',
    params: query
  })
}

// 查询轮播图管理详细
export function getSwiper(swiperId) {
  return request({
    url: '/system/swiper/' + swiperId,
    method: 'get'
  })
}

// 新增轮播图管理
export function addSwiper(data) {
  return request({
    url: '/system/swiper',
    method: 'post',
    data: data
  })
}

// 修改轮播图管理
export function updateSwiper(data) {
  return request({
    url: '/system/swiper',
    method: 'put',
    data: data
  })
}

//上传轮播图图片
export function editSwiperImg(file){
  const formData = new FormData()
  formData.append("file",file)

  return request({
    url: '/system/swiper/img',
    method: 'post',
    data: formData,
    headers:{
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除轮播图管理
export function delSwiper(swiperId) {
  return request({
    url: '/system/swiper/' + swiperId,
    method: 'delete'
  })
}
