package com.ibm.iv.userexit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.iv.util.IVUtil;
import com.ibm.iv.util.IVXMLUtil;
import com.ibm.iv.util.JsonXmlUtil;
import com.customer.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class testJSONtoXML implements YIFCustomApi {
	//protected static YFCLogCategory log = YFCLogCategory.instance(ADIgetExternalInventoryForItemListUE.class.getName());
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public static String getJsonStr() {
		// return
		// "{\"lines\": [ {\"lineId\": \"1\",\"shipNodeAvailability\": [{\"totalAvailableQuantity\": 37,\"onhandAvailableQuantity\": 37,\"onhandEarliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"onhandLatestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"futureAvailableQuantity\": 0,\"futureEarliestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"futureLatestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"earliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"shipNode\": \"HeavyLiftWH3\",\"latestShipTs\": \"2019-02-11T10:55:06.515+0000\" }]}]}";
		return "{\"lines\": [{\"lineId\": \"1\",\"shipNodeAvailability\": [{\"totalAvailableQuantity\": 37,\"onhandAvailableQuantity\": 37,\"onhandEarliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"onhandLatestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"futureAvailableQuantity\": 0,\"futureEarliestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"futureLatestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"earliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"shipNode\": \"HeavyLiftWH3\",\"latestShipTs\": \"2019-02-11T10:55:06.515+0000\"}]},{\"lineId\": \"2\",\"shipNodeAvailability\": [{\"totalAvailableQuantity\": 37,\"onhandAvailableQuantity\": 37,\"onhandEarliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"onhandLatestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"futureAvailableQuantity\": 0,\"futureEarliestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"futureLatestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"earliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"shipNode\": \"HeavyLiftWH3\",\"latestShipTs\": \"2019-02-11T10:55:06.515+0000\"},{\"totalAvailableQuantity\": 37,\"onhandAvailableQuantity\": 37,\"onhandEarliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"onhandLatestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"futureAvailableQuantity\": 0,\"futureEarliestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"futureLatestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"earliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"shipNode\": \"HeavyLiftWH3\",\"latestShipTs\": \"2019-02-11T10:55:06.515+0000\"}]}]}";
	}
	
	public void testJSON(){
	
		try {
			/*Document jsonDoc = JsonXmlUtil.getXMLFromJSON(getJsonStr(),null);
			System.out.println(IVXMLUtil.getXMLString(jsonDoc));*/
			
			
			
			//System.out.println(jsonDoc.getClass().getName());
			Document docTxtFile = XMLUtil.getDocumentFromFile(new File("C:\\DATA\\Adidas\\GetNodeAvail.xml"));
			System.out.println("Doc XML"+ IVXMLUtil.getXMLString(docTxtFile));
			
			Element eleDocTxtFile = docTxtFile.getDocumentElement();
			Element eleOutput = (Element) eleDocTxtFile.getElementsByTagName("Output").item(0);
			System.out.println("Response Output::"+ SCXmlUtil.getString(eleOutput));
			String strOutputStatus = eleOutput.getAttribute("Status");
			System.out.println("Output Status::"+ strOutputStatus);
			String strTxtFile;
			if(!YFCObject.isVoid(strOutputStatus)&& "200".equals(strOutputStatus)){
				strTxtFile = eleOutput.getTextContent();
				Document jsonTestDoc = JsonXmlUtil.getXMLFromJSON(strTxtFile, "ShipNodeAvailability");
				System.out.println("Translated Json From Txt" + IVXMLUtil.getXMLString(jsonTestDoc));
			}
			//String strTxtFile = "{\"lines\": [{\"lineId\": \"1\",\"shipNodeAvailability\": [{\"totalAvailableQuantity\": 37,\"onhandAvailableQuantity\": 37,\"onhandEarliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"onhandLatestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"futureAvailableQuantity\": 0,\"futureEarliestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"futureLatestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"earliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"shipNode\": \"HeavyLiftWH3\",\"latestShipTs\": \"2019-02-11T10:55:06.515+0000\"}]},{\"lineId\": \"2\",\"shipNodeAvailability\": [{\"totalAvailableQuantity\": 37,\"onhandAvailableQuantity\": 37,\"onhandEarliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"onhandLatestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"futureAvailableQuantity\": 0,\"futureEarliestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"futureLatestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"earliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"shipNode\": \"HeavyLiftWH3\",\"latestShipTs\": \"2019-02-11T10:55:06.515+0000\"},{\"totalAvailableQuantity\": 37,\"onhandAvailableQuantity\": 37,\"onhandEarliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"onhandLatestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"futureAvailableQuantity\": 0,\"futureEarliestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"futureLatestShipTs\": \"2500-01-01T00:00:00.000+0000\",\"earliestShipTs\": \"2019-02-11T10:55:06.515+0000\",\"shipNode\": \"HeavyLiftWH3\",\"latestShipTs\": \"2019-02-11T10:55:06.515+0000\"}]}]}";
			/*Document jsonTestDoc = JsonXmlUtil.getXMLFromJSON(strTxtFile, "ShipNodeAvailability");
			System.out.println("Translated Json From Txt" + IVXMLUtil.getXMLString(jsonTestDoc));*/
			
		} catch (Exception e) {
			//log.debug("Exception occurred while setting the item ID in input xml :" + e.getMessage());
		}
		
		
	}
	
	public static void main(String args []) {
		testJSONtoXML t1 = new testJSONtoXML();
		t1.testJSON();
	}

}
