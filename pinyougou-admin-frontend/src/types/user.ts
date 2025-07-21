export interface User {
  id: number;
  username: string;
  email: string;
  createdTime: string; // 后端传过来通常是字符串
  // ... 其他你需要的字段
}