package fr.xebia.xebicon.model;

import java.util.List;

public class K8SNode {

    public String name;

    public String state;

    public List<K8SApp> apps;

    public K8SNode(List<K8SApp> apps, String name, String state) {
        this.apps = apps;
        this.name = name;
        this.state = state;
    }
}
