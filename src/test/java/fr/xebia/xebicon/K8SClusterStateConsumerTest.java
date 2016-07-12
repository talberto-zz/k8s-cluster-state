package fr.xebia.xebicon;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.xebia.xebicon.model.K8SApp;
import fr.xebia.xebicon.model.K8SClusterState;
import fr.xebia.xebicon.model.K8SNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        SimpleMessageListenerContainer container(@Value("${rabbitMQ.queueName}") String queueName, ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setQueueNames(queueName);
            container.setMessageListener(listenerAdapter);
            return container;
        }

        @Bean
        MessageListenerAdapter listenerAdapter(Receiver receiver) {
            return new MessageListenerAdapter(receiver, "receiveMessage");
        }
    }

    @Component
    public static class Receiver {

        public final ArrayList<String> receivedMessages = new ArrayList<>();
    }

    @Autowired
    Receiver receiver;

    @Autowired
    K8SClusterStateConsumer k8SClusterStateConsumer;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldSendToRabbitMQ() throws IOException {
        K8SApp app = new K8SApp("dummy-application");
        K8SNode node = new K8SNode(Collections.singletonList(app), "test-node", "ON");
        K8SClusterState clusterState = new K8SClusterState(Collections.singletonList(node));

        // Send the state and try to get it throw the listener
        k8SClusterStateConsumer.accept(clusterState);

        await().atMost(30, SECONDS).until(() ->
                assertThat(receiver.receivedMessages, hasSize(1))
        );

        // Extract the received message and parse it
        K8SClusterState actualClusterState = objectMapper.readValue(receiver.receivedMessages.get(0), K8SClusterState.class);

        assertThat(actualClusterState, is(equalTo(clusterState)));
    }

}
