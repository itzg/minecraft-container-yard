package me.itzg.mccy.services;

import com.google.common.base.Optional;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.Container;
import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.types.MccyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.List;

import static com.spotify.docker.client.DockerClient.ListContainersParam.allContainers;
import static com.spotify.docker.client.DockerClient.ListContainersParam.create;
import static com.spotify.docker.client.DockerClient.ListContainersParam.withLabel;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
@Service
@Profile("!test")
public class DumpCurrentStateService {
    private static Logger LOG = LoggerFactory.getLogger(DumpCurrentStateService.class);

    @Autowired
    public void setDockerClient(DockerClientProxy dockerClientProxy) {
        try {

            dockerClientProxy.access(dockerClient -> {
                final List<Container> containers = dockerClient.listContainers(withLabel(MccyConstants.MCCY_LABEL), allContainers());

                if (containers.isEmpty()) {
                    LOG.info("NO CONTAINERS currently running");
                }
                else {
                    containers.forEach(c -> LOG.info("CURRENT CONTAINER: {}", c));
                }

                return null;
            });
        } catch (InterruptedException | DockerException e) {
            LOG.warn("Failed to access or list containers", e);
        }

    }
}
