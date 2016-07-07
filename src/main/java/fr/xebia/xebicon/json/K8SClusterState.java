package fr.xebia.xebicon.json;

import java.util.List;

public class K8SClusterState {

    public List<K8SNode> nodes;

    public K8SClusterState(List<K8SNode> nodes) {
        this.nodes = nodes;
    }
}
