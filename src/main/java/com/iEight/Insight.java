package com.iEight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class Insight {
    private static final String getAgentCountQuery="SELECT COUNT(*) AS AGENT_COUNT FROM AGENT";
    private static final String getPartyCountQuery="SELECT COUNT(*) AS PARTY_COUNT FROM PARTY";
    private static final String getBillCountQuery="SELECT COUNT(*) AS BILL_COUNT FROM BILL_BOOK";
    private static final String getCollectionCountQuery="SELECT COUNT(*) AS COLLECTION_COUNT FROM COLLECTION_BOOK";
    private static final String getTaakaMonthlyCountAndTotalLengthQuery="SELECT COUNT(*) AS TAAKA_COUNT, SUM(TAAKA_LENGTH) AS TOTAL_LENGTH, YEAR(PRODUCTION_DATE) AS YEAR, MONTH(PRODUCTION_DATE) AS MONTH FROM TAAKA GROUP BY YEAR(PRODUCTION_DATE), MONTH(PRODUCTION_DATE) ORDER BY YEAR(PRODUCTION_DATE), MONTH(PRODUCTION_DATE) DESC";
    private static final String getCollectionMonthlyCountQuery="SELECT COUNT(*) AS COLLECTION_COUNT, YEAR(ENTRY_DATE) AS YEAR, MONTH(ENTRY_DATE) AS MONTH FROM COLLECTION_BOOK GROUP BY YEAR(ENTRY_DATE), MONTH(ENTRY_DATE) ORDER BY YEAR(ENTRY_DATE), MONTH(ENTRY_DATE) DESC";
    private static final String getMonthlyBillCountQuery = "SELECT COUNT(*) AS BILL_COUNT, YEAR(BILL_DATE) AS YEAR, MONTH(BILL_DATE) AS MONTH FROM BILL_BOOK GROUP BY YEAR(BILL_DATE), MONTH(BILL_DATE) ORDER BY YEAR(BILL_DATE), MONTH(BILL_DATE) DESC";
    private static final String getDueBillsQuery = "SELECT BILL_NO, BILL_DATE, DATEDIFF( DATE_ADD(BILL_DATE, INTERVAL (PAYMENT_DUE) DAY ), ? ) FROM BILL_BOOK WHERE BILL_STATUS = 0";
    
    private static final PreparedStatement getAgentCountStatement= StaticDatabaseConnectionHolder.getPreparedStatement(getAgentCountQuery);
    private static final PreparedStatement getPartyCountStatement= StaticDatabaseConnectionHolder.getPreparedStatement(getPartyCountQuery);
    private static final PreparedStatement getBillCountStatement=StaticDatabaseConnectionHolder.getPreparedStatement(getBillCountQuery);
    private static final PreparedStatement getCollectionCountStatement=StaticDatabaseConnectionHolder.getPreparedStatement(getCollectionCountQuery);
    private static final PreparedStatement getTaakaMonthlyCountAndTotalLengthStatement=StaticDatabaseConnectionHolder.getPreparedStatement(getTaakaMonthlyCountAndTotalLengthQuery);
    private static final PreparedStatement getCollectionMonthlyCountStatement=StaticDatabaseConnectionHolder.getPreparedStatement(getCollectionMonthlyCountQuery);
    private static final PreparedStatement getMonthlyBillCountStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getMonthlyBillCountQuery);
    private static final PreparedStatement getDueBillStatement = StaticDatabaseConnectionHolder.getPreparedStatement(getDueBillsQuery);

    private static final Logger logger = Logger.getLogger(Insight.class.getName());
    @RequestMapping(
            path = "/service/insights",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public JsonNode getInsights(@RequestParam(name="date") Date date) throws SQLException {
        int agentCount=0,partyCount=0,billCount=0,collectionTotalCount=0,taakaCount=0,collectionMonthlyCount=0;
        double taakaLength=0.0;
        ResultSet rs=null;
        rs = getAgentCountStatement.executeQuery();
        if(rs.next())
            agentCount=rs.getInt(1);

        rs = getPartyCountStatement.executeQuery();
        if(rs.next())
            partyCount=rs.getInt(1);

        rs = getBillCountStatement.executeQuery();
        if(rs.next())
            billCount = rs.getInt(1);

        rs = getCollectionCountStatement.executeQuery();
        if(rs.next())
            collectionTotalCount = rs.getInt(1);

        rs = getTaakaMonthlyCountAndTotalLengthStatement.executeQuery();
        if(rs.next()){
            taakaCount = rs.getInt(1);
            taakaLength = rs.getDouble(2);
        }

        rs = getCollectionMonthlyCountStatement.executeQuery();
        if(rs.next())
            collectionMonthlyCount = rs.getInt(1);
        
        rs = getMonthlyBillCountStatement.executeQuery();
        rs.last();
        
        int monthlyBillsCount = rs.getRow();

        getDueBillStatement.setDate(1, date);
        rs = getDueBillStatement.executeQuery();
        rs.last();
        int dueBillsCount = rs.getRow();
        /*logger.log(Level.SEVERE,"AgentCount: "+agentCount);
        logger.log(Level.SEVERE,"PartyCount: "+partyCount);
        logger.log(Level.SEVERE,"BillCount: "+billCount);
        logger.log(Level.SEVERE,"collectionTotalCount"+collectionTotalCount);
        logger.log(Level.SEVERE,"taakaCount"+taakaCount);
        logger.log(Level.SEVERE,"taakaLength"+taakaLength);
        logger.log(Level.SEVERE,"collectionMonthlyCount"+collectionMonthlyCount);
        */
        ObjectMapper objectMapper=new ObjectMapper();
        ObjectNode objectNode=objectMapper.createObjectNode().
                put("agentCount",agentCount).
                put("partyCount",partyCount).
                put("billCount", billCount).
                put("collectionTotalCount", collectionTotalCount).
                put("taakaCount", taakaCount).
                put("taakaLength",taakaLength).
                put("collectionMonthlyCount",collectionMonthlyCount).
                put("monthlyBillsCount",monthlyBillsCount).
                put("dueBillsCount",dueBillsCount);
        return objectNode;
    }
}
