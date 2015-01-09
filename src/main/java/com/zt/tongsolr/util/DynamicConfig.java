package com.zt.tongsolr.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import java.util.List;

public class DynamicConfig {

    public static final String MODEL_FEATURE = "model.feature";
    private static PropertiesConfiguration propertiesConfig = null;

    static {
        try {
            PropertiesConfiguration.setDefaultListDelimiter('-');
            propertiesConfig = new PropertiesConfiguration("application.properties");
            propertiesConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Boolean getBoolean(String key) {
        return propertiesConfig.getBoolean(key, false);
    }

    public static Double getDouble(String key) {
        return propertiesConfig.getDouble(key, -1);
    }

    public static Float getFloat(String key) {
        return propertiesConfig.getFloat(key, -1);
    }

    public static Integer getInteger(String key) {
        return propertiesConfig.getInt(key, -1);
    }

    public static Long getLong(String key) {
        return propertiesConfig.getLong(key, -1);
    }

    public static String getString(String key) {
        return propertiesConfig.getString(key, "");
    }

    public static List<String> getStringList(String key) {
        return propertiesConfig.getList(key);
    }
}
