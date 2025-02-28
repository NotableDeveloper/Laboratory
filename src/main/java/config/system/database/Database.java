package config.system.database;

import config.system.database.members.ConnectionPool;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class Database {
    private String type = "";
    private String driver = "";
    private String url = "";
    private String username = "";
    private String password = "";
    private int waitTimeout = 0;
    private ConnectionPool connectionPool;
}
