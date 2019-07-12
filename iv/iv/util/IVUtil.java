package com.ibm.iv.util;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ibm.iv.constants.IVXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;



public class IVUtil {

	private static Logger logger = Logger.getLogger(IVUtil.class.getName());
	
	private IVUtil(){
	  
	}

	/**
	 * @param env YFSEnvironment object.
	 * @param apiName Name of API
	 * @param inXMLDoc input document to API
	 * @return returns API output document
	 * @throws YIFClientCreationException
	 * @throws RemoteException
	 * @throws YFSException
	 * @throws Exception Exception
	 * @description invoke API within the system
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName, Document inXMLDoc,
			Document inTemplate) throws YIFClientCreationException,  RemoteException {

		logger.info("Entering invokeAPI method");
		if (!YFCCommon.isVoid(inTemplate)) {
			env.setApiTemplate(apiName, inTemplate);
		}
		YIFApi yifApi = YIFClientFactory.getInstance().getLocalApi();
		Document outXML = yifApi.invoke(env, apiName, inXMLDoc);
		env.clearApiTemplates();
		logger.info("Exiting invokeAPI method");
		return outXML;

	}

	/**
	 * Calls a YIF api.
	 * 
	 * @param env YFS env
	 * @param input Input xml
	 * @param apiName String api name
	 * @param templateName String template name
	 * @return Result xml on calling the sterling api
	 * @throws YIFClientCreationException 
	 * @throws RemoteException 
	 * @throws YFSException 
	 * @throws Exception if the api can't be called
	 */
	public static Document invokeAPI(YFSEnvironment env, Document input, String apiName,
			String templateName) throws YIFClientCreationException,  RemoteException {
		if (!isEmpty(templateName)) {
			env.setApiTemplate(apiName, templateName);
		}

		YIFApi api = YIFClientFactory.getInstance().getApi();
		Document outputDoc = api.invoke(env, apiName, input);
		env.clearApiTemplate(apiName);

		return outputDoc;
	}

	/**
	 * @param env YFSEnvironment object.
	 * @param serviceName Name of the service
	 * @param inXMLDoc Service input document
	 * @return Output document
	 * @throws YIFClientCreationException 
	 * @throws RemoteException 
	 * @throws YFSException 
	 * @description invoke service within the system
	 */
	public static Document invokeService(YFSEnvironment env, String serviceName, Document inXMLDoc) throws YIFClientCreationException,  RemoteException
	{

		logger.info("Entering invokeService method");
		YIFApi oApi = YIFClientFactory.getInstance().getLocalApi();
		logger.info("Exiting invokeService method");
		return oApi.executeFlow(env, serviceName, inXMLDoc);

	}


	

	/**
	 * @param env YFSEnvironment object.
	 * @param apiName API Name
	 * @param inXMLDoc APi Input document
	 * @return OutPut of API
	 * @throws YIFClientCreationException 
	 * @throws RemoteException 
	 * @throws YFSException 
	 * @throws Exception Exception
	 * @description invoke API within the system
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName, Document inXMLDoc) throws YIFClientCreationException,  RemoteException
	{

		logger.info("Entering invokeAPI method");
		YIFApi yifApi = YIFClientFactory.getInstance().getLocalApi();
		Document outXML = yifApi.invoke(env, apiName, inXMLDoc);
		logger.info("Exiting invokeAPI method");
		return outXML;

	}

	/**
	 * Description of isAffirmative Return true for Y / true / 1 else false.
	 * 
	 * @param value input string
	 * @return true if Y/true/1 else false
	 *
	 */
	public static boolean isAffirmative(String value) {

		if (value == null) {
			return false;
		} else if ("Y".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "1".equals(value)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param str input string.
	 * @return true if string is not empty/null esle false
	 * @description This method check the input string is null or empty,if string is null or empty it
	 *              will return true else false.
	 */
	public static boolean isEmpty(String str) {
		return str == null || (str.trim().length() == 0);
	}

	/**
	 * 
	 * @param strExCode Custom error code.
	 * @param strOrgCode organization code
	 * @param strParentOrgCode parent organization code
	 * @param strErrorAttr error attribute name
	 * @param strErrorAttrVal error attribute value
	 * @description This method get the required information as parameters and throw the exception.
	 */

	public static void throwException(String strExCode, String strOrgCode, String strParentOrgCode,
			String strErrorAttr, String strErrorAttrVal) {

		YFCException ex = new YFCException(strExCode);
		ex.setAttribute(IVXMLLiterals.ATTR_ORGANIZATION_CODE, strOrgCode);
		ex.setAttribute(IVXMLLiterals.ATTR_PARENT_ORGANIZATION_CODE, strParentOrgCode);
		ex.setAttribute(strErrorAttr, strErrorAttrVal);
		throw ex;

	}

	/**
	 * 
	 * @param datestr1 date string in format 2017-05-22T19:15:15-04:00
	 * @param datestr2 date string in format 2017-05-24T19:15:15-04:00 *
	 * @return 1 if datestr1 is before datestr2,2 if datestr1 is after datestr2.0 if datestr1 and
	 *         datestr2 are same else 4
	 * @throws ParseException
	 * 
	 * @description This method get the required information as parameters and throw the exception.
	 */

	public static int compareDate(String datestr1, String datestr2) throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date datestr1Date = formatter.parse(datestr1);
		Date datestr2Date = formatter.parse(datestr2);

		if (datestr1Date.before(datestr2Date)) {
			return 1;
		}
		if (datestr1Date.after(datestr2Date)) {
			return 2;
		}
		if (datestr1Date.equals(datestr2Date)) {

			return 0;
		}
		return 4;

	}

	/**
	 * 
	 * @param strExCode Custom error code.   
	 * @throws YFSUserExitException 
	 * @description This method get the required information as parameters and throw UserExitException.
	 */

	public static void throwUserExitException(String strExCode) throws YFSUserExitException {
		YFSUserExitException ueEx = new YFSUserExitException();  
		ueEx.setErrorCode(strExCode);    
		throw ueEx;

	}
	
	/**
	 * CommonCode call for a Code Type and Code Value to get the Code Short Description.
	 * 
	 * @param env
	 * @param codeType
	 * @param codeValue
	 * @return
	 */
	public static Document callGetCommonCodeListAPIForCodeTypeCodeValue(YFSEnvironment env, String codeType, String codeValue) {

		YFCDocument getCodeInput = YFCDocument.createDocument(IVXMLLiterals.ELE_COMMON_CODE);
		YFCElement getCodeDocEle = getCodeInput.getDocumentElement();
		// getCodeDocEle.setAttribute(VFCXMLLiterals.XMLAttributes.ORG_CODE, orgCode);
		getCodeDocEle.setAttribute(IVXMLLiterals.ATTR_CODE_TYPE, codeType);
		getCodeDocEle.setAttribute(IVXMLLiterals.ATTR_CODE_VALUE, codeValue);

		logger.debug("The input XML for getCommonCOdeList API is: " + getCodeInput.getString());

		Document ret = null;
		try {
			ret = IVUtil.invokeAPI(env, IVXMLLiterals.API_GET_COMMON_CODE_LIST, getCodeInput.getDocument());
		}
		catch (YFSException e) {
			logger.debug("Exception occurred while calling getCommonCodeList API :" + e.getMessage());
			
		}
		catch (Throwable t) {
			logger.debug("Exception occurred while calling getCommonCodeList API :" + t.getMessage());
		}
		return ret;
	}
	
}

