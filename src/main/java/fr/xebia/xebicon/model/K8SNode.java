package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class K8SNode {

    public final boolean active;
    public final List<K8SApp> apps;

    @JsonCreator
    public K8SNode(@JsonProperty("apps") List<K8SApp> apps, @JsonProperty("active") boolean active) {
        this.apps = apps;
        this.active = active;
    }

    @Override
    public String toString() {
        return "K8SNode{" +
                "active=" + active +
                ", apps=" + apps +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        K8SNode k8SNode = (K8SNode) o;
        return active == k8SNode.active &&
                Objects.equals(apps, k8SNode.apps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, apps);
    }
}
