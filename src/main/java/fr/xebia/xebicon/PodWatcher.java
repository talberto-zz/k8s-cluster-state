package fr.xebia.xebicon;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PodWatcher implements Watcher<Pod> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void eventReceived(Action action, Pod resource) {
        logger.debug("action: {}  metadata: {}", action, resource.getSpec().getContainers().get(0).getName());
        logger.debug("metadata: {}", action, resource.getSpec().getContainers().get(0).getName());
    }

    @Override
    public void onClose(KubernetesClientException cause) {

    }
}
