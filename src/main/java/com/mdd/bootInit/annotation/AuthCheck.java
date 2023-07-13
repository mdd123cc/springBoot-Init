package com.mdd.bootInit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户权限校验注解
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
//标识注解作用的的目标
@Target(ElementType.METHOD)
//标识注解生命周期
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     *必须有某个角色
     *
     * @return
     */
    String mustRole() default "";
}
