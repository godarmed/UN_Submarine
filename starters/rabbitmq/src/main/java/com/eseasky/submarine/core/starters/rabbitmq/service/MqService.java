package com.eseasky.submarine.core.starters.rabbitmq.service;

import com.eseasky.submarine.core.starters.rabbitmq.entity.Message;
import com.eseasky.submarine.core.starters.rabbitmq.entity.Message;
import org.springframework.stereotype.Service;

@Service
public interface MqService {
    <T> void push(Message<T> var1);
}
