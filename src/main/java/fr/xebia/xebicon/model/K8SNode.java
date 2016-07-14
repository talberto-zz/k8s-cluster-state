package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class K8SNode {

    public String name;

    public String state;

    public List<K8SApp> apps;

    @JsonCreator
    public K8SNode(@JsonProperty("apps") List<K8SApp> apps, @JsonProperty("name") String name, @JsonProperty("state") String state) {
        this.apps = apps;
        this.name = name;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        K8SNode k8SNode = (K8SNode) o;

        if (!name.equals(k8SNode.name)) return false;
        if (!state.equals(k8SNode.state)) return false;
        return apps.equals(k8SNode.apps);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + apps.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "K8SNode{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", apps=" + apps +
                '}';
    }
}
