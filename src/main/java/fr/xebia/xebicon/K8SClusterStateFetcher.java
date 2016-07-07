package fr.xebia.xebicon;

import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class K8SClusterStateFetcher {

    private static final Logger logger = LoggerFactory.getLogger(K8SClusterStateFetcher.class);

    private KubernetesClient k8sClient;

    @Autowired
    public K8SClusterStateFetcher(K8SClientConf k8SClientConfig) {
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

    @Scheduled(fixedRate = 1000)
    public void scan() {
        // Fetch the list of nodes
        k8sClient.nodes().list();
    }
}
