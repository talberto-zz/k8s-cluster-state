package fr.xebia.xebicon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class K8SClientConf {

    public final String masterUrl;

    public final String user;

    public final String password;

    public final String namespace;

    @Autowired
    public K8SClientConf(
            @Value("${k8s.masterUrl}") String masterUrl,
            @Value("${k8s.user}") String user,
            @Value("${k8s.password}") String password,
            @Value("${k8s.namespace}") String namespace
    ) {
        this.masterUrl = masterUrl;
        this.user = user;
        this.password = password;
        this.namespace = namespace;
    }

    @Override
    public String toString() {
        return "K8SClientConf{" +
                "masterUrl='" + masterUrl + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", namespace='" + namespace + '\'' +
                '}';
    }
}
