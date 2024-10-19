package data;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;

public class SQLRequestHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLRequestHelper() {
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(System.getProperty("db.url"), System.getProperty("db.user"), System.getProperty("db.password"));
    }

    static final String msgNotFound = "Status not found";

    public static void deleteBaseRec() {
        try (Connection connect = getConnection()) {
            QUERY_RUNNER.execute(connect, "DELETE FROM credit_request_entity");
            QUERY_RUNNER.execute(connect, "DELETE FROM payment_entity");
            QUERY_RUNNER.execute(connect, "DELETE FROM order_entity ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getValuePaymentEntity() {
        try (Connection connect = getConnection()) {
            String queryStatus = "SELECT status FROM payment_entity";
            return QUERY_RUNNER.query(connect, queryStatus, new ScalarHandler<>());
        } catch (SQLException e) {
            e.printStackTrace();
            return msgNotFound;
        }
    }

    public static String getValueCreditRequestEntity() {
        try (Connection connect = getConnection()) {
            String queryStatus = "SELECT status FROM credit_request_entity";
            return QUERY_RUNNER.query(connect, queryStatus, new ScalarHandler<>());
        } catch (SQLException e) {
            e.printStackTrace();
            return msgNotFound;
        }
    }
}