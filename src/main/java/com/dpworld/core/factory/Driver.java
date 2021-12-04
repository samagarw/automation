package com.dpworld.core.factory;

import com.dpworld.core.dataProviders.TestData;
import com.dpworld.core.entities.ParamsEntity;
import org.openqa.selenium.WebDriver;

public interface Driver {

    public WebDriver setupDriver(ParamsEntity entity);

    public WebDriver setupDriver(ParamsEntity entity,String dataPath,String jsonPath);
}
