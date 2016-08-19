package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class K8SMsg {

    public final String type = "K8S_STATUS";
    public final K8SClusterState payload;

    @JsonCreator
    public K8SMsg(@JsonProperty("payload") K8SClusterState payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "K8SMsg{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        K8SMsg k8SMsg = (K8SMsg) o;
        return Objects.equals(type, k8SMsg.type) &&
                Objects.equals(payload, k8SMsg.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, payload);
    }
}
