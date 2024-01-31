import request from '@/utils/request'

const loginApi = {
  validateCode: () => request({
    url: '/platform/bilibili/validateCode',
    method: 'get'
  }),
  loginStatus: () => request({
    url: '/platform/bilibili/loginStatus',
    method: 'get'
  }),
  login: data => request({
    url: '/platform/bilibili/login',
    method: 'post',
    data
  }),
  logout: () => request({
    url: '/platform/bilibili/logout',
    method: 'get'
  })
}

export default loginApi