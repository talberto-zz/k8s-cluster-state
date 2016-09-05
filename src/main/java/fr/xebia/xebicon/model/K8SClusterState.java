package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class K8SClusterState {

    public final String type = "K8S_STATUS";
    public final K8SNode cloud;
    public final K8SNode local;

    @JsonCreator
    public K8SClusterState(@JsonProperty("cloud") K8SNode cloud, @JsonProperty("local") K8SNode local) {
        this.cloud = cloud;
        this.local = local;
    }

    @Override
    public String toString() {
        return "K8SClusterState{" +
                "type='" + type + '\'' +
                ", cloud=" + cloud +
                ", local=" + local +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        K8SClusterState that = (K8SClusterState) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(cloud, that.cloud) &&
                Objects.equals(local, that.local);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cloud, local);
    }
}
