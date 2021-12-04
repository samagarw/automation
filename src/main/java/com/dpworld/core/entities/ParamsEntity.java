package com.dpworld.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ParamsEntity {

    String env;
    String testType;
    int env_cap_id;
    String browser;
    String setup;
    String suiteType;
    boolean mobile;
    boolean app;
    String appType;
    String build;
    String os;
    String device;
    String name;

}
