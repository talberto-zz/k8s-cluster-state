package fr.xebia.xebicon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Bean
public class K8SClientConf {

    public final String masterUrl;

    public final String user;

    public final String password;

    public final String namespace;

    @Autowired
    public K8SClientConf(
            @Value("masterUrl") String masterUrl,
            @Value("user") String user,
            @Value("password") String password,
            @Value("namespace") String namespace
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
