package com.poap.pinyougou.productservice.feign;

import com.poap.pinyougou.productservice.common.Result; // 可以把Result类也复制一份到product-service
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// value/name: 指定要调用的服务在Nacos中的名字
// path: 如果对方的Controller有统一的 @RequestMapping，可以写在这里
@FeignClient(value = "pinyougou-user-service", path = "/api/users")
public interface UserFeignClient {

    // 这里的@GetMapping要和user-service中UserController的方法完全对应
    // 我们假设UserController里有一个根据ID查用户的接口
    @GetMapping("/{id}")
    Result<UserDTO> getUserById(@PathVariable("id") Long id);
}