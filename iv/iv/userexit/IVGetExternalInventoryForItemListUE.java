package com.ibm.iv.userexit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.iv.constants.IVXMLLiterals;
import com.ibm.iv.util.IVUtil;
import com.ibm.iv.util.IVXMLUtil;
import com.ibm.iv.util.JsonXmlUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetExternalInventoryForItemListUE;

public class IVGetExternalInventoryForItemListUE implements YFSGetExternalInventoryForItemListUE {
	protected static YFCLogCategory log = YFCLogCategory.instance(IVGetExternalInventoryForItemListUE.class.getName());


	@Override
	public Document getExternalInventoryForItemList(YFSEnvironment env,
			Document inXML) throws YFSUserExitException {
		Document outXML;
		YFCDocument outDoc = YFCDocument.createDocument(IVXMLLiterals.ELE_ITEM_LIST);
		try {
			
			YFCDocument inDoc = YFCDocument.getDocumentFor(inXML);
			YFCElement eleItemList = inDoc.getDocumentElement();			
			YFCElement eleOutDoc = outDoc.getDocumentElement();
			
			if (eleItemList.hasChildNodes()) {

				YFCIterable<YFCElement> items = eleItemList.getChildren();
				int i=1;
				for (YFCElement item : items) {
					YFCElement eleOutDocItem = eleOutDoc.createChild(IVXMLLiterals.ELE_ITEM);
					item.setAttribute(IVXMLLiterals.ATTR_LINE_ID, i);
					eleOutDocItem.setAttribute(IVXMLLiterals.ATTR_LINE_ID, i);
					eleOutDocItem.setAttribute(IVXMLLiterals.ATTR_ITEM_ID, item.getAttribute(IVXMLLiterals.ATTR_ITEM_ID));
					//eleOutDocItem.setAttribute(IVXMLLiterals.ATTR_ORGANIZATION_CODE, item.getAttribute(IVXMLLiterals.ATTR_ORGANIZATION_CODE));
					//Changing the attribute from OrganisationCode to OwnerOrganisationCode
					eleOutDocItem.setAttribute(IVXMLLiterals.ATTR_ORGANIZATION_CODE, item.getAttribute(IVXMLLiterals.ATTR_OWNER_ORGANIZATION_CODE));
					eleOutDocItem.setAttribute(IVXMLLiterals.ATTR_PRODUCT_CLASS, item.getAttribute(IVXMLLiterals.ATTR_PRODUCT_CLASS));
					eleOutDocItem.setAttribute(IVXMLLiterals.ATTR_UOM, item.getAttribute(IVXMLLiterals.ATTR_UOM));
					i++;
				}
			}
			log.verbose("UE Input"+ SCXmlUtil.getString(inXML));
			Document docGetNodeAvailabilityFromIV = IVUtil.invokeService( env, "IVGetNodeAvailability", inXML );			
			Element eleDocTxtFile = docGetNodeAvailabilityFromIV.getDocumentElement();
			Element eleOutput = (Element) eleDocTxtFile.getElementsByTagName(IVXMLLiterals.ELE_OUTPUT).item(0);
			log.verbose("Response Output::"+ SCXmlUtil.getString(eleOutput));
			String strOutputStatus = eleOutput.getAttribute(IVXMLLiterals.ATTR_STATUS);
			log.verbose("Output Status::"+ strOutputStatus);
			Document docJsonOut = null;
			if(!YFCObject.isVoid(strOutputStatus)&& "200".equals(strOutputStatus)){
				String strTxtFile = eleOutput.getTextContent();
				docJsonOut = JsonXmlUtil.getXMLFromJSON(strTxtFile, "ShipNodeAvailability");
				log.verbose("Json to Doc::" + IVXMLUtil.getXMLString(docJsonOut));
			}

			if(!YFCObject.isNull(docJsonOut)){
			YFCDocument yfsJsonOutDoc = YFCDocument.getDocumentFor(docJsonOut);
			YFCElement yfsJsonOutEle = yfsJsonOutDoc.getDocumentElement();
			if (eleOutDoc.hasChildNodes()) {

				YFCIterable<YFCElement> eleOutItems = eleOutDoc.getChildren();
				int i=1;
				for (YFCElement eleOutitem : eleOutItems) {
					String strOutLineId = eleOutitem.getAttribute(IVXMLLiterals.ATTR_LINE_ID);
					if (yfsJsonOutEle.hasChildNodes()) {
						YFCIterable<YFCElement> eleJsonLines = yfsJsonOutEle.getChildren();
						for (YFCElement eleJsonLine : eleJsonLines) {
							String strLineId = eleJsonLine.getAttribute("lineId");
							if ((!YFCObject.isVoid(strOutLineId))&& (!YFCObject.isVoid(strLineId))&& strLineId.equals(strOutLineId)){
								YFCElement eleSupplies = eleOutitem.createChild(IVXMLLiterals.ELE_SUPPLIES);
								
								if (eleJsonLine.hasChildNodes()) {
									YFCIterable<YFCElement> eleShipNodeAvailabilities = eleJsonLine.getChildren();
									for (YFCElement eleShipNodeAvailability : eleShipNodeAvailabilities) {
										YFCElement eleSupply = eleSupplies.createChild(IVXMLLiterals.ELE_SUPPLY);
										eleSupply.setAttribute(IVXMLLiterals.ATTR_FIRST_SHIP_DATE, eleShipNodeAvailability.getAttribute("onhandEarliestShipTs"));
										eleSupply.setAttribute(IVXMLLiterals.ATTR_LAST_SHIP_DATE, eleShipNodeAvailability.getAttribute("onhandLatestShipTs"));
										eleSupply.setAttribute(IVXMLLiterals.ATTR_QUANTITY, eleShipNodeAvailability.getAttribute("onhandAvailableQuantity"));
										eleSupply.setAttribute(IVXMLLiterals.ATTR_SHIP_NODE, eleShipNodeAvailability.getAttribute("shipNode"));
									}//end of Supply for loop
								}//end of Json ShipNode Availability if
								
							}//end of Line Id if
						}//end of Json Lines for loop
							
					}
					eleOutitem.removeAttribute(IVXMLLiterals.ATTR_LINE_ID);
				}
			}
			}
		} catch (Exception e) {
			log.debug("Exception in the IVGetExternalInventoryForItemListUE:" + e.getMessage());
		}
		outXML = outDoc.getDocument();
		log.verbose("UE Output"+ SCXmlUtil.getString(outDoc.getDocument()));
		return outXML;
	}

}
