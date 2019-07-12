package com.ibm.iv.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.ibm.sterling.afc.jsonutil.PLTJSONUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class JsonXmlUtil {
	
	/*This method returns the corresponding XML Document for the JSON Object*/
	public static Document getXMLFromJSON(JSONObject jsonObj, String strRootElemet) throws JSONException, Exception{
		return PLTJSONUtils.getXmlFromJSON(jsonObj.toString(), strRootElemet);
	}
	
	/*This method returns the corresponding XML Document for the JSON string*/
	public static Document getXMLFromJSON(String jsonStr, String strRootElemet) throws JSONException, Exception{
		return PLTJSONUtils.getXmlFromJSON(jsonStr, strRootElemet);
	}
	
	/*This method returns the JSON Object for the XML Document passed*/
	public static JSONObject getJSONFromXML(Document docIn) throws Exception{
		
		YFCElement yfcEleIn = YFCDocument.getDocumentFor(docIn).getDocumentElement();
		JSONObject jsonObj = PLTJSONUtils.getJSONObjectFromXML((Element)yfcEleIn.getDOMNode(), null, null);
		JSONObject ret = jsonObj.getJSONObject(yfcEleIn.getTagName());
		if (ret == null)
		{
			if (jsonObj.length() == 1) {
				ret = jsonObj.getJSONObject(JSONObject.getNames(jsonObj)[0]);
			}
			if (ret != null) {}
		}
		return jsonObj;
	}
	
	/*This method returns the JSON Object for the XML String passed*/
	public static JSONObject getJSONFromXML(String inXML) throws ParserConfigurationException, SAXException, IOException, Exception {
		JSONObject jobj = getJSONFromXML(IVXMLUtil.getDocument(inXML));
		return jobj;
	}
}
