package config.reader;



public interface ConfigReader {
    default Object readFromConfigFile(String configFilePath) {
        return null;
    }

    default void setConfigObjectClassType(Class<?> configClassType){}
}

