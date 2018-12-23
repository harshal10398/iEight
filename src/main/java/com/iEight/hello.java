package com.iEight;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.iEight.util.ResultSetToJson;
//import org.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.iEight.util.StaticDatabaseConnectionHolder.*;

@RestController
public class hello {

    static String name = "World";

    @RequestMapping(method = RequestMethod.GET, path = "/hello", produces = MediaType.TEXT_HTML_VALUE)
    public String greeting() {
        return "<html><body><h1>Hello, " + name + "</h1><form name='update' method=post><input type=text name=name><input type=submit></form></body></html>";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/hello", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public String setName(@RequestParam(name = "name") String name) {
        hello.name = name;
        return "ok!";
    }

    public class Agent{
        private final String agentName,agentPhone;

        public String getAgentName() {
            return agentName;
        }

        public String getAgentPhone() {
            return agentPhone;
        }

        public Agent(String agentName, String agentPhone) {
            this.agentName = agentName;
            this.agentPhone = agentPhone;
        }

    }
    @RequestMapping(method = RequestMethod.GET, path = "/db", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayNode dbTest(){
        ResultSet rs = null;
        try {
            rs = getStatement().executeQuery("SELECT * FROM AGENT");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ResultSetToJson.getJSON(rs);
    }
}
