package me.itzg.mccy.controllers;

import com.spotify.docker.client.DockerException;
import me.itzg.mccy.model.ContainerDetails;
import me.itzg.mccy.model.ContainerRequest;
import me.itzg.mccy.model.ContainerSummary;
import me.itzg.mccy.model.SingleValue;
import me.itzg.mccy.services.ContainersService;
import me.itzg.mccy.types.MccyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
@RestController
@RequestMapping("/api/containers")
public class ApiContainersController {

    @Autowired
    private ContainersService containers;

    @RequestMapping(method = RequestMethod.GET)
    public List<ContainerSummary> getAllMccyContainers(Authentication auth)
            throws DockerException, InterruptedException {

        if (auth != null) {
            return containers.getAll(getAuthUsername(auth));
        }
        else {
            return containers.getAllPublic();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public SingleValue<String> createContainer(@RequestBody @Valid ContainerRequest request,
                                               Authentication auth) throws MccyException, DockerException, InterruptedException {

        return SingleValue.of(containers.create(request, getAuthUsername(auth)));

    }

    @RequestMapping(value = "/{containerId}", method = RequestMethod.GET)
    public ContainerDetails getContainer(@PathVariable("containerId") String containerId) throws DockerException, InterruptedException {
        return containers.get(containerId);
    }

    @RequestMapping(value = "/{containerId}", method = RequestMethod.DELETE)
    public void deleteContainer(@PathVariable("containerId") String containerId) throws DockerException, InterruptedException {
        containers.delete(containerId);
    }

    @RequestMapping(value = "/{containerId}/_start", method = RequestMethod.POST)
    public void startContainer(@PathVariable("containerId") String containerId) throws DockerException, InterruptedException {
        containers.start(containerId);
    }

    @RequestMapping(value = "/{containerId}/_stop", method = RequestMethod.POST)
    public void stopContainer(@PathVariable("containerId") String containerId) throws DockerException, InterruptedException {
        containers.stop(containerId);
    }

    private static String getAuthUsername(Authentication auth) {
        if (auth == null) {
            return null;
        }
        final User user = (User) auth.getPrincipal();
        return user.getUsername();
    }
}
