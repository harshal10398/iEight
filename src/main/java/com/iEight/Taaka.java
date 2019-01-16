package com.iEight;

import com.fasterxml.jackson.databind.JsonNode;
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

@RestController
public class Taaka {
    private static String getAllTaakasQuery = "SELECT * FROM TAAKA";
    private static String getAllTaakasWithDateQuery = "SELECT * FROM TAAKA WHERE YEAR(PRODUCTION_DATE) = ? AND MONTH(PRODUCTION_DATE) = ?";
    private static String getAllAvailableTaakasQuery = "SELECT * FROM TAAKA WHERE BILL_NO IS NULL";
    private static String getAllAvailableTaakasWithDateQuery = "SELECT * FROM TAAKA WHERE YEAR(PRODUCTION_DATE) = ? AND MONTH(PRODUCTION_DATE) = ? AND BILL_NO IS NULL";
    private static String addTaakaQuery = "INSERT INTO TAAKA(PRODUCTION_DATE, TAAKA_NUMBER, TAAKA_LENGTH, QUALITY_ID) VALUE (?, ?, ?, ?)";
    private static String lastTaakaNumberFinderQuery = "SELECT MAX(TAAKA_NUMBER) FROM TAAKA WHERE YEAR(PRODUCTION_DATE) = ? AND MONTH(PRODUCTION_DATE) = ?";

    private static PreparedStatement getAllTaakasStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllTaakasQuery);
    private static PreparedStatement getAllTaakasWithDateStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllTaakasWithDateQuery);
    private static PreparedStatement getAllAvailableTaakasStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllAvailableTaakasQuery);
    private static PreparedStatement getAllAvailableTaakasWithDateStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllAvailableTaakasWithDateQuery);
    private static PreparedStatement addTaakaStatement = StaticDatabaseConnectionHolder.getPreparedStatement(addTaakaQuery);
    private static PreparedStatement lastTaakaNumberFinderStatement = StaticDatabaseConnectionHolder.getPreparedStatement(lastTaakaNumberFinderQuery);
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
            @RequestParam(name = "qualityId") int qualityId
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
}
