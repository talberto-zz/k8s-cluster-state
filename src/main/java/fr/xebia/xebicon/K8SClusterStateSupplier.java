package fr.xebia.xebicon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@Component
public class K8SClusterStateSupplier implements Supplier<K8SClusterState> {

    private static final Logger logger = LoggerFactory.getLogger(K8SClusterStateSupplier.class);

    private final KubernetesClient k8sClient;

    private final K8sNodesBackupRepository k8sNodesBackupRepository;

    @Autowired
    public K8SClusterStateSupplier(KubernetesClient k8sClient,
                                   K8sNodesBackupRepository k8sNodesBackupRepository) {
        this.k8sClient = k8sClient;
        this.k8sNodesBackupRepository = k8sNodesBackupRepository;
    }

    @Override
    public K8SClusterState get() {
        logger.debug("Will fetch the cluster state");

        List<Node> nodes = k8sClient.nodes().list().getItems();

        try {
            logger.debug("raw data for nodes: {}", (new ObjectMapper()).writeValueAsString(nodes));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<Pod> pods = k8sClient.pods().list().getItems();

        List<NodeAndPods> nodesAndPods = groupPodsByNode(nodes, pods);

        List<K8SNode> k8sNodes = nodesAndPods.stream()
                .map(this::buildK8sNode)
                .collect(toList());

        Optional<List<K8SNode>> previousK8sNodes = k8sNodesBackupRepository.findPreviousK8sNodes();
        if (previousK8sNodes.isPresent()) {
            updateNodesState(previousK8sNodes.get(), k8sNodes);
        }

        k8sNodes = k8sNodes.stream()
                .sorted((o1, o2) -> o1.name.compareTo(o2.name))
                .collect(toList());

        k8sNodesBackupRepository.save(k8sNodes);

        return new K8SClusterState(k8sNodes);
    }

    public void updateNodesState(List<K8SNode> previousK8sNodes, List<K8SNode> justFetchedK8sNodes) {
        previousK8sNodes.forEach(previousK8sNode -> {
            if (!justFetchedK8sNodes.contains(previousK8sNode)) {
                logger.debug("node {} is OFF", previousK8sNode.name);
                justFetchedK8sNodes.add(new K8SNode(new ArrayList<K8SApp>(), previousK8sNode.name, "OFF"));
            }
        });
    }

    public static <T, U, R> Function<U, R> partial(BiFunction<T, U, R> biFunction, T t) {
        return u -> biFunction.apply(t, u);
    }

    private List<NodeAndPods> groupPodsByNode(List<Node> nodes, List<Pod> pods) {
        return nodes.stream()
                .map(partial(this::findPodsForNode, pods))
                .collect(toList());
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
