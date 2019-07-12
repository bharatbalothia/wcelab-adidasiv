package com.ibm.iv.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.iv.constants.IVConstants;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.util.YFCException;


public class IVXMLUtil {

  private static Logger logger = Logger.getLogger(IVXMLUtil.class.getName());

  private static Map<String, XPath> pathCache = Collections
      .synchronizedMap(new HashMap<String, XPath>());
  
  /**
 	 * This method takes Document as input and returns the XML String.
 	 * @param document   a valid document object for which XML output in String form is required.
 	 * @return String type
 	 */
     
     public static String getXMLString(Document document) {
 		String strXMLString = null;
 		if(null != document){
 			strXMLString = serialize(document);
 		}
 		return strXMLString;
 	}
     
     /**
 	 * Returns a formatted XML string for the Node, using encoding 'iso-8859-1'.
 	 *
 	 * @param node   a valid document object for which XML output in String form is required.
 	 *
 	 * @return the formatted XML string.
 	 */

 	public static String serialize(Node node) {
 		return serialize(node, "iso-8859-1", true);
 	}
 	
 	/**
 	 *	Return a XML string for a Node, with specified encoding and indenting flag.
 	 *	<p>
 	 *	<b>Note:</b> only serialize DOCUMENT_NODE, ELEMENT_NODE, and DOCUMENT_FRAGMENT_NODE
 	 *
 	 *	@param node the input node.
 	 *	@param encoding such as "UTF-8", "iso-8859-1"
 	 *	@param indenting indenting output or not.
 	 *
 	 *	@return the XML string
 	 */
 	public static String serialize(Node node, String encoding, boolean indenting) {
 		OutputFormat outFmt = null;
 		StringWriter strWriter = null;
 		XMLSerializer xmlSerializer = null;
 		String retVal = null;
 		
 		try {
 			outFmt = new OutputFormat("xml", encoding, indenting);
 			outFmt.setOmitXMLDeclaration(true);
 			strWriter = new StringWriter();

 			xmlSerializer = new XMLSerializer(strWriter, outFmt);

 			short ntype = node.getNodeType();

 			switch(ntype) {
 			case Node.DOCUMENT_FRAGMENT_NODE:
 				xmlSerializer.serialize((DocumentFragment) node);
 				break;
 			case Node.DOCUMENT_NODE:
 				xmlSerializer.serialize((Document) node);
 				break;
 			case Node.ELEMENT_NODE:
 				xmlSerializer.serialize((Element) node);
 				break;
 			default: throw new IOException("Can serialize only Document, DocumentFragment and Element type nodes");
 			}

 			retVal = strWriter.toString();
 		} catch (IOException e) {
 			retVal = e.getMessage();
 		} finally {
 			try {
 				//added for violation-Value is null and guaranteed to be dereferenced on exception path fix
 				if(strWriter!=null)
 				strWriter.close();
 			} catch (IOException ie) {
 				retVal = ie.getMessage();

 			}
 		}

 		return retVal;
 	}



  /**
   * This method gets the specified Child element of the parent passed.
   * 
   * @param eleParent parent element
   * @param strChildName child element name
   * @return child element.
   */

  public static Element getChildElement(Element eleParent, String strChildName) {
    Element eleChild = null;
    if (eleParent != null && !YFCObject.isVoid(strChildName)) {
      for (Node n = eleParent.getFirstChild(); n != null; n = n.getNextSibling()) {
        if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(strChildName)) {
          return (Element) n;
        }
      }
    }

