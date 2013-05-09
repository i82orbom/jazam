package FingerPrinting.description;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

public class MPEG7Description extends XMLFile {

	private Element rootElement;
	private Element audioElement;
	
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
	
	public void setFingerPrint(ArrayList<ArrayList<Long>> list){
		int iSize = list.size() - 1;
		for (int i = 0; i < iSize; ++i){
			ArrayList<Long> value = list.get(i);
			int iValueSize = value.size();
			StringBuffer dump = new StringBuffer();
			/** Write first, it's supposed at least we have one */
			dump.append(value.get(0));
			for (int j = 1; j < iValueSize; ++j){
				dump.append(","+value.get(j).longValue());
			}
			setFingerPrint(new String(""+i), dump.toString());
		}
	}
}
