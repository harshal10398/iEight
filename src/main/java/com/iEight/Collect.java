package com.iEight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import java.sql.SQLException;
import java.util.logging.Logger;

import static org.springframework.http.MediaType.*;

@RestController
public class Collect{
    private static final String getAllCollectionsQuery = "SELECT * FROM COLLECTION_BOOK";
    private static final String getSpecificCollectionQuery = "SELECT * FROM COLLECTION_BOOK WHERE BILL_NO = ?";
    private static final String addCollectionQuery = "INSERT INTO COLLECTION_BOOK(" +
    "ENTRY_DATE, " +
    "PAYMENT_DATE, " +
    "BILL_NO, " +
    "CHEQUE_NO, " +
    "BANK_NAME, " +
    "PARTY_ACCOUNT_NO, " +
    "AMOUNT" +
    ") VALUE(?,?,?,?,?,?,?)";

    private static final PreparedStatement getAllCollectionsStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllCollectionsQuery);    
    private static final PreparedStatement getSpecificCollectionStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getSpecificCollectionQuery);
    private static final PreparedStatement addCollectionStatement = StaticDatabaseConnectionHolder.getPreparedStatement(addCollectionQuery);
    
    private static final Logger logger = Logger.getLogger(Collect.class.getName());
    
    @RequestMapping(
            path="/service/collect/all",
    		method=RequestMethod.GET,
    		produces= MediaType.APPLICATION_JSON_VALUE
	)
    public JsonNode getAllCollections() throws SQLException {
    	JsonNode returnNode = null;
        returnNode = ResultSetToJson.getJSON(getAllCollectionsStatement.executeQuery());
        return returnNode;
    }

    @RequestMapping(
            path = "/service/collect",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode getSpecificCollection(
            @RequestParam(name = "bill_no") String billNo
    ) throws SQLException {
        JsonNode returnNode = null;
        getSpecificCollectionStatement.setString(1,billNo);
        returnNode = ResultSetToJson.getJSON(getSpecificCollectionStatement.executeQuery());
        return returnNode;
    }

    @RequestMapping(
            path = "/service/collect",
            method = RequestMethod.PUT,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode addCollection(
            @RequestParam(name = "entry_date") Date entryDate,
            @RequestParam(name = "payment_date") Date paymentDate,
            @RequestParam(name = "bill_no") int billNo,
            @RequestParam(name = "cheque_no") int chequeNo,
            @RequestParam(name = "bank_name") String bankeName,
            @RequestParam(name = "party_account_no") String partyAccountNo,
            @RequestParam(name = "amount") double amount
    ) throws SQLException {
        JsonNode returnNode = null;
        addCollectionStatement.setDate(1,entryDate);
        addCollectionStatement.setDate(2,paymentDate);
        addCollectionStatement.setInt(3,billNo);
        addCollectionStatement.setInt(4,chequeNo);
        addCollectionStatement.setString(5,bankeName);
        addCollectionStatement.setString(6,partyAccountNo);
        addCollectionStatement.setDouble(7,amount);
        if(addCollectionStatement.executeUpdate() != 1)
            returnNode = ResultSetToJson.getResponse(-1,"Failed to add bill.");
        else
            returnNode = ResultSetToJson.getOk();
        return returnNode;
    }
}
