package util;

import java.sql.SQLException;

public final class SqlExceptions {
    private SqlExceptions() {
    }

    public static boolean isDuplicateKey(SQLException ex) {
        return ex.getErrorCode() == 1062;
    }

    public static boolean isReferencedRow(SQLException ex) {
        return ex.getErrorCode() == 1451;
    }

    public static boolean isMissingParent(SQLException ex) {
        return ex.getErrorCode() == 1452;
    }
}
