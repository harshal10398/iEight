package com.iEight.util;

//import org.json.JSONArray;
//import org.json.JSONObject;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetToJson {
    public static ObjectNode getJSON(ResultSet resultSet){
        return getJSON(resultSet,"records");
    }
    public static ObjectNode getJSON(ResultSet resultSet, String fieldName) {

        ObjectMapper objectMapper=new ObjectMapper();
        ObjectNode returnNode = objectMapper.createObjectNode();
        ArrayNode arrayNode=objectMapper.createArrayNode();

        try {
            int totalColumns = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {

                // for each record, create a new JSON object
                ObjectNode objectNode=objectMapper.createObjectNode();

                for (int i = 0; i < totalColumns; i++) {

                    // for each cell, put value with columnname in object

                    objectNode.put(
                            resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(),
                            String.valueOf(resultSet.getObject(i + 1))
                    );
                }

                // put early made JOSN object into JSON array
                arrayNode.add(objectNode);

            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        returnNode.set(fieldName,arrayNode);
        return returnNode;
    }
    public static JsonNode getOk(){
        ObjectMapper objectMapper=new ObjectMapper();
        ObjectNode objectNode=objectMapper.createObjectNode();
        objectNode.put("ok",true);
        return objectNode;
    }

    public static JsonNode getResponse(int code, String message){
        ObjectMapper objectMapper=new ObjectMapper();
        ObjectNode objectNode=objectMapper.createObjectNode().
                put("state",code).
                put("message",message);
        return objectNode;
    }
}
