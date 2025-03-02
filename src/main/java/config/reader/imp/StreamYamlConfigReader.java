package config.reader.imp;

import config.reader.ConfigReader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * YAML 파일 내의 Document를 하나씩 읽어가며 설정 인스턴스를 생성하는 클래스
 * @author seongjin
 */
public class StreamYamlConfigReader implements ConfigReader {
    private Yaml yaml;
    private Class<?> configObjectClassType;

    public StreamYamlConfigReader(Class<?> configType) {
        this.configObjectClassType = configType;
    }

    /**
     * 읽어들일 Config Class의 타입(System or Site)을 설정하는 메서드이다.
     * @param configType SystemConfig.class or SiteConfig.class
     */
    @Override
    public void setConfigObjectClassType(Class<?> configType) {
        this.configObjectClassType = configType;
    }

    /**
     * YAML 파일 내의 Document를 하나씩 읽고, Map<String, Object> 형태로 가공하여
     * 설정 정보 인스턴스와 멤버 필드를 초기화하는 메서드이다.
     *
     * 인스턴스 생성 과정에서 YAML 파일 내에 Key는 존재하지만 Value가 비어있는 경우와
     * Key 자체가 존재하지 않는 경우에 대한 검사를 실시한다.
     *
     * @param configFilePath YAML 파일이 위치한 경로
     * @return 최상위 클래스인 Object 타입의 설정(SystemConfig or SiteConfig) 정보가 담긴 인스턴스
     */

    @Override
    public Object readFromConfigFile(String configFilePath) {
        yaml = new Yaml();
        yaml.setBeanAccess(BeanAccess.FIELD);

        // YAML 파일을 읽어서 내용을 채우고, 반환할 비어있는 객체 생성
        Object configInstance = createConfigInstance();
        System.out.println("Reading config file: " + configFilePath);

        try (InputStream inputStream = new FileInputStream(configFilePath)) {
            for (Object rawData : yaml.loadAll(inputStream)) {
                if(rawData == null) {
                    throw new RuntimeException();
                }

                // YAML 파일에서 읽은 Raw data를 Map 타입으로 캐스팅하여 인스턴스의 멤버 변수에 값 초기화
                if (rawData instanceof Map) initializeConfigInstance(configInstance, (Map<String, Object>) rawData);
            }
        } catch (IOException e) {
            System.err.println("Error reading config file: " + configFilePath);
            throw new RuntimeException();
        } catch (ParserException e){
            System.err.println("Check config file to syntax - " + configFilePath);
            throw new RuntimeException();
        }

        return configInstance;
    }

    /**
     * YAML 파일의 내용을 읽어서 반환하기 위한 Config 인스턴스를 생성하는 메서드이다.
     * @return 값이 비어있는 SystemConfig 혹은 SiteConfig 인스턴스
     */
    private Object createConfigInstance() {
        try {
            return configObjectClassType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Failed to instantiate config class - " + e.getMessage());
            throw new RuntimeException();
        }
    }

