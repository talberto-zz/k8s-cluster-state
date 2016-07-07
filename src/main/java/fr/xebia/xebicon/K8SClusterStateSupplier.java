package fr.xebia.xebicon;

import fr.xebia.xebicon.model.K8SClusterState;
import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class K8SClusterStateSupplier implements Supplier<K8SClusterState> {

    private static final Logger logger = LoggerFactory.getLogger(K8SClusterStateSupplier.class);

    private final KubernetesClient k8sClient;

    @Autowired
    public K8SClusterStateSupplier(K8SClientConf k8SClientConfig) {
        logger.info("Initializing state fetcher with client conf [{}]", k8SClientConfig);

        // Init kubernetes client
        Config config = new ConfigBuilder().withMasterUrl(k8SClientConfig.masterUrl)
                .withTrustCerts(true)
                .withUsername(k8SClientConfig.user)
                .withPassword(k8SClientConfig.password)
                .withNamespace(k8SClientConfig.namespace)
                .build();

        k8sClient = new AutoAdaptableKubernetesClient(config);
    }

    @Override
    public K8SClusterState get() {
        logger.debug("Will fetch the cluster state");
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
