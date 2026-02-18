package JavaApi.entity;

import java.sql.Timestamp;

/**
 * sakila DB의 category 테이블과 매핑되는 엔티티입니다.
 * 애노테이션 기반 매핑 예제에 사용됩니다.
 */
public class Category {
    private Byte categoryId;
    private String name;
    private Timestamp lastUpdate;

    public Byte getCategoryId() { return categoryId; }
    public void setCategoryId(Byte categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Timestamp getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }

    @Override
    public String toString() {
        return "Category{categoryId=" + categoryId + ", name='" + name + "'}";
    }
}
