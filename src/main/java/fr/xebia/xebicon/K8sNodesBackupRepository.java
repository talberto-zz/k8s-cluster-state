package fr.xebia.xebicon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.xebia.xebicon.model.K8SNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class K8sNodesBackupRepository {

    private static final Logger logger = LoggerFactory.getLogger(K8sNodesBackupRepository.class);

    private Optional<List<K8SNode>> k8SNodes = Optional.empty();

    public Optional<List<K8SNode>> findPreviousK8sNodes() {
        return k8SNodes;
    }

    public void save(List<K8SNode> aK8SNodes) {
        try {
            logger.debug("will save as backup following k8sNodes: {}", (new ObjectMapper()).writeValueAsString(aK8SNodes));
        } catch (JsonProcessingException e) {
        }
        this.k8SNodes = Optional.ofNullable(aK8SNodes);
    }

}
