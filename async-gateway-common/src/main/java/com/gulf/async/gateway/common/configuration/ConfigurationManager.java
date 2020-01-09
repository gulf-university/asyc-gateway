package com.gulf.async.gateway.common.configuration;

import com.gulf.async.gateway.common.exception.GatewayException;
import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.util.ClassUtil;
import com.gulf.async.gateway.common.util.StringUtil;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xubai on 2020/01/09 5:56 PM.
 */
public final class ConfigurationManager {

    private final static Logger LOG = LoggerFactory.getInstance(ConfigurationManager.class);

    private final static String CONFIG_FILE = "gateway.properties";

    private static ConfigurationManager instance;

    private final Map<String, Configuration> configurations = new ConcurrentHashMap<>();

    private final Properties configProperties = new Properties();

    private ConfigurationManager(){
        load();
        initialize();
    }

    public static ConfigurationManager getInstance(){
        if (instance == null){
            synchronized (ConfigurationManager.class){
                if (instance == null){
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    private void load(){
        String configFile = CONFIG_FILE;
        try {
            InputStream is = ConfigurationManager.class.getClassLoader().getResourceAsStream(configFile);
            configProperties.load(is);
        } catch (Exception e) {
            throw new GatewayException("load config file:"+configFile+" exception", e);
        }
    }

    private void initialize(){
        String packeName = System.getProperty("gateway.configuration.package", "com.gulf");
        List<Class<?>> allClass = ClassUtil.getClassByInterface(packeName, Configuration.class);
        if(allClass != null){
            final Properties properties = configProperties;
            for (Class<?> clazz : allClass){
                try {
                    Configuration cf = (Configuration)clazz.newInstance();
                    if (cf != null){
                        cf.load(properties);
                        configurations.putIfAbsent(cf.name(), cf);
                    }
                } catch (Exception e) {
                    LOG.error("instance Configuration:{} exception", clazz.getCanonicalName(), e);
                }
            }
        }
    }

    public Configuration getConfiguration(String name){
        if (StringUtil.isNullOrEmpty(name)){
            return null;
        }
        return configurations.get(name);
    }
}
