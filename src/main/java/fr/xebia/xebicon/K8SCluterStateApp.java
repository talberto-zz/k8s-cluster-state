package fr.xebia.xebicon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${rabbitMQ.host}") String host,
            @Value("${rabbitMQ.user}") String user,
            @Value("${rabbitMQ.password}") String password) {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(host);
        connectionFactory.setUsername(user);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(K8SCluterStateApp.class, args);
    }
}
