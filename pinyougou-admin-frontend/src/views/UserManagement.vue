<template>
  <el-table :data="userList">
    <el-table-column prop="id" label="ID"></el-table-column>
    <el-table-column prop="username" label="Username"></el-table-column>
    <el-table-column prop="email" label="Email"></el-table-column>
  </el-table>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { getUsers } from '../api/user' // 导入API
import type { User } from '../types/user'

const userList = ref<User[]>([]) 
onMounted(async () => {
  try {
    // 现在，await getUsers({}) 的返回值必须符合 User[] 类型
    userList.value = await getUsers({})
  } catch (error) {
    console.error("获取用户列表失败:", error)
  }
})
</script>