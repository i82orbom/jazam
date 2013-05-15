package FingerPrinting.description;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLFile {

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	protected Document doc;

	private String _inputFileName;
	private String _outputFileName;
	
	public XMLFile (){
	}
	
	public void setOutputFileName(String output){
		this._outputFileName = output;
	}
	public void createNewFile(String outputFileName) throws ParserConfigurationException{
		this._outputFileName = outputFileName;
		
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docBuilder = this.docFactory.newDocumentBuilder();
		this.doc = this.docBuilder.newDocument();
	}
	
	public void write() throws TransformerException{
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		DOMSource source = new DOMSource(this.doc);
		StreamResult result = new StreamResult(new File(this._outputFileName));
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, result);
	}
	
	public void loadFromFile(String inputFileName) throws ParserConfigurationException, SAXException, IOException{
		this._inputFileName = inputFileName;
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docBuilder = this.docFactory.newDocumentBuilder();
		this.doc = this.docBuilder.parse(new File(this._inputFileName));
		this.doc.getDocumentElement().normalize();
	}
}
