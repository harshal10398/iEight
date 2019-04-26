package com.iEight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iEight.util.ResultSetToJson;
import com.iEight.util.StaticDatabaseConnectionHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class Taaka {
    private static String getAllTaakasQuery = "SELECT T.TAAKA_NUMBER AS TAAKA_NUMBER, T.PRODUCTION_DATE AS PRODUCTION_DATE, T.TAAKA_LENGTH AS TAAKA_LENGTH, Q.QUALITY_TEXT AS QUALITY_TEXT, Q.QUALITY_ID AS QUALITY_ID, T.BILL_NO AS BILL_NO FROM TAAKA T, TAAKA_QUALITY Q WHERE T.QUALITY_ID = Q.QUALITY_ID ORDER BY BILL_NO ";
    private static String getAllTaakasWithBillQuery = "SELECT T.TAAKA_NUMBER as TAAKA_NUMBER, T.PRODUCTION_DATE AS PRODUCTION_DATE, T.TAAKA_LENGTH AS TAAKA_LENGTH, Q.QUALITY_TEXT AS QUALITY_TEXT, T.BILL_NO AS BILL_NO FROM TAAKA T,TAAKA_QUALITY Q WHERE T.BILL_NO = ? AND T.QUALITY_ID = Q.QUALITY_ID";
    private static String getAllTaakasWithDateQuery = "SELECT * FROM TAAKA WHERE YEAR(PRODUCTION_DATE) = ? AND MONTH(PRODUCTION_DATE) = ?";
    private static String getAllAvailableTaakasQuery = "SELECT * FROM TAAKA WHERE BILL_NO IS NULL";
    private static String getAllAvailableTaakasWithDateQuery = "SELECT * FROM TAAKA WHERE YEAR(PRODUCTION_DATE) = ? AND MONTH(PRODUCTION_DATE) = ? AND BILL_NO IS NULL";
    private static String addTaakaQuery = "INSERT INTO TAAKA(PRODUCTION_DATE, TAAKA_NUMBER, TAAKA_LENGTH, QUALITY_ID) VALUE (?, ?, ?, ?)";
    private static String lastTaakaNumberFinderQuery = "SELECT MAX(TAAKA_NUMBER) FROM TAAKA WHERE YEAR(PRODUCTION_DATE) = ? AND MONTH(PRODUCTION_DATE) = ?";
    private static String updateTaakaBillQuery = "UPDATE TAAKA SET BILL_NO = ? WHERE TAAKA_NUMBER = ? AND YEAR(PRODUCTION_DATE) = ? AND MONTH(PRODUCTION_DATE) = ?";

    private static PreparedStatement getAllTaakasStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllTaakasQuery);
    private static PreparedStatement getAllTaakasWithBillStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllTaakasWithBillQuery);
    private static PreparedStatement getAllTaakasWithDateStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllTaakasWithDateQuery);
    private static PreparedStatement getAllAvailableTaakasStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllAvailableTaakasQuery);
    private static PreparedStatement getAllAvailableTaakasWithDateStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllAvailableTaakasWithDateQuery);
    private static PreparedStatement addTaakaStatement = StaticDatabaseConnectionHolder.getPreparedStatement(addTaakaQuery);
    private static PreparedStatement lastTaakaNumberFinderStatement = StaticDatabaseConnectionHolder.getPreparedStatement(lastTaakaNumberFinderQuery);
    private static PreparedStatement updateTaakaBillStatement = StaticDatabaseConnectionHolder.getPreparedStatement(updateTaakaBillQuery);

    private static Logger logger = Logger.getLogger(Taaka.class.getName());
    @RequestMapping(
            path = "/service/taaka/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode getAllTaakas() throws SQLException {
        JsonNode returnNode = null;
        returnNode = ResultSetToJson.getJSON(getAllTaakasStatement.executeQuery());
        return returnNode;
    }
    @RequestMapping(
            path = "/service/taaka/bill",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public static JsonNode getAllTaakasWithBill(
            @RequestParam(name = "bill_no") int billNo
    ) throws SQLException
    {
            JsonNode returnNode=null;
            getAllTaakasWithBillStatement.setInt(1, billNo);
            returnNode = ResultSetToJson.getJSON(getAllTaakasWithBillStatement.executeQuery());
            return returnNode;
    }
    @RequestMapping(
            path = "/service/taaka/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode getAllTaakasWithDate(
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month
    ) throws SQLException {
        JsonNode returnNode = null;
        getAllTaakasWithDateStatement.setInt(1, year);
        getAllTaakasWithDateStatement.setInt(2, month);
        returnNode = ResultSetToJson.getJSON(getAllTaakasWithDateStatement.executeQuery());
        return returnNode;
    }

    @RequestMapping(
            path = "/service/taaka/all/available",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode getAllAvailableTaakas() throws SQLException {
        JsonNode returnNode = null;
        returnNode = ResultSetToJson.getJSON(getAllAvailableTaakasStatement.executeQuery());
        return returnNode;
    }
    @RequestMapping(
            path = "/service/taaka/all/available",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode getAllAvailableTaakasWithDate(
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month
    ) throws SQLException
    {
        JsonNode returnNode = null;
        getAllAvailableTaakasWithDateStatement.setInt(1, year);
        getAllAvailableTaakasWithDateStatement.setInt(2, month);
        returnNode = ResultSetToJson.getJSON(getAllAvailableTaakasWithDateStatement.executeQuery());
        return returnNode;
    }

    @RequestMapping(
            path = "/service/taaka",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode addTaaka(
            @RequestParam(name = "production_date") Date productionDate,
            @RequestParam(name = "length") double length,
            @RequestParam(name = "quality_id") int qualityId
    ) throws SQLException {
        JsonNode returnNode = null;
        lastTaakaNumberFinderStatement.setInt(1, productionDate.toLocalDate().getYear());
        lastTaakaNumberFinderStatement.setInt(2, productionDate.toLocalDate().getMonthValue());
        ResultSet rs = lastTaakaNumberFinderStatement.executeQuery();
        rs.next();
        int newTaakaNumber = rs.getInt(1) + 1;
        addTaakaStatement.setDate(1, productionDate);
        addTaakaStatement.setInt(2, newTaakaNumber);
        addTaakaStatement.setDouble(3, length);
        addTaakaStatement.setInt(4, qualityId);
        if(addTaakaStatement.executeUpdate() != 1)
            returnNode = ResultSetToJson.getResponse(-1, "Failed to add taaka!");
        else
            returnNode = ResultSetToJson.getOk();
        return returnNode;
    }

    @RequestMapping(
            path = "/service/taaka/new_taaka_number",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE}
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode getNewTaakaNumber(
            @RequestParam(name = "production_date") Date productionDate
    ) throws SQLException {
        lastTaakaNumberFinderStatement.setInt(1,productionDate.toLocalDate().getYear());
        lastTaakaNumberFinderStatement.setInt(2,productionDate.toLocalDate().getMonthValue());
        ResultSet rs = lastTaakaNumberFinderStatement.executeQuery();
        rs.next();
        int newTaakaNumber = rs.getInt(1) + 1;
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode().put("newTaakaNumber",newTaakaNumber);
        return objectNode;
    }
    @RequestMapping(
            path = "/service/taaka/update_bill",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public static JsonNode updateBill(
            @RequestParam(name = "taaka_number") int taakaNumber,
            @RequestParam(name = "bill_number") int billNumber,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month
    ) throws SQLException {
        JsonNode returnNode=null;
        updateTaakaBillStatement.setInt(1,billNumber);
        updateTaakaBillStatement.setInt(2,taakaNumber);
        updateTaakaBillStatement.setInt(3,year);
        updateTaakaBillStatement.setInt(4,month);
        if(updateTaakaBillStatement.executeUpdate()!=1)
            returnNode = ResultSetToJson.getResponse(-1,"Failed to udpate Taaka!");
        else
            returnNode = ResultSetToJson.getOk();
        return returnNode;
    }
}
