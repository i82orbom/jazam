package FingerPrinting.description;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import FingerPrinting.computation.matching.DataPoint;

public class MPEG7Description extends XMLFile {

	private Element rootElement;
	private Element audioElement;
	private int timeStampStep;
	private List<DataPoint> data;
	public List<DataPoint> getData() {
		return data;
	}

	public String getTitle() {
		return title;
	}

	private String title;
	
	public void createNewFile(String outputFileName) throws ParserConfigurationException
	{
		super.createNewFile(outputFileName);
		this.rootElement = super.doc.createElement("MPEG7Description");
		super.doc.appendChild(rootElement);
		
		this.audioElement = super.doc.createElement("audio");
		this.rootElement.appendChild(this.audioElement);
	}
	
	public void setAudioInfo(String id, String title){
		this.audioElement.setAttribute("id", id);
		this.audioElement.setAttribute("title", title);	
	}
	
	public void setFingerPrint(String timeStamp, String fingerPrintValue){
		Element fingerPrint = super.doc.createElement("fingerprint");
		fingerPrint.setAttribute("timeStamp", timeStamp);
		fingerPrint.appendChild(super.doc.createTextNode(fingerPrintValue));
		this.audioElement.appendChild(fingerPrint);
	}
	
	public void setFingerPrint(ArrayList<Long> list){
		int currentTimeStamp = 0;
		int iSize = list.size() - 1;
		for (int i = 0; i < iSize; ++i){
			long fingerprint = list.get(i);
			setFingerPrint(new String(""+currentTimeStamp), new String(""+fingerprint));
			currentTimeStamp += this.timeStampStep;
		}
	}

	public void setTimeStampStep(int timeStampStep) {
		this.timeStampStep = timeStampStep;
	}
	
	public void loadFromFile(String inputFileName) throws ParserConfigurationException, SAXException, IOException{
		super.loadFromFile(inputFileName);
		this.data = new ArrayList<DataPoint>();
		NodeList el = this.doc.getElementsByTagName("audio");
		this.title = ((Element)el.item(0)).getAttribute("title");
		
		NodeList nList = this.doc.getElementsByTagName("fingerprint");

		for (int i = 0; i < nList.getLength(); ++i){
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE){
				Element eElement = (Element)nNode;
				DataPoint dp = new DataPoint();
				dp.setTimestamp(Integer.parseInt(eElement.getAttribute("timeStamp")));
				dp.setFingerprint(Long.parseLong(eElement.getTextContent()));
				this.data.add(dp);
			}
		}
	}
}
