package fr.xebia.xebicon;

import fr.xebia.xebicon.model.K8SApp;
import fr.xebia.xebicon.model.K8SClusterState;
import fr.xebia.xebicon.model.K8SNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = K8SCluterStateApp.class)
public class K8SClusterStateConsumerTest {

    public class Receiver {

        private CountDownLatch latch = new CountDownLatch(1);

        public void receiveMessage(String message) {
            System.out.println("Received <" + message + ">");
            latch.countDown();
        }

        public CountDownLatch getLatch() {
            return latch;
        }
    }

    @Autowired
    K8SClusterStateConsumer k8SClusterStateConsumer;

    @Test
    public void shouldSendToRabbitMQ() {
        K8SApp app = new K8SApp("dummy-application");
        K8SNode node = new K8SNode(Collections.singletonList(app), "test-node", "ON");
        K8SClusterState clusterState = new K8SClusterState(Collections.singletonList(node));

        // Send the state and try to get it throw the listener
        k8SClusterStateConsumer.accept(clusterState);


    }

}
