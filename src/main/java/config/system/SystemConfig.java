package config.system;

import config.system.database.Database;
import config.system.first.FirstSystemService;
import config.system.fourth.FourthSystemService;
import config.system.second.SecondSystemService;
import config.system.third.ThirdSystemService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;

@NoArgsConstructor
@ToString
public class SystemConfig {
    private FirstSystemService firstSystemService;
    private SecondSystemService secondSystemService;
    private ThirdSystemService thirdSystemService;
    private FourthSystemService fourthSystemService;
    private Database database;

    public <T> T getConfig(Class<T> configType) {
        Field[] fields = this.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                if (field.getType().equals(configType)) {
                    return (T) field.get(this);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing field value");
        }

        throw new RuntimeException("No config found - " + configType);
    }
}
