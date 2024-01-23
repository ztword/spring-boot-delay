package com.example.demo.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 线程池配置类
 */
@EnableAsync
@Configuration
public class ThreadPoolConfig {
    /**
     * 核心线程数量
     */
    @Value("${import.thread.core}")
    private Integer core;
    /**
     * 最大线程数
     */
    @Value("${import.thread.max}")
    private Integer max;
    /**
     * 排队线程数
     */
    @Value("${import.thread.queue}")
    private Integer queue;
    /**
     * 线程回收时间
     */
    @Value("${import.thread.keepAlive}")
    private Integer keepAlive;

    /**
     * toolsThreadPool
     * @return {@link ThreadPoolExecutor} 线程池
     */
    @Bean("toolThreadPool")
    public ThreadPoolExecutor arxmlThreadPoolExecutor() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("build-tool-%d").build();
        return new ThreadPoolExecutor(core, max, keepAlive, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queue),
                namedThreadFactory);
    }

}
