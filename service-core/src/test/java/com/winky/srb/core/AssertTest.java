package com.winky.srb.core;

import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;

import com.winky.common.exception.BusinessException;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * @auther Li Wenjie
 * @create 2022-02-21 14:43
 */
public class AssertTest {

    @Test
    public void test1(){
        Object o = null;
        if(o == null){
            throw new IllegalArgumentException("参数错误");
        }
    }

    @Test
    public void test2(){
        Object o = null;
        //断言替代
        Assert.notNull(o,"参数错误");
    }
}
