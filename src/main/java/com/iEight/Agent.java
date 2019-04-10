package com.iEight;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iEight.util.ResultSetToJson;
import com.iEight.util.StaticDatabaseConnectionHolder;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class Agent {
    private static final String getAllAgentsQuery="SELECT * FROM AGENT";
    private static final String getSpecificAgentQuery="SELECT * FROM AGENT WHERE AGENT_PHONE = ?";
    private static final String updateAgentQuery="UPDATE AGENT SET AGENT_NAME = ? WHERE AGENT_PHONE = ?";
    private static final String addAgentQuery="INSERT INTO AGENT(AGENT_PHONE, AGENT_NAME) VALUE (?, ?)";
    private static final String deleteAgentQuery="";

    private static final PreparedStatement getAllAgentsStatement =
            StaticDatabaseConnectionHolder.getPreparedStatement(getAllAgentsQuery);
    private static final PreparedStatement getSpecificAgentStatement =
            StaticDatabaseConnectionHolder.getPreparedStatement(getSpecificAgentQuery);
    private static final PreparedStatement updateAgentStatement =
            StaticDatabaseConnectionHolder.getPreparedStatement(updateAgentQuery);
    private static final PreparedStatement addAgentStatement =
            StaticDatabaseConnectionHolder.getPreparedStatement(addAgentQuery);
//     private static final PreparedStatement deleteAgentStatement =
//             StaticDatabaseConnectionHolder.getPreparedStatement(deleteAgentQuery);

    private static final Logger logger=Logger.getLogger(Agent.class.getName());
    @RequestMapping(
            path = "/service/agent/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode getAgents() throws SQLException {

        JsonNode returnNode = ResultSetToJson.getJSON(getAllAgentsStatement.executeQuery());

        return returnNode;
    }

    @RequestMapping(
            path = "service/agent",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode getSpecificAgent(
            @RequestParam(name = "agent_phone") String agentPhone
    ) throws SQLException {
//        return StaticDatabaseConnectionHolder.getResult("SELECT * FROM AGENT WHERE AGENT_PHONE = "+agentPhone).get(0);
        JsonNode returnNode;

        getSpecificAgentStatement.setString(1, agentPhone);
        returnNode = ResultSetToJson.getJSON(getSpecificAgentStatement.executeQuery());

        return returnNode;
    }


    @RequestMapping(
            path = "/service/agent",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    public JsonNode updateAgent(
            @RequestParam(name = "agent_phone") String agentPhone,
            @RequestParam(name = "agent_name") String agentNameNew
    ) throws SQLException
    {
        JsonNode returnNode = null;

        updateAgentStatement.setString(1, agentNameNew);
        updateAgentStatement.setString(2, agentPhone);
        int result = updateAgentStatement.executeUpdate();
        if(result!=1)
            returnNode = ResultSetToJson.getResponse(-1,"Error!");
        else
            returnNode = ResultSetToJson.getOk();

        return returnNode;

    }

    @RequestMapping(
            path = "/service/agent",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode addAgent(
            @RequestParam(name = "agent_phone") String agentPhone,
            @RequestParam(name = "agent_name") String agentName
    ) throws SQLException
    {
        JsonNode returnNode = null;
        
        addAgentStatement.setString(1,agentPhone);
        addAgentStatement.setString(2, agentName);
        
        int result = addAgentStatement.executeUpdate();
        
        if (result != 1)
            returnNode = ResultSetToJson.getResponse(-1,"Error!");
        else
            returnNode = ResultSetToJson.getOk();

        return returnNode;
    }



    @RequestMapping(
            path = "/service/agent",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode deleteAgent(
            @RequestParam(name = "agent_phone") String agnetPhone
    )
    {
        return ResultSetToJson.getResponse(-1,"Still not implemented!");
    }
}
