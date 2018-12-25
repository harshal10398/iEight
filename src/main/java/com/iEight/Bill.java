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

import java.util.logging.Logger;

import static org.springframework.http.MediaType.*;

@RestController
public class Bill {
    private static final Logger logger=Logger.getLogger(Bill.class.getName());
    @RequestMapping(
            path = "/service/bill/all",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE
    )
    public JsonNode getAllBills(){
        return StaticDatabaseConnectionHolder.getResult("SELECT * FROM BILLS");
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

}
