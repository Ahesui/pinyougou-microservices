package com.poap.pinyougou.userservice.service.login;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginStrategyFactory {
    private final Map<LoginType, LoginStrategy> strategyMap = new ConcurrentHashMap<>();

    // Spring会自动将所有LoginStrategy的实现类注入到这个List中
    public LoginStrategyFactory(List<LoginStrategy> strategies) {
        for (LoginStrategy strategy : strategies) {
            strategyMap.put(strategy.supports(), strategy);
        }
    }

    public LoginStrategy getStrategy(LoginType type) {
        LoginStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的登录类型: " + type);
        }
        return strategy;
    }
}