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

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
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

        List<Node> nodes = k8sClient.nodes().list().getItems();

        List<Pod> pods = k8sClient.pods().list().getItems();

        List<NodeAndPods> nodesAndPods = groupPodsByNode(nodes, pods);

        List<K8SNode> k8sNodes = nodesAndPods.stream().map(this::buildK8sNode).collect(toList());

        return new K8SClusterState(k8sNodes);
    }

    public static <T, U, R> Function<U, R> partial(BiFunction<T, U, R> biFunction, T t) {
        return u -> biFunction.apply(t, u);
    }

    private List<NodeAndPods> groupPodsByNode(List<Node> nodes, List<Pod> pods) {

        return nodes.stream()
                .map(partial(this::findPodsForNode, pods))
                .collect(toList());

//        nodes.stream().forEach(node -> {
//            findPodsForNode(pods, node);
//            nodeAndPods.add(new NodeAndPods(node, pods));
//        });
//        return nodeAndPods;
    }

    private K8SNode buildK8sNode(NodeAndPods nodeAndPods) {
        List<K8SApp> apps = nodeAndPods.pods.stream().map(this::podToK8sApp).collect(toList());
        return nodeToK8sNode(nodeAndPods.node, apps);
    }

    private K8SNode nodeToK8sNode(Node node, List<K8SApp> apps) {
        return new K8SNode(apps, node.getMetadata().getName(), "ON");
    }

    private K8SApp podToK8sApp(Pod pod) {
        return new K8SApp(pod.getMetadata().getName());
    }

    private NodeAndPods findPodsForNode(List<Pod> pods, Node node) {
        List<Pod> podsForNode = pods.stream()
                .filter(pod -> pod.getSpec().getNodeName().equals(node.getMetadata().getName()))
                .collect(toList());
        return new NodeAndPods(node, podsForNode);
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
