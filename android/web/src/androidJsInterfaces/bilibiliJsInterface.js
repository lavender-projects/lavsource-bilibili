import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import codeUtils from '@/utils/code'

const methodDefinitions = {
  validateCode: {
    isAsync: true,
    fallback: async () => await codeUtils.requestAndGetData({
      url: '/platform/bilibili/validateCode',
      method: 'get'
    })
  },
  loginStatus: {
    isAsync: true,
    fallback: async () => await codeUtils.requestAndGetData({
      url: '/platform/bilibili/loginStatus',
      method: 'get'
    })
  },
  login: {
    isAsync: true,
    fallback: async data => await codeUtils.requestAndGetData({
      url: '/platform/bilibili/login',
      method: 'post',
      data
    })
  },
  logout: {
    isAsync: true,
    fallback: async () => await codeUtils.requestAndGetData({
      url: '/platform/bilibili/logout',
      method: 'get'
    })
  }
}

const bilibiliJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'BilibiliJsInterface', methodDefinitions
) ?? methodDefinitions

export default bilibiliJsInterface