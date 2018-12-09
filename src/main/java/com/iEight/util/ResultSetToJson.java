package com.iEight.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetToJson {
    public static JSONArray getJSON(ResultSet resultSet) {

        JSONArray jsonArray = new JSONArray();
        try {
            int totalColumns = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {

                // for each record, create a new JSON object
                JSONObject jsonObject = new JSONObject();

                for (int i = 0; i < totalColumns; i++) {

                    // for each cell, put value with columnname in object

                    jsonObject.put(
                            resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(),
                            resultSet.getObject(i + 1)
                    );
                }

                // put early made JOSN object into JSON array
                jsonArray.put(jsonObject);

            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        System.out.println(jsonArray.toString(4));
        return jsonArray;
    }
}
