package config;

import config.reader.ConfigReader;
import config.reader.imp.StreamYamlConfigReader;
import config.site.SiteConfig;
import config.system.SystemConfig;
import config.system.database.Database;

public class Configuration {
    private final String _SYSTEM_CONFIG_FILE_PATH = System.getenv("SYSTEM_CONFIG_FILE_PATH");
    private final String _SITE_CONFIG_FILE_PATH = System.getenv("SITE_CONFIG_FILE_PATH");

    private ConfigReader configReader;
    private static SystemConfig systemRootConfig;
    private static SiteConfig siteRootConfig;

    private Configuration() {
        try {
            configReader = new StreamYamlConfigReader(SystemConfig.class);
            systemRootConfig = (SystemConfig) configReader.readFromConfigFile(_SYSTEM_CONFIG_FILE_PATH);

            configReader.setConfigObjectClassType(SiteConfig.class);
            siteRootConfig = (SiteConfig) configReader.readFromConfigFile(_SITE_CONFIG_FILE_PATH);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static class SingletonHolder{
        private static final Configuration INSTANCE = new Configuration();
    }

    private static Configuration getInstance(){ return SingletonHolder.INSTANCE; }

    public static SystemConfig getSystemConfig(){ return getInstance().systemRootConfig; }

    public static SiteConfig getSiteConfig(){ return getInstance().siteRootConfig; }
}