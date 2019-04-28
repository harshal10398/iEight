package com.iEight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iEight.util.ResultSetToJson;
import com.iEight.util.StaticDatabaseConnectionHolder;

import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.tomcat.jni.File;
import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import javax.xml.transform.Result;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.http.MediaType.*;

class TaakaClass {
    public Integer taaka_number, year, month;
};

@RestController
public class Bill {
    private static final String getAllBillsQuery = "SELECT * FROM BILL_BOOK ORDER BY BILL_STATUS";
    private static final String getSpecificBillQuery = "SELECT * FROM BILL_BOOK WHERE BILL_NO = ?";
    private static final String addBillQuery = "INSERT INTO BILL_BOOK(" + "PARTY_AGENT_ID, " + "BILL_DATE,"
            + "DELIVERY_ADDRESS," + "PAYMENT_DUE," + "RATE_PER_METER," + "GST," + "TOTAL_AMOUNT," + "BILL_STATUS"
            + ") VALUE(?,?,?,?,?,?,?,?)";
    private static final String newBillNoQuery = "SELECT MAX(BILL_NO) FROM BILL_BOOK";
    private static final String updateBillStatusQuery = "UPDATE BILL_BOOK SET BILL_STATUS = ? WHERE BILL_NO = ?";

    private static final PreparedStatement getAllBillsStatement = StaticDatabaseConnectionHolder
            .getPreparedStatement(getAllBillsQuery);
    private static final PreparedStatement getSpecificBillStatement = StaticDatabaseConnectionHolder
            .getPreparedStatement(getSpecificBillQuery);
    private static final PreparedStatement addBillStatement = StaticDatabaseConnectionHolder
            .getPreparedStatement(addBillQuery);
    private static final PreparedStatement newBillNoStatement = StaticDatabaseConnectionHolder
            .getPreparedStatement(newBillNoQuery);
    private static final PreparedStatement updateBillStatusStatement = StaticDatabaseConnectionHolder
            .getPreparedStatement(updateBillStatusQuery);

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

