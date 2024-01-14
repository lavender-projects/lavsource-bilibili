import request from '@/utils/request'

const loginApi = {
  validateCode: () => request({
    url: '/platform/bilibili/validateCode',
    method: 'get'
  })
}

export default loginApi