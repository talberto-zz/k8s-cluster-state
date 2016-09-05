package fr.xebia.xebicon;

import fr.xebia.xebicon.model.K8SApp;
import fr.xebia.xebicon.model.K8SClusterState;
import fr.xebia.xebicon.model.K8SNode;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@Component
public class K8SClusterStateSupplier implements Supplier<K8SClusterState> {

    private static final Logger logger = LoggerFactory.getLogger(K8SClusterStateSupplier.class);

    private final KubernetesClient k8sClient;

    @Autowired
    public K8SClusterStateSupplier(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    @Override
    public K8SClusterState get() {
        logger.debug("Will fetch the cluster state");

        Optional<Node> maybeCloudNode = findCloudNode();
        Optional<Node> maybeLocalNode = findLocalNode();

        List<Pod> pods = findPods();

        K8SNode k8sCloudNode = buildK8sNode(maybeCloudNode, pods);
        K8SNode k8sLocalNode = buildK8sNode(maybeLocalNode, pods);

        return new K8SClusterState(k8sCloudNode, k8sLocalNode);
    }

    private List<Pod> findPods() {
        return k8sClient.pods().list().getItems();
    }

    private Optional<Node> findCloudNode() {
        List<Node> cloudNodes = k8sClient.nodes().withLabel("type", "cloud").list().getItems();

        if (cloudNodes.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(cloudNodes.get(0));
        }
    }

    private Optional<Node> findLocalNode() {
        List<Node> cloudNodes = k8sClient.nodes().withLabel("type", "local").list().getItems();

        if (cloudNodes.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(cloudNodes.get(0));
        }
    }

    private K8SNode buildK8sNode(Optional<Node> maybeNode, List<Pod> pods) {
        if (maybeNode.isPresent()) {
            Node node = maybeNode.get();
            List<K8SApp> apps = pods.stream()
                    .filter(pod -> pod.getSpec().getNodeName().equals(node.getMetadata().getName()))
                    .filter(pod -> pod.getMetadata().getLabels().containsKey("display"))
                    .map(pod -> new K8SApp(pod.getMetadata().getName())).collect(toList());
            return new K8SNode(apps, false);
        } else {
            return new K8SNode(Collections.emptyList(), false);
        }
    }
}
