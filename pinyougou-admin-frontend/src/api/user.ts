import request from './request'
import type { User } from '../types/user' // 导入User类型

// 明确声明函数的返回类型是一个 Promise，Promise的结果是 User[]
export const getUsers = (params: any): Promise<User[]> => {
  return request({
    url: '/api/users',
    method: 'get',
    params
  })
}