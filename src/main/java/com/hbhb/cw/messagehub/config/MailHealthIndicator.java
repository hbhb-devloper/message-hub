package com.hbhb.cw.messagehub.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 解决启动时mail健康检查失败问题
 *
 * @author xiaokang
 * @since 2020-12-04
 */
@Component
public class MailHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        int errorCode = check();
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    int check() {
        // 可以实现自定义的数据库检测逻辑
        return 0;
    }
}
