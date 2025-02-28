package config.reader.imp;

import config.reader.ConfigReader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.FileInputStream;
import java.io.InputStream;

public class BatchYamlConfigReader implements ConfigReader {
    private Yaml                _YAML;
    private Class               _ConfigObjectClassType;

    public BatchYamlConfigReader(Class<?> configType){
        this._ConfigObjectClassType = configType;
    }

    @Override
    public void setConfigObjectClassType(Class<?> configType){
        this._ConfigObjectClassType = configType;
    }

    @Override
    public Object readFromConfigFile(String configFilePath) {
        _YAML = new Yaml();
        Object result = null;
        _YAML.setBeanAccess(BeanAccess.FIELD);

        try {
            InputStream inputStream = new FileInputStream(configFilePath);
            result = _YAML.loadAs(inputStream, _ConfigObjectClassType);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
}