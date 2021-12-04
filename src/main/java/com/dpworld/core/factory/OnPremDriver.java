package com.dpworld.core.factory;

import com.dpworld.core.entities.ParamsEntity;
import com.dpworld.core.utils.DriverUtil;
import org.openqa.selenium.WebDriver;

public class OnPremDriver implements Driver {


    @Override
    public WebDriver setupDriver(ParamsEntity entity, String dataPath, String jsonPath) {
        return null;
    }

    @Override
    public WebDriver setupDriver(ParamsEntity entity) {
        return DriverUtil.getDriver(entity.getBrowser());
    }
}
