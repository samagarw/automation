package com.dpworld.core.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class PropertiesUtil {

    Properties props;

    public PropertiesUtil(){
       props = new Properties();
    }

    public  void loadProperty(String path) {

        try {
            props.load(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    /**************************************************************
     * @return returns properties value from properties file.
     *
     *************************************************************/

    /**
     *
     * @param value - Get property field
     * @return propertyValue - Value of property
     */
    public  String getProperty_(String value) {
        String propertyValue = null;
        try {
            propertyValue = props.getProperty(value);
        } catch (Exception e) {
            e.printStackTrace();
            propertyValue = "default";
        }
        return propertyValue;
    }

    /**************************************************************
     * @return returns properties value from properties file.
     *************************************************************/

    /**
     *
     * @param value - Get property field
     * @return propertyValue - Value of property
     */
    public  String getProperty(String value) {
        String propertyValue = null;
        String env_prop_to_load = null;
        try {
            env_prop_to_load = props.getProperty("env_to_use");
            props.load(new FileInputStream(env_prop_to_load));
            propertyValue = props.getProperty(value);
        } catch (Exception e) {
            // e.printStackTrace();
            propertyValue = "default";
        }
        return propertyValue;
    }

    /************************************************************
     * sets values in the properties file.
     **********************************************************/

    /**
     *
     * @param key - Setting key property
     * @param value - Setting key with this value
     */
    public  void setProperty(String key, String value) {
        String env_prop_to_load = props.getProperty("env_to_use");
        props.setProperty(key, value);
        saveProperties(props, env_prop_to_load);
    }

    /**************************************************************
     * saves values to the properties file before processing. Example set system
     * date in properties file.
     **************************************************************/

    /**
     * @param p : Properties file
     * @param fileName :  Saved file name
     */
    public  void saveProperties(Properties p, String fileName) {
        OutputStream outPropFile;
        try {
            outPropFile = new FileOutputStream(fileName);
            p.store(outPropFile, "Properties File to the Test Application");
            outPropFile.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

		/*
		 * A better way to do this is to sort the properties before writing them out.
		 */
        List<String> unSortedProperties = new ArrayList<String>();
        try {
            unSortedProperties = FileUtils.readLines(new File(fileName), "utf-8");
            Collections.sort(unSortedProperties);
            // now the properties are sorted
            FileUtils.writeLines(new File(fileName+".sorted"), unSortedProperties);
        } catch (IOException e) {
            System.out.println("Error reading properties file:" + e.getMessage());
            e.printStackTrace();
        }
    }

}
