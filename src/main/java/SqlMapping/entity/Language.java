package SqlMapping.entity;

import java.sql.Timestamp;

/**
 * sakila DB의 language 테이블과 매핑되는 엔티티 클래스입니다.
 * film 과의 1:1 association 예제에 사용됩니다.
 */
public class Language {
    private Byte languageId;
    private String name;
    private Timestamp lastUpdate;

    public Byte getLanguageId() { return languageId; }
    public void setLanguageId(Byte languageId) { this.languageId = languageId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Timestamp getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }

    @Override
    public String toString() {
        return "Language{languageId=" + languageId + ", name='" + name + "'}";
    }
}
