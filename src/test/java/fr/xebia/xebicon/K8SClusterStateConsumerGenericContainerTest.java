package fr.xebia.xebicon;

import fr.xebia.xebicon.model.K8SApp;
import fr.xebia.xebicon.model.K8SClusterState;
import fr.xebia.xebicon.model.K8SMsg;
import fr.xebia.xebicon.model.K8SNode;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {K8SCluterStateApp.class, K8SClusterStateConsumerGenericContainerTest.TestConfiguration.class})
@IntegrationTest
public class K8SClusterStateConsumerGenericContainerTest {

    @ClassRule
    public static GenericContainer rabbitmq =
            new GenericContainer("jbclaramonte/rabbitmq-testconfig:latest")
                    .withExposedPorts(5672);
    @Autowired
    Receiver receiver;
    @Autowired
    K8SClusterStateConsumer k8SClusterStateConsumer;

    @BeforeClass
    public static void beforeClass() {
        // overrides rabbitmq property with the real data comming from the container
        System.setProperty("spring.rabbitmq.port", "" + rabbitmq.getMappedPort(5672));
        System.setProperty("spring.rabbitmq.host", "" + rabbitmq.getContainerIpAddress());
    }

    @Test
    public void shouldSendToRabbitMQ() throws IOException {
        K8SApp app = new K8SApp("dummy-application");
        K8SNode node = new K8SNode(Collections.singletonList(app), "test-node", "ON");
        K8SClusterState clusterState = new K8SClusterState(Collections.singletonList(node));
        K8SMsg k8SMsg = new K8SMsg(clusterState);

        // Send the state and try to get it throw the listener
        k8SClusterStateConsumer.accept(clusterState);

        await().atMost(10, SECONDS).until(() -> assertThat(receiver.receivedMessages, hasSize(1)));

        assertThat(receiver.receivedMessages.get(0), is(equalTo(k8SMsg)));
    }

    @Configuration
    public static class TestConfiguration {

        @Bean
        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

            factory.setConnectionFactory(connectionFactory);
            factory.setMessageConverter(messageConverter);

            return factory;
        }

        @Bean
        public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
            System.out.println("-2-host:" + connectionFactory.getHost());
            System.out.println("-2-port:" + connectionFactory.getPort());

            return new RabbitAdmin(connectionFactory);
        }


        @Bean
        public FanoutExchange xebiconExchangeExchange() {
            return new FanoutExchange("xebiconExchange");
        }

        @Bean
        public Queue k8sStateObserverQueue() {
            return new Queue("k8s-state-observer", true, false, true);
        }

        @Bean
        public Binding inboundEmailExchangeBinding() {
            return BindingBuilder.bind(k8sStateObserverQueue()).to(xebiconExchangeExchange());
        }
    }

    @Component
    public static class Receiver {

        private static Logger logger = LoggerFactory.getLogger(K8SCluterStateApp.class);
        public final ArrayList<K8SMsg> receivedMessages = new ArrayList<>();

        @RabbitListener(queues = {"k8s-state-observer"})
        public void handleClusterState(K8SMsg k8SMsg) {
            logger.debug("Received message [{}]", k8SMsg);
            receivedMessages.add(k8SMsg);
        }
    }

}
