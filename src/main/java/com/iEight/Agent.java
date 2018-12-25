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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class Agent {
    private static final Logger logger=Logger.getLogger(Agent.class.getName());
    @RequestMapping(
            path = "/service/agent/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode getAgents()
    {
        logger.log(Level.INFO,"Request captured!");
        return StaticDatabaseConnectionHolder.getResult("SELECT * FROM AGENT");
    }

    @RequestMapping(
            path = "service/agent",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode getSpecificAgent(
            @RequestParam(name = "agent_phone", required = true, defaultValue = "") String agentPhone
    )
    {
        return StaticDatabaseConnectionHolder.getResult("SELECT * FROM AGENT WHERE AGENT_PHONE = "+agentPhone).get(0);
    }



    @RequestMapping(
            path = "/service/agent",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    public JsonNode updateAgent(
            @RequestParam(name = "agent_phone",required = true, defaultValue = "") String agnetPhone,
            @RequestParam(name = "agent_name",required = true, defaultValue = "") String agentNameNew
    ) throws SQLException
    {
        StaticDatabaseConnectionHolder.getStatement().executeUpdate("UPDATE AGENT SET AGENT_NAME = "+agentNameNew+" WHERE AGENT_PHONE = "+agnetPhone);
        return ResultSetToJson.getOk();
    }

    @RequestMapping(
            path = "/service/agent",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode addAgent(
            @RequestParam(name = "agent_phone",required = true,defaultValue = "") String agentPhone,
            @RequestParam(name = "agent_name",required = true,defaultValue = "") String agentName
    ) throws SQLException {
        StaticDatabaseConnectionHolder.getStatement().
                executeUpdate("INSERT INTO " +
                        "AGENT(AGENT_PHONE,AGENT_NAME) " +
                        "VALUE ( "+agentPhone+" , "+agentName+" )");
        return ResultSetToJson.getOk();
    }



    @RequestMapping(
            path = "/service/agent",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode deleteAgent(
            @RequestParam(name = "agent_phone",required = true,defaultValue = "") String agnetPhone
    )
    {
        return ResultSetToJson.getResponse(-1,"Still not implemented!");
    }
}
