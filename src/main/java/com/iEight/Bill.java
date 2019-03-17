package com.iEight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iEight.util.ResultSetToJson;
import com.iEight.util.StaticDatabaseConnectionHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.http.MediaType.*;


class TaakaClass{
    public Integer taaka_number,year,month;
};


@RestController
public class Bill {
    private static final String getAllBillsQuery = "SELECT * FROM BILL_BOOK";
    private static final String getSpecificBillQuery = "SELECT * FROM BILL_BOOK WHERE BILL_NO = ?";
    private static final String addBillQuery = "INSERT INTO BILL_BOOK(" + "PARTY_AGENT_ID, " + "BILL_DATE,"
            + "DELIVERY_ADDRESS," + "PAYMENT_DUE," + "RATE_PER_METER," + "GST," + "TOTAL_AMOUNT," + "BILL_STATUS"
            + ") VALUE(?,?,?,?,?,?,?,?)";
    private static final String newBillNoQuery = "SELECT MAX(BILL_NO) FROM BILL_BOOK";
    // private static final updateBillQuery = "";

    private static final PreparedStatement getAllBillsStatement = StaticDatabaseConnectionHolder
            .getPreparedStatement(getAllBillsQuery);
    private static final PreparedStatement getSpecificBillStatement = StaticDatabaseConnectionHolder
            .getPreparedStatement(getSpecificBillQuery);
    private static final PreparedStatement addBillStatement = StaticDatabaseConnectionHolder
            .getPreparedStatement(addBillQuery);
    private static final PreparedStatement newBillNoStatement = StaticDatabaseConnectionHolder
            .getPreparedStatement(newBillNoQuery);

    private static final Logger logger = Logger.getLogger(Bill.class.getName());

    @RequestMapping(path = "/service/bill/all", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public JsonNode getAllBills() throws SQLException {
        JsonNode returnNode = null;
        returnNode = ResultSetToJson.getJSON(getAllBillsStatement.executeQuery());
        return returnNode;
    }

    @RequestMapping(path = "/service/bill", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public JsonNode getSpecificBill(@RequestParam(name = "bill_no") int billNo) throws SQLException {
        JsonNode returnNode = null;
        getSpecificBillStatement.setInt(1, billNo);
        returnNode = ResultSetToJson.getJSON(getSpecificBillStatement.executeQuery());
        return returnNode;
        // return StaticDatabaseConnectionHolder.getResult("SELECT * FROM BILLS WHERE
        // BILL_NO = "+billNo);
    }

    @RequestMapping(
            path = "/service/bill",
            method = RequestMethod.PUT,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode addBill(
            @RequestParam(name = "party_agent_id") int partyAgentId,
            @RequestParam(name = "bill_date") java.sql.Date billDate,
            @RequestParam(name = "delivery_address") String address,
            @RequestParam(name = "payment_due", required = false, defaultValue = "20") int due,
            @RequestParam(name = "rate_per_meter", required = true, defaultValue = "") double ratePerMeter,
            @RequestParam(name = "gst", required = false, defaultValue = "5") double gst,
            @RequestParam(name = "total_amount") double totalAmount,
            @RequestParam(name = "bill_status", required = false, defaultValue = "0") int billStatus,
            @RequestParam(name = "taakas") String taakasArray
    ) throws SQLException, IOException {
        
        JsonNode returnNode = null;
        addBillStatement.setInt(1, partyAgentId);
        addBillStatement.setDate(2, billDate);
        addBillStatement.setString(3, address);
        addBillStatement.setInt(4, due);
        addBillStatement.setDouble(5, ratePerMeter);
        addBillStatement.setDouble(6, 5.0);
        addBillStatement.setDouble(7, totalAmount);
        addBillStatement.setInt(8, billStatus);
        int newBillNumber = getNewBillNo().get("new_bill_number").asInt();
        logger.info("newBillNumber: "+newBillNumber);
        if (addBillStatement.executeUpdate() != 1)
            returnNode = ResultSetToJson.getResponse(-1, "Failed to add bill!");
        else
            returnNode = ResultSetToJson.getOk();
        TaakaClass[] taakaList = new ObjectMapper().readValue(taakasArray, TaakaClass[].class);
        for(TaakaClass taaka : taakaList){
            logger.info(taaka.taaka_number+" "+taaka.year+" "+taaka.month+" bill: "+newBillNumber);
            Taaka.updateBill(taaka.taaka_number, newBillNumber, taaka.year, taaka.month);
        }
        return returnNode;
    }

    @RequestMapping(path = "/service/bill/new_bill_number", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public JsonNode getNewBillNo() throws SQLException {
        ResultSet rs = newBillNoStatement.executeQuery();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = null;
        if (rs.next()) {
            int newBillNumber = rs.getInt(1) + 1;
            objectNode = objectMapper.createObjectNode().put("new_bill_number", newBillNumber);
        }
        return objectNode;
    }
}
