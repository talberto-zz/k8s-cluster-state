package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class K8SMsg {

    private String type;
    private K8SClusterState payload;

    public K8SMsg() {
    }

    public K8SMsg(@JsonProperty("payload") K8SClusterState payload) {
        type= "K8S_STATUS";
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public K8SClusterState getPayload() {
        return payload;
    }

    public void setPayload(K8SClusterState payload) {
        this.payload = payload;
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
