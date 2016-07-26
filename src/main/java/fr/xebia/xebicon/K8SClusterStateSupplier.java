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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

        List<Node> nodes = k8sClient.nodes().list().getItems();

        List<Pod> pods = k8sClient.pods().list().getItems();

        List<NodeAndPods> nodesAndPods = groupPodsByNode(nodes, pods);

        List<K8SNode> k8sNodes = nodesAndPods.stream().map(this::buildK8sNode).collect(Collectors.toList());

        return new K8SClusterState(k8sNodes);
    }

    private List<NodeAndPods> groupPodsByNode(List<Node> nodes, List<Pod> pods) {
        Map<Node, List<Pod>> podsByNode = new HashMap<>();

        pods.stream().forEach(pod -> {
            Node node = findNodeForPod(pod, nodes);

            List<Pod> oldPods = podsByNode.get(node);
            if (oldPods == null) {
                oldPods = new ArrayList<>();
                oldPods.add(pod);
            } else {
                oldPods.add(pod);
            }
            podsByNode.put(node, oldPods);
        });

        return podsByNode.entrySet().stream().map(entry -> new NodeAndPods(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    private K8SNode buildK8sNode(NodeAndPods nodeAndPods) {
        List<K8SApp> apps = nodeAndPods.pods.stream().map(this::podToK8sApp).collect(Collectors.toList());
        return nodeToK8sNode(nodeAndPods.node, apps);
    }

    private K8SNode nodeToK8sNode(Node node, List<K8SApp> apps) {
        return new K8SNode(apps, node.getMetadata().getName(), node.getStatus().getPhase());
    }

    private K8SApp podToK8sApp(Pod pod) {
        return new K8SApp(pod.getMetadata().getName());
    }

    private Node findNodeForPod(Pod pod, List<Node> nodes) {
        List<Node> filteredNodes = nodes.stream().filter(node -> node.getMetadata().getName().equals(pod.getSpec().getNodeName())).collect(Collectors.toList());

        logger.debug("Found node [{}] for pod [{}]", filteredNodes, pod);
        return filteredNodes.get(0);
    }

    private static class NodeAndPods {
        final Node node;

        final List<Pod> pods;

        public NodeAndPods(Node node, List<Pod> pods) {
            this.node = node;
            this.pods = pods;
        }
    }
}
