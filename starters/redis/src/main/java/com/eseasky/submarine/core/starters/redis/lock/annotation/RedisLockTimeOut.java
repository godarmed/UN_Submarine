package com.eseasky.submarine.core.starters.redis.lock.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLockTimeOut {
}
