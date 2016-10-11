package de.unikonstanz.winter.bard.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SimpleSaxParser extends DefaultHandler {

	private String m_elementPath = "";
	private Stack<Map<String, String>> m_attributes = new Stack<Map<String, String>>();
	private Stack<StringBuilder> m_textContent = new Stack<StringBuilder>();

	public abstract void finishedElement(final String path,
			final String textContent, final Map<String, String> attributes);

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		m_elementPath += "/" + qName;
		m_textContent.push(new StringBuilder());
		m_attributes.push(new HashMap<String, String>());
		for (int i = 0; i < attributes.getLength(); i++) {
			m_attributes.peek().put(attributes.getLocalName(i),
					attributes.getValue(i));
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		m_textContent.peek().append(new String(ch, start, length));
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		finishedElement(m_elementPath, m_textContent.pop().toString().trim(),
				m_attributes.pop());
		m_elementPath = m_elementPath.substring(0,
				m_elementPath.lastIndexOf("/"));
	}

}
