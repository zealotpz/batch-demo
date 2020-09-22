package com.zealotpz.batchdemo.amqp.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author: zealotpz
 * create: 2020-09-22
 **/

@Configuration
public class RabbitMqConfig {

    // ↓↓↓↓↓ 跑批任务 ↓↓↓↓↓
    @Bean("memberBatchTaskExchange")
    public Exchange memberBatchTaskExchange() {
        return ExchangeBuilder.topicExchange("memberBatchTask.topic").durable(true).build();
    }

    @Bean("memberBatchTaskQueue")
    public Queue memberBatchTaskQueue() {
        return QueueBuilder.durable("memberBatchTaskQueue").build();
    }

    @Bean
    public Binding memberBatchTaskBinding(@Qualifier("memberBatchTaskExchange") Exchange topicExchange,
                                          @Qualifier("memberBatchTaskQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(topicExchange).with("memberBatchTask.#").noargs();
    }

}