    @RequestMapping(path = "/service/bill", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public JsonNode addBill(@RequestParam(name = "party_agent_id") int partyAgentId,
            @RequestParam(name = "bill_date") java.sql.Date billDate,
            @RequestParam(name = "delivery_address") String address,
            @RequestParam(name = "payment_due", required = false, defaultValue = "20") int due,
            @RequestParam(name = "rate_per_meter", required = true, defaultValue = "") double ratePerMeter,
            @RequestParam(name = "gst", required = false, defaultValue = "5") double gst,
            @RequestParam(name = "total_amount") double totalAmount,
            @RequestParam(name = "bill_status", required = false, defaultValue = "0") int billStatus,
            @RequestParam(name = "taakas") String taakasArray) throws SQLException, IOException {

        JsonNode returnNode = null;
        addBillStatement.setInt(1, partyAgentId);
        addBillStatement.setDate(2, billDate);
        addBillStatement.setString(3, address);
        addBillStatement.setInt(4, due);
        addBillStatement.setDouble(5, ratePerMeter);
        addBillStatement.setDouble(6, 5.0);
        addBillStatement.setDouble(7, Math.round(totalAmount*100.0)/100.0);
        addBillStatement.setInt(8, billStatus);
        int newBillNumber = getNewBillNo().get("new_bill_number").asInt();
        logger.info("newBillNumber: " + newBillNumber);
        if (addBillStatement.executeUpdate() != 1)
            returnNode = ResultSetToJson.getResponse(-1, "Failed to add bill!");
        else
            returnNode = ResultSetToJson.getOk();
        TaakaClass[] taakaList = new ObjectMapper().readValue(taakasArray, TaakaClass[].class);
        for (TaakaClass taaka : taakaList) {
            logger.info(taaka.taaka_number + " " + taaka.year + " " + taaka.month + " bill: " + newBillNumber);
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

    public static void updateBillStatus(int billNo) throws SQLException {
        updateBillStatusStatement.setInt(1, 1);
        updateBillStatusStatement.setInt(2, billNo);
        if (updateBillStatusStatement.executeUpdate() != 1) {
            throw new SQLException("some error updating bill_status...");
        }
    }

    @RequestMapping(path = "/service/bill/print/{bill_no}", method = RequestMethod.GET, produces = APPLICATION_PDF_VALUE )
    public InputStreamResource getBillPDF(@PathVariable(name = "bill_no") int billNo) throws IOException, SQLException {
        JsonNode billData = getSpecificBill(billNo).get("records").get(0);
        JsonNode taakas = Taaka.getAllTaakasWithBill(billNo).get("records");

        InputStreamResource returnResource = null;

        final float leading=20.f;
        final float margin=50;
        final float tabWidth=100;
        PDDocument document = new PDDocument();
        PDDocumentInformation documentInformation = document.getDocumentInformation();
        documentInformation.setAuthor("iEight Software");
        documentInformation.setCreationDate(Calendar.getInstance());
        documentInformation.setProducer("iEight Software");
        documentInformation.setTitle("Bill Number: " + billNo);

        PDPage bill = new PDPage();
        PDPage challan = new PDPage();

        document.addPage(bill);

        PDPageContentStream billStream = new PDPageContentStream(document, bill);

        billStream.beginText();

        billStream.setFont(PDType1Font.HELVETICA, 12);
        
        PDType0Font monoFont = PDType0Font.load(document,getClass().getResourceAsStream("/public/RobotoMono-Regular.ttf"));
        
        billStream.setFont(monoFont, 12);
        
        billStream.setLeading(leading);
        billStream.newLineAtOffset(margin, bill.getBleedBox().getHeight() - margin);
        StringBuffer titleLine = new StringBuffer();
        for (int i = 0; i < 34; i++)
            titleLine.append("=");
        titleLine.append(" iEight ");
        for (int i = 0; i < 34; i++)
            titleLine.append("=");

        billStream.showText(titleLine.toString());
        billStream.newLine();
        billStream.showText("Bill Number:      " + billNo);

        billStream.newLine();
        billStream.showText("Bill Date:        " + billData.get("bill_date").asText());
        
        JsonNode partyAgentDetails = PartyAgent.getSpecificPartyAgentCombo(billData.get("party_agent_id").asInt()).get("records").get(0);
        billStream.newLine();
        billStream.showText("Party:            "+partyAgentDetails.get("party_name").asText());

        billStream.newLine();
        billStream.showText("GSTIN:            "+partyAgentDetails.get("gstin").asText());

        billStream.newLine();
        billStream.showText("Agent:            "+partyAgentDetails.get("agent_name").asText());

        billStream.newLine();
        billStream.showText("Agent Phone:      "+partyAgentDetails.get("agent_phone").asText());

        billStream.newLine();
        billStream.showText("Delivery Address: "+billData.get("delivery_address").asText());

        billStream.newLine();
        billStream.showText("Rate/Meter:       "+billData.get("rate_per_meter").asDouble());

        billStream.newLine();
        billStream.showText("Total Amount:     " + billData.get("total_amount").asDouble());

        billStream.newLine();
        billStream.newLine();
        
        billStream.showText("Number");
        for(int i=0;i<taakas.size();i++){
            billStream.newLine();
            billStream.showText(taakas.get(i).get("taaka_number").asText());
        }
        
        billStream.newLineAtOffset(tabWidth*2.f/3.f,leading*taakas.size());
        billStream.showText("Production Date");
        for(int i=0;i<taakas.size();i++){
            billStream.newLine();
            billStream.showText(taakas.get(i).get("production_date").asText());
        }        

        billStream.newLineAtOffset(tabWidth*3.f/2.f, leading*taakas.size());
        billStream.showText("Quality");
        for(int i=0;i<taakas.size();i++){
            billStream.newLine();
            billStream.showText(taakas.get(i).get("quality_text").asText());
        }

        billStream.newLineAtOffset(tabWidth, leading*taakas.size());
        billStream.showText("Length");
        for(int i=0;i<taakas.size();i++){
            billStream.newLine();
            billStream.showText(taakas.get(i).get("taaka_length").asText());
        }
        
        billStream.newLineAtOffset(tabWidth, leading*taakas.size());
        billStream.showText("Price");
        for(int i=0;i<taakas.size();i++){
            billStream.newLine();
            double rawPrice = taakas.get(i).get("taaka_length").asDouble()*billData.get("rate_per_meter").asDouble();
            billStream.showText(String.valueOf(Math.round(rawPrice*100.f)/100.f));
        }

        billStream.newLine();
        billStream.showText("--------------");
        billStream.newLine();
        billStream.showText(billData.get("total_amount").asText());
        
        billStream.endText();
        billStream.addRect(0,bill.getBleedBox().getHeight() - (leading*12) , bill.getBleedBox().getWidth(), 1.f);
        billStream.fill();

        
        billStream.close();
        
        
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        document.save(outputStream);

        returnResource = new InputStreamResource(outputStream.getInputStream());

        return returnResource;
    }
}
