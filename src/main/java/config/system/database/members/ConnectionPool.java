package config.system.database.members;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ConnectionPool {
    private int maximumPool = 0;
    private boolean cachePrepStmts = false;
    private int prepStmtCacheSize = 0;
    private int prepStmtCacheSqlLimit = 0;
}
