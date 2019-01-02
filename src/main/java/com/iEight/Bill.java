package com.iEight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iEight.util.ResultSetToJson;
import com.iEight.util.StaticDatabaseConnectionHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;

import static org.springframework.http.MediaType.*;

@RestController
public class Bill {
    private static final String getAllBillsQuery = "";
    private static final String getSpecificBillQuery = "";
    private static final String addBillQuery = "";
//    private static final updateBillQuery = "";

    private static final PreparedStatement getAllBillsStatement =
            StaticDatabaseConnectionHolder.getPreparedStatement(getAllBillsQuery);
    private static final PreparedStatement getSpecificBillStatement =
            StaticDatabaseConnectionHolder.getPreparedStatement(getSpecificBillQuery);
    private static final PreparedStatement addBillStatement =
            StaticDatabaseConnectionHolder.getPreparedStatement(addBillQuery);

    private static final Logger logger=Logger.getLogger(Bill.class.getName());
    @RequestMapping(
            path = "/service/bill/all",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE
    )
    public JsonNode getAllBills() throws SQLException {
        JsonNode returnNode = null;
        returnNode = ResultSetToJson.getJSON(getAllBillsStatement.executeQuery());
        return returnNode;
    }

    @RequestMapping(
            path = "/service/bill",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode getSpecificBill(
            @RequestParam(name = "bill_no",required = true,defaultValue = "") String billNo)
    {
        return StaticDatabaseConnectionHolder.getResult("SELECT * FROM BILLS WHERE BILL_NO = "+billNo);
    }

    @RequestMapping(
            path = "/service/bill",
            method = RequestMethod.PUT,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode addBill(
            @RequestParam(name = "bill_no",required = true,defaultValue = "") String billNo,
            @RequestParam(name = "party_agent_id", required = true, defaultValue = "") String partyAgentId,
            @RequestParam(name = "bill_date",required = true, defaultValue = "") Date billDate,
            @RequestParam(name = "delivery_address", required = true, defaultValue = "") String address,
            @RequestParam(name = "payment_due", required = true, defaultValue = "") int due,
            @RequestParam(name = "rate_per_meter", required = true, defaultValue = "") double ratePerMeter,
            @RequestParam(name = "gst",required = true, defaultValue = "") double gst,
            @RequestParam(name = "total_amount", required = true, defaultValue = "") double totalAmount,
            @RequestParam(name = "bill_status", required = true, defaultValue = "") int billStatus
    )
    {
        StaticDatabaseConnectionHolder.getStatement().executeUpdate("INSERT INTO BILL_BOOK(" +
                    "PARTY_AGENT_ID" +
                    "BILL_DATE" +
                    "DELIVERY_ADDRESS" +
                    "PAYMENT_DUE" +
                    "RATE_PER_METER" +
                    "GST" +
                    "TOTAL_AMOUNT" +
                    "BILL_STATUS" +
                    ")" +
                    "VALUE (" +
                    "'"+ partyAgentId + "'," +
                    "'"+ billDate +"'," +
                    "'" + address + "'," +
                    "'" + due + "',"

                );
    }
}
