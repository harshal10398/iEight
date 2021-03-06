package com.iEight.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.*;

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
    public static ObjectNode getResult(String queryString){
        ResultSet rs = null;
        try {
            rs = getStatement().executeQuery(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ResultSetToJson.getJSON(rs);
    }
    public static PreparedStatement getPreparedStatement(String queryString) {

        PreparedStatement returnStatement=null;
        try {
            returnStatement = getConnection().prepareStatement(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnStatement;
    }
}
