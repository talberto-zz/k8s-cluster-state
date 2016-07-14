package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class K8SClusterState {

    public List<K8SNode> nodes;

    @JsonCreator
    public K8SClusterState(@JsonProperty("nodes") List<K8SNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        K8SClusterState that = (K8SClusterState) o;

        return nodes.equals(that.nodes);

    }

    @Override
    public int hashCode() {
        return nodes.hashCode();
    }

    @Override
    public String toString() {
        return "K8SClusterState{" +
                "nodes=" + nodes +
                '}';
    }
}
