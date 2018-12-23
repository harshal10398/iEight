package com.iEight.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StaticDatabaseConnectionHolder {
    private static Connection connection;
    private static Statement statement;

    private static void initConnection() {
        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver loaded!");

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loom", "application", "1StackOn!application");

        } /*catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }*/ catch (SQLException sqle) {
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
    public static ArrayNode getResult(String queryString){
        ResultSet rs = null;
        try {
            rs = getStatement().executeQuery(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ResultSetToJson.getJSON(rs);
    }

}
