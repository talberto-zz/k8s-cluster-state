package fr.xebia.xebicon;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class K8SClusterStateWatcher {

    private static final Logger logger = LoggerFactory.getLogger(K8SClusterStateWatcher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    KubernetesClient kubernetesClient;

    @PostConstruct
    public void watch() {
        kubernetesClient.pods().inNamespace("default").watch(new PodWatcher());
//        kubernetesClient.replicationControllers().inNamespace("default").watch(new Watcher<ReplicationController>() {
//            @Override
//            public void eventReceived(Action action, ReplicationController resource) {
//                logger.info("{}: {}", action, resource.getMetadata().getResourceVersion());
//            }
//
//            @Override
//            public void onClose(KubernetesClientException e) {
//                if (e != null) {
//                    logger.error(e.getMessage(), e);
//                }
//            }
//        });
    }

}
