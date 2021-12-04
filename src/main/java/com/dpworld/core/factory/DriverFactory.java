package com.dpworld.core.factory;

import com.dpworld.core.entities.ParamsEntity;

import static com.dpworld.core.utils.SetUpType.*;

public class DriverFactory {

    public Driver getDriver(String setup) {

        if(setup.equalsIgnoreCase(REMOTE.name())) {
                return new BrowserStackDriver();
        } else if (setup.equalsIgnoreCase(DOCKER.name())) {
                return new DockerGridDriver();
        } else {
            return  new OnPremDriver();
        }
    }
}
