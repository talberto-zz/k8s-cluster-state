package fr.xebia.xebicon;

import fr.xebia.xebicon.model.K8SApp;
import fr.xebia.xebicon.model.K8SClusterState;
import fr.xebia.xebicon.model.K8SNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {K8SCluterStateApp.class, K8SClusterStateConsumerTest.TestConfiguration.class})
@IntegrationTest
public class K8SClusterStateConsumerTest {

    @Configuration
    public static class TestConfiguration {
        @Bean
        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

            factory.setConnectionFactory(connectionFactory);
            factory.setMessageConverter(messageConverter);

            return factory;
        }
    }

    @Component
    public static class Receiver {

        public final ArrayList<K8SClusterState> receivedMessages = new ArrayList<>();

        private static Logger logger = LoggerFactory.getLogger(K8SCluterStateApp.class);

        @RabbitListener(queues = {"${rabbitmq.routingKey}"})
        public void handleClusterState(K8SClusterState state) {
            logger.debug("Received message [{}]", state);
            receivedMessages.add(state);
        }
    }

    @Autowired
    Receiver receiver;

    @Autowired
    K8SClusterStateConsumer k8SClusterStateConsumer;

    @Test
    public void shouldSendToRabbitMQ() throws IOException {
        K8SApp app = new K8SApp("dummy-application");
        K8SNode node = new K8SNode(Collections.singletonList(app), "test-node", "ON");
        K8SClusterState clusterState = new K8SClusterState(Collections.singletonList(node));

        // Send the state and try to get it throw the listener
        k8SClusterStateConsumer.accept(clusterState);

        await().atMost(10, SECONDS).until(() -> assertThat(receiver.receivedMessages, hasSize(1)));

        assertThat(receiver.receivedMessages.get(0), is(equalTo(clusterState)));
    }

}