    return eleChild;
  }

  /**
   * This method gets the node as input and convert it into document and return document.
   * 
   * @param inputNode
   * 
   * @return document
   */
  public static Document convertNodeToDocument(Node inputNode) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document newDocument = builder.newDocument();
    Node importedNode = newDocument.importNode(inputNode, true);
    newDocument.appendChild(importedNode);
    return newDocument;
  }

  /**
   * 
   * @param newDoc input document to print in console.
   * @throws TransformerFactoryConfigurationError
   * @throws TransformerException
   * @throws Exception Exception
   * @description This method accepts document as input and log the document into console
   */

  public static void printDocumentInConsole(Document newDoc) throws  TransformerException
        {
    DOMSource domSource = new DOMSource(newDoc);
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    StringWriter sw = new StringWriter();
    StreamResult sr = new StreamResult(sw);
    transformer.transform(domSource, sr);    
  }



  /**
   * 
   * @param newDoc input document for which string needs to obtain.
   * @throws TransformerFactoryConfigurationError
   * @throws TransformerException
   * @throws Exception Exception
   * @description This method accepts document as input and return the document string
   */

  public static String getDoumentString(Document newDoc)
      throws  TransformerException {
    DOMSource domSource = new DOMSource(newDoc);
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    StringWriter sw = new StringWriter();
    StreamResult sr = new StreamResult(sw);
    transformer.transform(domSource, sr);
    return sw.toString();

  }

  /**
   * Create a new blank XML Document.
   */
  public static Document newDocument() throws ParserConfigurationException {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
  }

  /**
   * Create a new DOM document that is namespace aware.
   * 
   * @throws ParserConfigurationException
   */
  public static Document newDocument(String namespace, String qualifiedName, DocumentType docType)
      throws ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    DOMImplementation domImpl = db.getDOMImplementation();
    return domImpl.createDocument(namespace, qualifiedName, docType);
  }

  /**
   * Parse an XML string or a file, to return the Document.
   *
   * @param inXML if starts with '&lt;', it is a XML string; otherwise it should be an XML file name
   *
   * @return the Document object generated
   */
  public static Document getDocument(String inXML) throws ParserConfigurationException,
      SAXException, IOException {
    String currentXML = null;
    if (inXML != null && inXML.length() > 0) {
      currentXML = inXML.trim();
      if (currentXML.startsWith("<")) {
        StringReader strReader = new StringReader(currentXML);
        InputSource iSource = new InputSource(strReader);
        return getDocument(iSource);
      }
      // It's a file
      FileReader inFileReader = new FileReader(currentXML);
      Document retVal = null;
      try {
        InputSource iSource = new InputSource(inFileReader);
        retVal = getDocument(iSource);
      } finally {
        inFileReader.close();
      }
      return retVal;
    } else {
      return null;
    }
  }

  /**
   * Generate a Document object according to InputSource object.
   */
  public static Document getDocument(InputSource inSource) throws ParserConfigurationException,
      SAXException, IOException {
    DocumentBuilder dbdr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    return dbdr.parse(inSource);
  }
  
  /**
   * Create a Document object with input as the name of document element.
   *
   * @param docElementTag : the document element name
   */
  public static Document createDocument(String docElementTag) throws ParserConfigurationException {
    Document doc = newDocument();
    Element ele = doc.createElement(docElementTag);
    doc.appendChild(ele);
    return doc;
  }

 
 
  /**
   * Return a decendent of first parameter, that is the first one to match the XPath specified in
   * the second parameter.
   *
   * @param ele The element to work on.
   * @param tagName format like "CHILD/GRANDCHILD/GRANDGRANDCHILD"
   *
   * @return the first element that matched, null if nothing matches.
   */
  public static Element getFirstElementByName(Element ele, String tagName) {
    StringTokenizer st = new StringTokenizer(tagName, "/");
    Element curr = ele;
    while (st.hasMoreTokens()) {
      String tag = st.nextToken();
      Node node;
      for (node = curr.getFirstChild(); node != null; node = node.getNextSibling())
        if (node.getNodeType() == 1 && tag.equals(node.getNodeName()))
          break;

      if (node != null)
        curr = (Element) node;
      else
        return null;
    }
    return curr;
  } 

  /**
   * For an Element node, return its Text node's value; otherwise return the node's value.
   */
  public static String getNodeValue(Node node) {
    if (node.getNodeType() == 1) {
      for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
        if (child.getNodeType() == 3)
          return child.getNodeValue();

      return null;
    } else {
      return node.getNodeValue();
    }
  }

  /**
   * For an Element node, set its Text node's value (create one if it does not have); otherwise set
   * the node's value.
   */
  public static void setNodeValue(Node node, String val) {
    if (node.getNodeType() == 1) {
      Node child;
      for (child = node.getFirstChild(); child != null; child = child.getNextSibling())
        if (child.getNodeType() == 3)
          break;

      if (child == null) {
        child = node.getOwnerDocument().createTextNode(val);
        node.appendChild(child);
      } else {
        child.setNodeValue(val);
      }
    } else {
      node.setNodeValue(val);
    }
  }

  /**
   * Returns document element for document.
   */
  public static Element getRootElement(Document doc) {
    return doc.getDocumentElement();
  }

  public static Transformer getXSLTransformer(String xslFileName)
      throws TransformerConfigurationException {
    TransformerFactory tFactory = TransformerFactory.newInstance();
    return tFactory.newTransformer(getXSLStreamSource(xslFileName));
  }






  /**
   * This method is for adding child Nodes to parent node element.
   * 
   * @param parentElement Parent Element under which the new Element should be present
   * @param childElement Child Element which should be added.
   */
  public static void appendChild(Element parentElement, Element childElement) {
    parentElement.appendChild(childElement);
  }

  /**
   * This method is for setting the attribute of an element.
   * 
   * @param objElement Element where this attribute should be set
   * @param attributeName Name of the attribute
   * @param attributeValue Value of the attribute
   */
  public static void setAttribute(Element objElement, String attributeName, String attributeValue) {
    objElement.setAttribute(attributeName, attributeValue);
  }



  /**
   * This method is for removing an attribute from an Element.
   * 
   * @param objElement Element from where the attribute should be removed.
   * @param attributeName Name of the attribute
   */
  public static void removeAttribute(Element objElement, String attributeName) {
    objElement.removeAttribute(attributeName);
  }

  /**
   * This method is for removing the child element of an element
   * 
   * @param parentElement Element from where the child element should be removed.
   * @param childElement Child Element which needs to be removed from the parent
   */
  public static void removeChild(Element parentElement, Element childElement) {
    parentElement.removeChild(childElement);
  }

  /**
   * Method to create a text mode for an element.
   * 
   * @param doc the XML document on which the node has to be created.
   * @param parentElement the element for which the text node has to be created.
   * @param elementValue the value for the text node.
   */
  public static void createTextNode(Document doc, Element parentElement, String elementValue) {
    parentElement.appendChild(doc.createTextNode(elementValue));
  }

 



  /**
   * @param document input document.
   * @param fileName name of file.
   * @throws IOException IOException
   */
  public static void flushToAFile(Document document, String fileName) throws IOException {
    if (document != null) {
      OutputFormat oFmt = new OutputFormat(document, "iso-8859-1", true);
      oFmt.setPreserveSpace(true);
      XMLSerializer xmlOP = new XMLSerializer(oFmt);
      FileWriter out = new FileWriter(new File(fileName));
      xmlOP.setOutputCharStream(out);
      xmlOP.serialize(document);
      out.close();
    }
  }

  /**
   * 
   * @param document input document.
   * @param writer writer
   * @throws IOException exception
   */
  public static void flushToAFile(Document document, Writer writer) throws IOException {
    if (document != null) {
      OutputFormat oFmt = new OutputFormat(document, "iso-8859-1", true);
      oFmt.setPreserveSpace(true);
      XMLSerializer xmlOP = new XMLSerializer(oFmt);
      xmlOP.setOutputCharStream(writer);
      xmlOP.serialize(document);
      writer.close();
    }
  }

  /**
   * This method is used for constructing process Instruction.
   */
  public static void createProcessingInstruction(Document doc, Element rootElement,
      String strTarget, String strData) {
    org.w3c.dom.ProcessingInstruction p = doc.createProcessingInstruction(strTarget, strData);
    doc.insertBefore(p, rootElement);
  }

  /** Gets the Parser from the input XML. **/
  /** Gets the attribute of a node. **/
  public static String getAttribute(Object element, String attributeName) {
    if (element != null) {
      String attributeValue = ((Element) element).getAttribute(attributeName);
      attributeValue = attributeValue != null ? attributeValue.trim() : attributeValue;
      return attributeValue;
    } else {
      return null;
    }
  }

  /** Gets the node value for a sub element under a Element with unique name. **/
  public static Element getUniqueSubNode(Element element, String nodeName) {
    Element uniqueElem = null;
    NodeList nodeList = element.getElementsByTagName(nodeName);
    if (nodeList != null && nodeList.getLength() > 0) {
      int size = nodeList.getLength();
      for (int count = 0; count < size; count++) {
        uniqueElem = (Element) nodeList.item(count);
        if (uniqueElem != null && uniqueElem.getParentNode() == element)
          break;
      }

    }
    return uniqueElem;
  }

  /** Gets the node value for a sub element under a Element with unique name. **/
  public static String getUniqueSubNodeValue(Element element, String nodeName) {
    NodeList nodeList = element.getElementsByTagName(nodeName);
    if (nodeList != null) {
      Element uniqueElem = (Element) nodeList.item(0);
      if (uniqueElem != null) {
        if (uniqueElem.getFirstChild() != null)
          return uniqueElem.getFirstChild().getNodeValue();
        else
          return null;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /** Gets the node list for sub elements under a Element with given name. **/
  public static List getSubNodeList(Element element, String nodeName) {
    NodeList nodeList = element.getElementsByTagName(nodeName);
    List elemList = new ArrayList();
    for (int count = 0; count < nodeList.getLength(); count++)
      elemList.add(nodeList.item(count));

    return elemList;
  }

  /**
   * 
   * @param startElement input element
   * @param elemName elemant name
   * @return List of elements
   * @description This methods returns the list of elements,name specified by elemName from input
   *              element.
   */
  public static List getElementsByTagName(Element startElement, String elemName) {
    NodeList nodeList = startElement.getElementsByTagName(elemName);
    List elemList = new ArrayList();
    for (int count = 0; count < nodeList.getLength(); count++)
      elemList.add(nodeList.item(count));

    return elemList;
  }

 



  /**
   * @param inXML input document.
   * @param xpath xpath of elements.
   * @return list with xpath elements
   * @throws ParserConfigurationException ParserConfigurationException
   * @throws TransformerException TransformerException
   * @description get the elements from xpath of input document and return the element list.
   */
  public static List getElementListByXpath(Document inXML, String xpath)
      throws TransformerException {

    List elementList = new ArrayList();
    CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
    NodeList nodeList = aCachedXPathAPI.selectNodeList(inXML, xpath);
    int iNodeLength = nodeList.getLength();
    for (int iCount = 0; iCount < iNodeLength; iCount++) {
      Node node = nodeList.item(iCount);
      elementList.add(node);
    }

    return elementList;
  }

  /**
   * 
   * @param inXML input document.
   * @param xpath xpath of element.
   * @return element which match the xpath.
   * @throws TransformerException TransformerException.
   * @description This method return the xpath match element of input doc.
   */
  public static Element getElementByXPath(Document inXML, String xpath) throws TransformerException {   
    CachedXPathAPI oCachedXPathAPI = new CachedXPathAPI();
    Node node = oCachedXPathAPI.selectSingleNode(inXML, xpath);
    return (Element) node;
  }

  /**
   * @param xslFileName XSLFileName.
   * @return StreamSource StreamSource of XSL
   * @throws Exception Exception
   * @description returns the XSL StreamSource
   */
  public static StreamSource getXSLStreamSource(String xslFileName) {
    return new StreamSource(xslFileName);
  }

 

  /**
   * @param inXML input document.
   * @param XPath xpath of node.
   * @return node list
   * @throws ParserConfigurationException ParserConfigurationException
   * @throws TransformerException TransformerException
   * @description get the xpath nodes from input document and return nodelist.
   */

  public static NodeList getNodeListByXpath(Document inXML, String xpath)
      throws  TransformerException {
    CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
    return aCachedXPathAPI.selectNodeList(inXML, xpath);

  }

  /**
   * @param inXML input document.
   * @param targetElement target element
   * @param sourceElement source element.
   * @return updated document.
   * @throws DOMException DOMException
   * @description this methods replace the targetElement with sourceElement in inXML document and
   *              return update document.
   */

  public static Document replaceElementsWithElement(Document inXML, Element targetElement,
      Element sourceElement)  {
    Node parentElement = sourceElement.getParentNode();
    parentElement.removeChild(sourceElement);
    Node importedNode = inXML.importNode(targetElement, true);
    parentElement.appendChild(importedNode);
    return inXML;
  }

  /**
   * Create a new document object with input element as the root.
   *
   * @param inputElement Input Element object
   * @param deep Include child nodes of this element true/false
   * @return XML Document object
   * @throws IllegalArgumentException if input is invalid
   * @throws ParserConfigurationException 
   * @throws Exception incase of any other exception
   */
  public static Document getDocument(Element inputElement, boolean deep)
      throws  ParserConfigurationException {
    // Validate input element
    if (inputElement == null) {
      throw new IllegalArgumentException("Input element cannot be null in "
          + "XmlUtils.getDocument method");
    }

    // Create a new document
    Document outputDocument = getDocument();

    // Import data from input element and
    // set as root element for output document
    outputDocument.appendChild(outputDocument.importNode(inputElement, deep));

    // return output document
    return outputDocument;
  }

  /**
   * Removes processing instruction from input XML String. Requirement is that input XML string
   * should be a valid XML.
   *
   * @param xmlString XML String they may contain processing instruction
   * @return XML String
   * @throws IllegalArgumentException for Invalid input.
   */
  public static String removeProcessingInstruction(String xmlString)
       {
    // Validate input XML string
    if (xmlString == null) {
      throw new IllegalArgumentException("Input XML string cannot be null in "
          + "XmlUtils.removeProcessingInstruction method");
    }

    // Is input contains processing instruction
    if (xmlString.toLowerCase().trim().startsWith("<?xml")) {
      // Get the ending index of processing instruction
      int processInstructionEndIndex = xmlString.indexOf("?>");

      // If processing instruction ending found,
      if (processInstructionEndIndex != -1) {
        // Remove processing instruction

        return xmlString.substring(processInstructionEndIndex + 2);
      }
    }

    // Return XML string after update
    return xmlString;
  }

  /**
   * Creates and empty Document object.
   * 
   * @throws ParserConfigurationException
   *
   * @throws Exception incase of any exception
   */
  public static Document getDocument() throws ParserConfigurationException {
    // Create a new Document Bilder Factory instance
    // SCR# 2392
    DocumentBuilderFactory documentBuilderFactory = new DocumentBuilderFactoryImpl();

    // Create new document builder
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

    // Create and return document object
    return documentBuilder.newDocument();
  }




  /**
   * Returns the Wrapper object around the input Node. <br>
   * <br>
   * <b> To get the Wrapper instance ove the input Node </b>
   * <code><pre> XMLManager.getInstance.getWrapperFor(node);
   * </pre></code>
   * 
   * @param node Node
   * @return XPathWrapper The wrapper object over the input Node.
   */
  public static synchronized XPathWrapper getWrapperFor(Node node) {

    return IVXMLUtil.getInstance().new XPathWrapper(node);
  }

  private static IVXMLUtil getInstance() {
    return new IVXMLUtil();
  } 


  /**
   * Returns the parsed Date of the input String. *
   * 
   * @param str input string
   * @return Date
   * @throws Exception Exception
   */
  public static Date parseDate(String str) {
    if (str == null || ("").equals(str))
      return null;
    Date d = null;

    DateFormat dateFormat = new SimpleDateFormat(IVConstants.DATE_FORMAT_YYYY_MM_DD);
    DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    try {
      d = timestampFormat.parse(str);
    } catch (Exception ex) {
      logger.error(ex.getStackTrace(),ex);
      try {
        d = dateFormat.parse(str);
      } catch (Exception ex1) {
        logger.error(ex.getStackTrace(),ex1);
        throw new YFCException(ex1.getMessage());
      }
    }
    return d;
  }



  /**
   * Return the localized numeric value as a string.
   * 
   * @param pattern required pattern
   * @param value value
   * @param loc locale
   * @return formated string
   */
  public static String getLocalizedStringFormat(String pattern, double value, Locale loc,
      boolean truncateDecimal) {
    NumberFormat nf = NumberFormat.getNumberInstance(loc);
    DecimalFormat df = (DecimalFormat) nf;
    df.applyPattern(pattern);
    if (!truncateDecimal) {
      df.setMinimumFractionDigits(2);
    }

    return df.format(value);
  }

  /**
   * Method to return attribute value by XPath
   * 
   * @see Need to call like --> XMLUtil.getAttributeFromXPath(Document Name, XPath/@AttributeName")
   * @param xpathExpr xpath
   * @return Attribute Value
   * @throws Exception Exception
   */
  public static String getAttributeFromXPath(Object inNode, String xpathExpr) {
    Node node = null;
    CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
    try {
      node = aCachedXPathAPI.selectSingleNode((Node) inNode, xpathExpr);
    } catch (TransformerException ex) {
      logger.error(ex.getStackTrace(),ex);
      throw new YFCException(ex.getMessage());

    }

    if (node == null)
      return null;
    else
      return node.getNodeValue();
  }

  /**
   * Method to return attribute value by XPath of root element. *
   * 
   * @param document input document
   * @param xpathExpr xpath of the required root element
   * @return element name
   * @throws Exception Exception
   */
  public static String getAttributeOfRootElementFromXPath(Document document, String xpathExpr) {
    if (document != null) {
      Element rootElement = IVXMLUtil.getRootElement(document);
      return getAttributeFromXPath(rootElement, xpathExpr);
    }

    return null;
  }


  /**
   * This is a utility method for fetching node value for given tag.
   * 
   * @param pObjOrderLineElement Element on which searching is to be done
   * @param pObjNode Name of node, which attribute value is to be searched
   * @param pObjAttributeName Name of attribute which value is being searched
   * @return node value
   */
  public static String getNodeValue(Element pObjOrderLineElement, String pObjNode,
      String pObjAttributeName) {
    logger.info("-- Entered into method getNodeValue --------");
    String lObjNodeValue = null;

    NodeList lObjNodeList = pObjOrderLineElement.getElementsByTagName(pObjNode);

    if (lObjNodeList != null) {
      for (int i = 0; i < lObjNodeList.getLength(); i++) {
        Node lObjNode = lObjNodeList.item(i);
        Node lObjNodeAttr = lObjNode.getAttributes().getNamedItem(pObjAttributeName);
        if (lObjNodeAttr != null) {
          lObjNodeValue = lObjNodeAttr.getNodeValue();
        }
      }
    }

    return lObjNodeValue;
  }


  



  public class XPathWrapper {
    XPathContext ctx = null;
    int dtm = -1;
    PrefixResolverDefault resolver = null;

    XPathWrapper(Node node) {
      ctx = new XPathContext();
      if (node.getNodeType() == Node.DOCUMENT_NODE) {
        Document doc = (Document) node;
        dtm = ctx.getDTMHandleFromNode(doc.getDocumentElement());
        resolver = new PrefixResolverDefault(doc.getDocumentElement());
      } else {
        dtm = ctx.getDTMHandleFromNode(node);
        resolver = new PrefixResolverDefault(node);
      }
    }



    /**
     * Returns the XML NodeList from the Input XPath String passed. <br>
     * <br>
     * <b> To get the nodelist from XPath </b>
     * <code><pre>  NodeList nl =xpathwrapper.getNodeList(xpathExpr);
     * </pre></code>
     * 
     * @param xpathExpr Xpath string
     * @return Nodelist the nodelist inthe xpath String
     * @throws TransformerException
     * @throws Exception Exception
     */
    public synchronized NodeList getNodeList(String xpathExpr) throws TransformerException {

      XPath path = (XPath) pathCache.get(xpathExpr);
      if (path == null) {
        path = new XPath(xpathExpr, null, null, XPath.SELECT);
        pathCache.put(xpathExpr, path);
      }
      XObject o = path.execute(ctx, dtm, resolver);

      return o.nodelist();

    }

    /**
     * used for sum() and count() xpath expressions. <br>
     * <br>
     * <b> To get the value </b> <code><pre>  double d = xpathwrapper.getDouble(xpathExpr);
     * </pre></code>
     * 
     * @param xpathExpr Xpath string
     * @return double
     * @throws TransformerException
     * @throws Exception Exception
     */
    public synchronized double getDouble(String xpathExpr) throws TransformerException {

      XPath path = (XPath) pathCache.get(xpathExpr);
      if (path == null) {
        path = new XPath(xpathExpr, null, null, XPath.SELECT);
        pathCache.put(xpathExpr, path);
      }
      XObject o = path.execute(ctx, dtm, resolver);
      return parseDouble(o.toString());

    }


    /**
     * Returns the first node from the Input XPath String passed. <br>
     * <br>
     * <b> To get the node from XPath </b>
     * <code><pre>  NodeList n = xpathwrapper.getNode(xpathExpr);
     * </pre></code>
     * 
     * @param xpathExpr Xpath string
     * @return Node the first node inthe xpath String
     * @throws TransformerException
     * @throws Exception Exception
     */
    public synchronized Node getNode(String xpathExpr) throws TransformerException {
      NodeList nl = getNodeList(xpathExpr);
      if (nl.getLength() > 0)
        return nl.item(0);
      else
        return null;
    }


    /**
     * Returns the node value from the Input XPath String passed. <br>
     * <br>
     * <b> To get the node value from XPath </b>
     * <code><pre>  String nv = xpathwrapper.getAttribute(xpathExpr);
     * </pre></code>
     * 
     * @param xpathExpr Xpath string
     * @return String Node value
     * @throws TransformerException
     * @throws Exception Exception
     */
    public synchronized String getAttribute(String xpathExpr) throws TransformerException {
      Node n = getNode(xpathExpr);
      if (n == null)
        return null;
      else
        return n.getNodeValue();
    }


    /**
     * Returns the attirbute value as date from the Input XPath String passed . <br>
     * <br>
     * <b> To get the node value in the form of date from XPath </b>
     * <code><pre>  String nv = xpathwrapper.getDateAttribute(xpathExpr);
     * </pre></code>
     * 
     * @param xpathExpr Xpath string
     * @return date attirbute value in the form of Date
     * @throws TransformerException 
     * @throws Exception Exception
     */
    public synchronized Date getDateAttribute(String xpathExpr) throws TransformerException  {
      String str = getAttribute(xpathExpr);
      return parseDate(str);
    }




    /**
     * Returns the attribute value as integer from the Input XPath String passed . <br>
     * <br>
     * <b> To get the attirbute value in the form of integer from XPath </b>
     * <code><pre>  String nv = xpathwrapper.getIntAttribute(xpathExpr);
     * </pre></code>
     * 
     * @param xpathExpr Xpath string
     * @return int attirbute value in Integer form
     * @throws TransformerException
     * @throws Exception Exception
     */
    public synchronized int getIntAttribute(String xpathExpr) throws TransformerException {
      String str = getAttribute(xpathExpr);
      return parseInt(str);
    }


    /**
     * Returns the attribute value in Long type from the Input XPath String passed . <br>
     * <br>
     * <b> To get the attirbute value in Long type from XPath </b>
     * <code><pre>  String nv = xpathwrapper.getLongAttribute(xpathExpr);
     * </pre></code>
     * 
     * @param xpathExpr Xpath string
     * @return long attirbute value in Long type
     * @throws TransformerException
     * @throws Exception Exception
     */
    public synchronized long getLongAttribute(String xpathExpr) throws TransformerException {
      String str = getAttribute(xpathExpr);
      return parseLong(str);
    }


    /**
     * Returns the attribute value in Double type from the Input XPath String passed . <br>
     * <br>
     * <b> To get the attirbute value in Double type from XPath </b>
     * <code><pre>  String nv = xpathwrapper.getDoubleAttribute(xpathExpr);
     * </pre></code>
     * 
     * @param xpathExpr Xpath string
     * @return Double attirbute value in Long type
     * @throws TransformerException
     * @throws Exception Exception
     */
    public synchronized double getDoubleAttribute(String xpathExpr) throws TransformerException {
      String str = getAttribute(xpathExpr);
      return parseDouble(str);
    }


    /**
     * Returns the attribute value as boolean from the Input XPath String passed . <br>
     * <br>
     * <b> To get the attirbute value in boolean type from XPath </b>
     * <code><pre>  String nv = xpathwrapper.getBooleanAttribute(xpathExpr);
     * </pre></code>
     * 
     * @param xpathExpr Xpath string
     * @param defaultValue Default boolen value
     * @return boolean Returns default value if the Attribute is null in the xpathExpr passed. else
     *         returns the boolean value of the Attribute.
     * @throws TransformerException
     * @throws Exception Exception
     */
    public synchronized boolean getBooleanAttribute(String xpathExpr, boolean defaultValue)
        throws TransformerException {
      String str = getAttribute(xpathExpr);
      if (str == null) {
        return defaultValue;
      } else {
        return parseBoolean(str);
      }
    }



    /**
     * Returns the parsed Date of the input String.
     * 
     * @param str date string
     * @return Date parsed date
     * @throws Exception Exception
     */
    public synchronized Date parseDate(String str)  {
      DateFormat dateFormat = new SimpleDateFormat(IVConstants.DATE_FORMAT_YYYY_MM_DD);
      DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      if (str == null)
        return null;
      Date d = null;
      try {
        d = timestampFormat.parse(str);
      } catch (Exception ex) {
        logger.error(ex.getStackTrace(),ex);
        try {
          d = dateFormat.parse(str);
        } catch (Exception ex1) {
          logger.error(ex1.getStackTrace(),ex1);
          throw new YFCException(ex1.getMessage());
        }
      }
      return d;
    }

    /**
     * Returns the parsed Date of the input String. *
     * 
     * @param str date string.
     * @return Date The format is yyyy-MM-dd
     * @throws ParseException
     * @throws Exception Exception
     */
    public synchronized Date parseDateMethod(String str) throws ParseException {
      DateFormat dateFormat = new SimpleDateFormat(IVConstants.DATE_FORMAT_YYYY_MM_DD);     
      if (str == null)
        return null;      
      return dateFormat.parse(str);
     
    }


    /**
     * Returns the parsed Integer of the input String.
     * 
     * @param str --string to be convert as integer.
     * @return Integer
     */
    public synchronized int parseInt(String str) {
      if (str == null)
        return Integer.MIN_VALUE;
      return Integer.parseInt(str);
    }

    /**
     * Returns the parsed Long type of the input String.
     * 
     * @param str -string to be convert as long.
     * @return Long
     */
    public synchronized long parseLong(String str) {
      if (str == null)
        return Long.MIN_VALUE;
      return Long.parseLong(str);
    }

    /**
     * Returns the parsed double type of the input String.
     * 
     * @param str -string to be convert as double.
     * @return double
     */
    public synchronized double parseDouble(String str) {
      if (str == null)
        return Double.MIN_VALUE;
      return Double.parseDouble(str);
    }

    /**
     * Returns the parsed boolean type of the input String.
     * 
     * @param str -string to be parsed.
     * @return boolean True when str='Y' or 'true', else 'false'
     */
    public boolean parseBoolean(String str) {
      return "Y".equals(str) || "true".equalsIgnoreCase(str);
    }
    
   
  }

}
