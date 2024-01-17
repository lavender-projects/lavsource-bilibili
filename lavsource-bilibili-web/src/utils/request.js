//noinspection JSUnresolvedReference

import axios from 'axios'
import messageUtils from './message'
import androidInterfaces from '@/utils/androidInterfaces'

let baseURL = androidInterfaces.lavsourceServerJsInterface.getUrlPrefix()
if(baseURL == null) baseURL = import.meta.env.VITE_API_BASE_URL

const request = axios.create({
  baseURL,
  timeout: 5000
})

request.interceptors.request.use(config => {
  return config
}, error => {
  console.log(error)
  return Promise.reject(error)
})

request.interceptors.response.use(response => {
  const status = response.status
  const body = response.data
  if(status !== 200) {
    return Promise.reject(new Error('API error: ' + body.msg))
  }
  return body
}, error => {
  console.log('Axios error: ', error)
  if(error.code === 'ERR_NETWORK') {
    messageUtils.error('网络请求失败')
  } else {
    let msg = error.response.data.msg != null ? error.response.data.msg : error.message
    messageUtils.error(msg)
  }
  return Promise.reject(error)
})

export default request