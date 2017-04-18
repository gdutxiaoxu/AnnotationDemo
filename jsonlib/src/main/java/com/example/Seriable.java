package com.example;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author meitu.xujun  on 2017/4/14 18:05
 * @version 0.1
 */

@Documented()
// 表示是基于编译时注解的
@Retention(RetentionPolicy.CLASS)
// 表示可以作用于成员变量，类、接口
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Seriable {

}