    /**
     * 값이 비어있는 SystemConfig 혹은 SiteConfig 인스턴스를 초기화하기 위한 메서드이다.
     * @param configInstance 비어있는 SystemConfig 혹은 SiteConfig 인스턴스
     * @param dataMap YAML 파일에서 읽은 raw data를 Map으로 캐스팅한 인스턴스
     */
    private void initializeConfigInstance(Object configInstance, Map<String, Object> dataMap) {
        // SystemConfig 혹은 SiteConfig의 멤버 변수를 불러온다.
        Field[] parentFields = configObjectClassType.getDeclaredFields();

        /*
            SystemConfig 혹은 SiteConfig에 정의된 필드와 YAML 파일에서 읽은 raw data 내의
            필드 내용을 비교한다.
        */
        for (Field parentField : parentFields) {
            validateParentField(parentField, dataMap);
        }

        /*
            raw data 내에 비어있는 값이 있는 지를 확인(= YAML 파일 내에서 키는 있으나, 값이 없는 경우)하고,
            raw data의 값을 인스턴스에 주입한다.
         */
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            setFieldValueIfPresent(configInstance, parentFields, entry);
        }
    }

    /**
     * SystemConfig 혹은 SiteConfig에 정의된 필드와 YAML 파일에서 읽은 데이터 내의 필드 값을 비교하여
     * 유효한 필드를 읽었는 지를 검사하는 메서드이다.
     *
     * @param parentField SystemConfig 혹은 SiteConfig의 멤버 필드
     * @param dataMap YAML 파일에서 읽은 raw data를 Map으로 캐스팅한 인스턴스
     */
    private void validateParentField(Field parentField, Map<String, Object> dataMap) {
        if (dataMap.containsKey(parentField.getName())) {
            Object parentValue = dataMap.get(parentField.getName());

            if (parentValue instanceof Map) {
                validateChildFields(parentField,(Map<String, Object>) parentValue);
            }
        }
    }

    /**
     * SystemConfig 혹은 SiteConfig에 정의된 필드의 멤버 필드와 YAML 파일에서 읽은 데이터 내의 필드 값을 비교하여
     * 유효한 필드를 읽었는 지를 검사하는 메서드이다.
     *
     * @param parentField SystemConfig 혹은 SiteConfig의 멤버 필드
     * @param childDataMap YAML 파일에서 읽은 raw data를 Map으로 캐스팅한 인스턴스
     */
    private void validateChildFields(Field parentField, Map<String, Object> childDataMap) {
        Field[] childFields = parentField.getType().getDeclaredFields();

        for (Field childField : childFields) {
            // Raw data가 String 형태이므로, String 내에 필드 이름이 들어가는 지를 확인한다.
            if (!childDataMap.containsKey(childField.getName())) {
                System.err.println("Not Found child field : " + childField.getName() + " in parent field: " + parentField.getName());
                throw new RuntimeException();
            }
        }
    }

    /**
     * 유효성 검사 이후에, 알맞은 필드에 값을 주입하는 메서드이다.
     *
     * @param configInstance SystemConfig 혹은 SiteConfig의 인스턴스
     * @param parentFields SystemConfig 혹은 SiteConfig의 멤버 필드
     * @param entry YAML 파일로부터 읽은 Raw data를 Map.Entry로 컨버팅한 인스턴스
     */
    private void setFieldValueIfPresent(Object configInstance, Field[] parentFields, Map.Entry<String, Object> entry) {
        for (Field field : parentFields) {
            // SystemConfig 혹은 SiteConfig의 멤버 필드에서 일치하는 값을 찾은 경우에 값을 주입하고, 바로 반복문을 탈출한다.
            if (field.getName().equals(entry.getKey())) {
                setFieldValue(configInstance, field, entry.getValue());
                break;
            }
        }
    }

    /**
     * 입력으로 들어온 타입과 값을 바탕으로 인스턴스에 값을 주입하는 메서드이다.
     *
     * @param configInstance SystemConfig 혹은 SiteConfig의 인스턴스
     * @param field configInstance 내에 있는 필드 이름
     * @param value configInstance 내에 있는 필드에 주입하려는 값
     */
    private void setFieldValue(Object configInstance, Field field, Object value) {
        field.setAccessible(true);

        try {
            if (field.getType().isAssignableFrom(Map.class)) {
                field.set(configInstance, value);
            } else {
                field.set(configInstance, convertMapToObject((Map<String, Object>) value, field.getType()));
            }
        } catch (IllegalAccessException e) {
            System.err.println("Failed to set value for field: " + field.getName());
            throw new RuntimeException();
        }
    }

    /**
     * Map 형태의 데이터를 객체 형태로 변환하는 메서드이다.
     *
     * @param map YAML 파일로부터 읽은 Raw data를 Map 형태로 컨버팅한 인스턴스
     * @param targetType 변환하려는 객체의 타입
     * @return
     */
    private Object convertMapToObject(Map<String, Object> map, Class<?> targetType) {
        try {
            Object instance = targetType.newInstance();

            for (Field field : targetType.getDeclaredFields()) {
                field.setAccessible(true);

                if (map.containsKey(field.getName())) {
                    Object value = map.get(field.getName());

                    if (value instanceof Map && !field.getType().isAssignableFrom(Map.class)) {
                        value = convertMapToObject((Map<String, Object>) value, field.getType());
                    } else {
                        value = convertValueToFieldType(value, field.getType());
                    }

                    if (value != null) {
                        field.set(instance, value);
                    }

                    else {
                        System.err.println("Value is empty, setting default value to field : " + field.getName());
                    }
                }
            }

            return instance;

        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Failed to convert map to object of type: " + targetType.getName());
            throw new RuntimeException();
        }
    }

    /**
     * 값을 필드 타입에 맞게 변환하는 메서드이다.
     *
     * @param value 변환하려는 값
     * @param fieldType 변환하려는 필드
     * @return fieldType에 알맞은 value가 주입된 인스턴스
     */
    private Object convertValueToFieldType(Object value, Class<?> fieldType) {
        if (value == null) {
            return null;
        }

        if (fieldType.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (fieldType.equals(String.class)) {
            return String.valueOf(value);
        }

        if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            return Integer.parseInt(value.toString());
        }

        if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            return Boolean.parseBoolean(value.toString());
        }

        if (fieldType.isArray()) {
            return convertListToArray(value, fieldType.getComponentType());
        }

        System.err.println("Cannot convert value: " + value + " to type: " + fieldType.getName());
        throw new RuntimeException();
    }

    /**
     * List 형태로 입력된 값을 Array 형태의 인스턴스로 변환하는 메서드이다.
     *
     * @param value 변환하려는 List 값
     * @param componentType 변환하려는 Array의 타입
     * @return Array 타입에 맞도록 List 값이 포함된 인스턴스
     */
    private Object convertListToArray(Object value, Class<?> componentType) {
        if (!(value instanceof List)) {
            throw new RuntimeException("Expected a List but got: " + value.getClass().getName());
        }

        List<?> list = (List<?>) value;

        // Handle String[] arrays
        if (componentType.equals(String.class)) {
            return list.stream().map(Object::toString).toArray(String[]::new);
        }

        // Handle Integer[] arrays
        if (componentType.equals(Integer.class)) {
            return list.stream()
                    .map(item -> Integer.parseInt(item.toString()))
                    .toArray(Integer[]::new);
        }

        // Handle int[] arrays (primitive types need special handling)
        if (componentType.equals(int.class)) {
            int[] result = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = Integer.parseInt(list.get(i).toString());
            }
            return result;
        }

        // Add more array types as needed
        System.err.println("Unsupported array component type: " + componentType.getName());
        throw new RuntimeException();
    }
}