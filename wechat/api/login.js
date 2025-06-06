import request from '@/utils/request'

// 登录方法
export function login(username, password, code, uuid) {
  const data = {
    username,
    password,
    code,
    uuid
  }
  return request({
    'url': '/login',
    headers: {
      isToken: false
    },
    'method': 'post',
    'data': data
  })
}

// 微信登录
export function wechatLogin(jsCode){
	return request({
		url: '/wechat/login',
		headers: {
			isToken: false
		},
		method: 'post',
		data: {
			jsCode: jsCode
		}
	})
}

// 获取短信验证码
export function getSMSCode(data){
	return request({
		url: '/smscode',
		headers: {
			isToken: false
		},
		method: 'get',
		params: {
			phonenumber: data
		}
	})
}

// 短信验证码登录
export function phoneLogin(data){
	return request({
		url: '/phone/login',
		headers: {
			isToken: false
		},
		method: 'post',
		data: data
	})
}

// 注册方法
export function register(data) {
  return request({
    url: '/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    'url': '/getInfo',
    'method': 'get'
  })
}

// 退出方法
export function logout() {
  return request({
    'url': '/logout',
    'method': 'post'
  })
}

// 获取验证码
export function getCodeImg() {
  return request({
    'url': '/captchaImage',
    headers: {
      isToken: false
    },
    method: 'get',
    timeout: 20000
  })
}
