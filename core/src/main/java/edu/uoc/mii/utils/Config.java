package edu.uoc.mii.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import java.io.IOException;

/**
 *
 * @author Marco Rodriguez
 */
public class Config {

    private ObjectMap<String, String> props;

    public Config(String internalPath) {
        props = new ObjectMap<>();
        load(internalPath);
    }

    private void load(String path) {
        try {
            PropertiesUtils.load(props, Gdx.files.internal(path).reader("UTF-8"));
        } catch (IOException e) {
            Gdx.app.error("Config", "Error fatal cargando config: " + path, e);
        }
    }

    public String getString(String key, String defaultValue) {
        return props.get(key, defaultValue);
    }
    
    public int getInt(String key, int defaultValue) {
        String value = props.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }    

    public float getFloat(String key, float defaultValue) {
        String value = props.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = props.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}
