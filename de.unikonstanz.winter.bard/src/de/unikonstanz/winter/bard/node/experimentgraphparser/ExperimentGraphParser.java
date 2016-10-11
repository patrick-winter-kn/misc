package de.unikonstanz.winter.bard.node.experimentgraphparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.util.Pair;
import org.xml.sax.SAXException;

import de.unikonstanz.winter.bard.util.SimpleSaxParser;

public class ExperimentGraphParser extends SimpleSaxParser {
	
	private final int m_pid;
	
	private final BufferedDataContainer m_container;
	
	private final Map<Integer, Integer> m_idToEid = new HashMap<Integer, Integer>();
	
	private final List<Pair<Integer, Integer>> m_edges = new ArrayList<Pair<Integer, Integer>>();
	
	private Integer m_id = null;
	
	public ExperimentGraphParser(final int pid, final BufferedDataContainer container) {
		m_pid = pid;
		m_container = container;
	}

	@Override
	public void finishedElement(String path, String textContent, Map<String, String> attributes) {
		textContent = textContent.trim();
		if (path.equals("/svg/g/g/title")) {
			if (textContent.matches("[0-9]+")) {
				m_id = Integer.parseInt(textContent);
			} else if (textContent.matches("[0-9]+->[0-9]+")) {
				String[] edge = textContent.split("->");
				m_edges.add(new Pair<Integer, Integer>(Integer.parseInt(edge[0]), Integer.parseInt(edge[1])));
			}
		} else if (path.equals("/svg/g/g/text") && m_id != null && textContent.matches("[0-9]+")) {
			m_idToEid.put(m_id, Integer.parseInt(textContent));
			m_id = null;
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		for (Pair<Integer, Integer> edge : m_edges) {
			DataCell[] cells = new DataCell[3];
			cells[0] = new IntCell(m_pid);
			cells[1] = new IntCell(m_idToEid.get(edge.getFirst()));
			cells[2] = new IntCell(m_idToEid.get(edge.getSecond()));
			String rowKey = "Row" + m_container.size();
			m_container.addRowToTable(new DefaultRow(rowKey, cells));
		}
	}

}
