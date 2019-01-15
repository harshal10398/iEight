package com.iEight;

import com.fasterxml.jackson.databind.JsonNode;
import com.iEight.util.ResultSetToJson;
import com.iEight.util.StaticDatabaseConnectionHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@RestController
public class PartyAgent {
    private static String getAllPartyAgentCombosQuery = "SELECT " +
            "PA.PARTY_AGENT_ID, " +
            "P.GSTIN, " +
            "P.PARTY_NAME, " +
            "P.PARTY_PHONE, " +
            "P.PARTY_ADDRESS, " +
            "A.AGENT_PHONE, " +
            "A.AGENT_NAME " +
            "FROM " +
            "PARTY_AGENT PA, " +
            "AGENT A, " +
            "PARTY P " +
            "WHERE " +
            "PA.PARTY_GSTIN = P.GSTIN AND PA.AGENT_PHONE = A.AGENT_PHONE";
    private static String getSpecificPartyAgentComboQuery = getAllPartyAgentCombosQuery + " AND PA.PARTY_AGENT_ID = ?";
    private static String addPartyAgentComboQuery = "INSERT INTO PARTY_AGENT(PARTY_GSTIN, AGENT_PHONE) VALUE(?,?)";

    private static PreparedStatement getAllPartyAgentCombosStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getAllPartyAgentCombosQuery);
    private static PreparedStatement getSpecificPartyAgentComboStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getSpecificPartyAgentComboQuery);
    private static PreparedStatement addPartyAgentComboStatement = StaticDatabaseConnectionHolder.getPreparedStatement(addPartyAgentComboQuery);

    @RequestMapping(
            path = "/service/partyagent/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE;
    )
    public JsonNode getAllPartyAgentCombos() throws SQLException {
        JsonNode returnNode = null;
        returnNode = ResultSetToJson.getJSON(getAllPartyAgentCombosStatement.executeQuery());
        return returnNode;
    }

}
