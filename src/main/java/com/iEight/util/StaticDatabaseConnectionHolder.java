package com.iEight.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class StaticDatabaseConnectionHolder {
    private static Connection connection;
    private static Statement statement;

    private static void initConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql//localhost:3306/loom", "application", "1StackOn!application");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (connection == null)
            initConnection();
        return connection;
    }

    public static Statement getStatement() {
        try {
            if (statement == null)
                statement = getConnection().createStatement();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return statement;
    }
}
