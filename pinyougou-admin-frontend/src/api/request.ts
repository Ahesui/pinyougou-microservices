import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: 'http://localhost:18080', // **关键：** 指向我们的API网关
  timeout: 10000,
})

// 请求拦截器
service.interceptors.request.use(config => {
  // 可以在这里添加token等
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(response => {
  const res = response.data
  if (res.code !== 200) {
    ElMessage.error(res.message || 'Error')
    return Promise.reject(new Error(res.message || 'Error'))
  } else {
    return res.data
  }
}, error => {
  ElMessage.error(error.message)
  return Promise.reject(error)
})

export default service