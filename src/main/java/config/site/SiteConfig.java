package config.site;

import config.site.first.FirstExternalService;
import config.site.second.SecondExternalService;
import config.site.third.ThirdExternalService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;

@NoArgsConstructor
@ToString
public class SiteConfig {
    private FirstExternalService firstExternalService;
    private SecondExternalService secondExternalService;
    private ThirdExternalService thirdExternalService;

    public <T> T getConfig(Class<T> configType) {
        Field[] fields = this.getClass().getDeclaredFields();
        try{
            for(Field field : fields){
                if(field.getType().equals(configType)){
                    return (T) field.get(this);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing field value", e);
        }

        throw new RuntimeException("No config found - " + configType);
    }
}
