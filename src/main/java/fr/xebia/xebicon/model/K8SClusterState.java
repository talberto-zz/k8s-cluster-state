package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class K8SClusterState {

    public final String type = "K8S_STATUS";
    public final List<K8SNode> payload;

    @JsonCreator
    public K8SClusterState(@JsonProperty("payload") List<K8SNode> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "K8SClusterState{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        K8SClusterState k8SClusterState = (K8SClusterState) o;

        if (type != null ? !type.equals(k8SClusterState.type) : k8SClusterState.type != null) return false;
        return payload != null ? payload.equals(k8SClusterState.payload) : k8SClusterState.payload == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }
}
