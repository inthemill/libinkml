package ch.unibe.inkml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InkDefinitions extends HashMap<String,InkUniqueElement> implements InkElementInterface{
	
	/**
	 * What ever this is for
	 */
	private static final long serialVersionUID = -6448563169075416272L;
	private List<InkElement> content = new ArrayList<InkElement>();
	private InkInk ink;
	
	public InkDefinitions(InkInk ink) {
		this.ink = ink;
		ink.setDefinitions(this);
	}
	
	public void put (InkUniqueElement el){
		put(el.getId(),el);
	}
	
	public InkUniqueElement get(String key){
		if(!this.containsKey(key)){
			throw new NullPointerException("Element with key "+key+" is not yet defined in this document");
		}
		return super.get(key);
	}
	
	public void enter(InkElement el){
		this.content.add(el);
	}
	
	public String uniqueId(String prefix) {
		int count = 0;
		while(containsKey(prefix+count)){
			count ++;
		}
		return prefix+count;
	}

	public void buildFromXMLNode(Element node) throws InkMLComplianceException {
		for(Node child = node.getFirstChild(); child!= null; child = child.getNextSibling()){
			if(child.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			String n = child.getNodeName();
			if(n.equals("inkSource")){
				InkInkSource b = new InkInkSource(this.getInk());
				b.buildFromXMLNode((Element)child);
				this.enter(b);
			}
			if(n.equals("brush")){
				InkBrush b = new InkBrush(this.getInk());
				b.buildFromXMLNode((Element)child);
				this.enter(b);
			}
			if(n.equals("canvas")){
				InkCanvas c = new InkCanvas(this.getInk());
				c.buildFromXMLNode((Element)child);
				this.enter(c);
			}
			if(n.equals("canvasTransform")){
				InkCanvasTransform f = new InkCanvasTransform(this.getInk());
				f.buildFromXMLNode((Element)child);
				this.enter(f);
			}
			if(n.equals("traceFormat")){
				InkTraceFormat f = new InkTraceFormat(this.getInk());
				f.buildFromXMLNode((Element)child);
				this.enter(f);
			}
			if(n.equals("mapping")){
				this.enter(InkMapping.mappingFactory(this.getInk(),(Element)child));
			}
		}
	}


	public void exportToInkML(Element parent) throws InkMLComplianceException {
		Element definitionNode = parent.getOwnerDocument().createElement("definitions");
		parent.appendChild(definitionNode);
		for(InkElement i : this.content){
			i.exportToInkML(definitionNode);
		}
	}


	public InkInk getInk() {
		return ink;
	}
}