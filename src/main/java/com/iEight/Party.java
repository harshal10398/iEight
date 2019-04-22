package com.iEight;

import com.fasterxml.jackson.databind.JsonNode;
import com.iEight.util.ResultSetToJson;
import com.iEight.util.StaticDatabaseConnectionHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.iEight.util.StaticDatabaseConnectionHolder.getPreparedStatement;

@RestController
public class Party {
    private static final String getAllPartyQuery = "SELECT * FROM PARTY";
    private static final String getSpecificPartyQuery = "SELECT * FROM PARTY WHERE GSTIN = ?";

    private static final String updatePartyNameQuery = "UPDATE PARTY SET PARTY_NAME = ? WHERE GSTIN = ?";
    private static final String updatePartyPhoneQuery = "UPDATE PARTY SET PARTY_PHONE = ? WHERE GSTIN = ?";
    private static final String updatePartyAddressQuery = "UPDATE PARTY SET PARTY_ADDRESS = ? WHERE GSTIN = ?";

    private static final String addPartyQuery = "INSERT INTO PARTY(GSTIN, PARTY_NAME, PARTY_PHONE, PARTY_ADDRESS) VALUE(?,?,?,?)";

    private static final PreparedStatement getAllPartyStatement = getPreparedStatement(getAllPartyQuery);
    private static final PreparedStatement getSpecificPartyStatement = getPreparedStatement(getSpecificPartyQuery);

    private static final PreparedStatement updatePartyNameStatement = getPreparedStatement(updatePartyNameQuery);
    private static final PreparedStatement updatePartyPhoneStatement = getPreparedStatement(updatePartyPhoneQuery);
    private static final PreparedStatement updatePartyAddressStatement = getPreparedStatement(updatePartyAddressQuery);

    private static final PreparedStatement addPartyStatement = getPreparedStatement(addPartyQuery);

    private static final Logger logger = Logger.getLogger(Party.class.getName());
    @RequestMapping(
            path = "/service/party/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode getAllParty() throws SQLException {
        JsonNode returnNode = null;
        returnNode = ResultSetToJson.getJSON(getAllPartyStatement.executeQuery());
        return returnNode;
    }

    @RequestMapping(
            path = "/service/party",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public static JsonNode getSpecificParty(
            @RequestParam(name = "gstin") String gstin
    ) throws SQLException {
        JsonNode returnNode = null;
        getSpecificPartyStatement.setString(1, gstin);
        return ResultSetToJson.getJSON(getSpecificPartyStatement.executeQuery());
    }

    @RequestMapping(
            path = "/service/party/",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode updateParty(
            @RequestParam(name = "gstin") String gstin,
            @RequestParam(name = "party_name", required = false) String partyName,
            @RequestParam(name = "party_phone", required = false) String partyPhone,
            @RequestParam(name = "party_address", required = false) String partyAddress
    ) throws SQLException {
        JsonNode returnNode = null;
        updatePartyNameStatement.setString(1,gstin);
        updatePartyPhoneStatement.setString(1,gstin);
        updatePartyAddressStatement.setString(1,gstin);

        String returnMsg = "Failed to update";
//         if (!partyName.isBlank()) {
            updatePartyNameStatement.setString(2,partyName);
            if (updatePartyNameStatement.executeUpdate() != 1) {
                returnMsg += " name";
            }
//         }
//         if (!partyPhone.isBlank()) {
            updatePartyPhoneStatement.setString(2, partyPhone);
            if (updatePartyPhoneStatement.executeUpdate() != 1) {
                returnMsg += " phone";
            }
//         }
//         if (!partyAddress.isBlank()) {
            updatePartyAddressStatement.setString(2,partyAddress);
            if (updatePartyAddressStatement.executeUpdate() != 1) {
                returnMsg += " address";
            }
//         }
        returnNode = ResultSetToJson.getOk();
        return returnNode;
    }

    @RequestMapping(
            path = "service/party",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode addParty(
            @RequestParam(name = "gstin") String gstin,
            @RequestParam(name = "party_name") String partyName,
            @RequestParam(name = "party_phone") String partyPhone,
            @RequestParam(name = "party_address") String partyAddress
    ) throws SQLException {
        JsonNode returnNode = null;
        addPartyStatement.setString(1, gstin);
        addPartyStatement.setString(2,partyName);
        addPartyStatement.setString(3,partyPhone);
        addPartyStatement.setString(4,partyAddress);
        if(addPartyStatement.executeUpdate() != 1)
            returnNode = ResultSetToJson.getResponse(-1, "Failed to add party!");
        returnNode = ResultSetToJson.getOk();
        return  returnNode;
    }
}
