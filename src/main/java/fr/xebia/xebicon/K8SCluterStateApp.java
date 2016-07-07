package fr.xebia.xebicon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class K8SCluterStateApp {

    private static Logger logger = LoggerFactory.getLogger(K8SCluterStateApp.class);

    @Bean
    Queue queue(@Value("${rabbitMQ.queueName}") String queueName) {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange(@Value("${rabbitMQ.topicExchangeName}") String topicExchangeName) {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(@Value("${rabbitMQ.queueName}") String queueName, Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queueName);
    }

    @Bean
    SimpleMessageListenerContainer container(@Value("${rabbitMQ.queueName}") String queueName, ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    public static void main(String[] args) {
        SpringApplication.run(K8SCluterStateApp.class, args);
    }
}
