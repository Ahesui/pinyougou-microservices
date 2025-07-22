import 'package:dio/dio.dart';
import 'dart:io' show Platform; // 引入Platform类来判断操作系统
import 'package:flutter/foundation.dart'
    show kReleaseMode; // 引入kReleaseMode来判断是否是发布版

// 1. 将 getBaseUrl() 函数定义在类的外部，或者作为类的静态方法
//    定义在外部更通用，因为它不依赖于类的任何实例
String getBaseUrl() {
  // 如果是发布模式，总是使用生产环境的API地址
  if (kReleaseMode) {
    // TODO: 替换成你未来真实的生产环境域名
    return 'https://api.pinyougou.com';
  }

  // 如果是开发模式，则根据平台返回不同的调试地址
  try {
    if (Platform.isAndroid) {
      // Android模拟器，使用10.0.2.2来访问宿主机
      return 'http://192.168.31.10:18080';
    } else if (Platform.isIOS) {
      // iOS模拟器，可以直接使用localhost
      return 'http://localhost:18080';
    }
  } catch (e) {
    // 如果在不支持 'dart:io' 的平台（如Web）上调用了Platform，会抛出异常
    // 这里可以添加Web平台的逻辑，或者直接走下面的默认逻辑
  }

  // 默认情况，适用于桌面端开发、Web开发，或者作为备用
  // 注意：在真机调试时，这里需要手动替换为你电脑的局域网IP
  // 例如：'http://192.168.31.10:18080'
  return 'http://192.168.31.10:18080';
}

// 2. 创建ApiService类
class ApiService {
  // 3. 在创建Dio实例时，调用 getBaseUrl() 函数
  final Dio _dio = Dio(
    BaseOptions(
      baseUrl: getBaseUrl(), // <-- 在这里应用！
      // connectTimeout: Duration(seconds: 5), // 5秒连接超时
      // receiveTimeout: Duration(seconds: 3), // 3秒接收超时
    ),
  );

  // 构造函数，可以用来配置拦截器等
  ApiService() {
    _dio.interceptors.add(
      LogInterceptor(responseBody: true, requestBody: true),
    ); // 添加日志拦截器，方便调试
  }

  Future<Map<String, dynamic>> getProducts({
    int page = 0,
    int size = 20,
  }) async {
    try {
      final response = await _dio.get(
        '/api/products',
        queryParameters: {'page': page, 'size': size, 'sort': 'id,asc'},
      );
      if (response.data['code'] == 200) {
        // 返回整个Page对象，而不仅仅是列表
        return response.data['data'];
      }
      return {};
    } catch (e) {
      print(e);
      return {};
    }
  }
}
