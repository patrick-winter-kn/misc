package de.unikonstanz.winter.crossref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.fluent.Request;
import org.knime.core.data.DataCell;
import org.knime.core.data.MissingCell;
import org.knime.core.data.collection.CollectionCellFactory;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CrossrefUtil {

	/**
	 * Base URL to the bard API.
	 */
	public static final String API_BASE_URL = "http://api.crossref.org";

	/**
	 * Retrieves the document for the given URL and parses it as JSON string.
	 * 
	 * @param url
	 *            The URL to the document
	 * @return JsonNode object for the parsed document
	 * @throws IOException
	 *             If retrieval or parsing failed
	 */
	public static JsonNode getJsonForUrl(final String url) throws IOException {
		String response = Request.Get(url).execute().returnContent().asString();
		JsonNode root = new ObjectMapper().readTree(response);
		if (root.isNull()) {
			throw new IOException("Could not parse returned response as JSON:\n" + response);
		}
		return root;
	}

	/**
	 * Creates a StringCell from the given JsonNode. Will return a MissingCell
	 * if node.isNull() is true.
	 * 
	 * @param node
	 *            The JsonNode
	 * @return The corresponding cell
	 */
	public static DataCell nodeToStringCell(final JsonNode node) {
		if (isNull(node)) {
			return new MissingCell(null);
		} else {
			return new StringCell(node.asText());
		}
	}

	/**
	 * Creates a IntCell from the given JsonNode. Will return a MissingCell if
	 * node.isNull() is true.
	 * 
	 * @param node
	 *            The JsonNode
	 * @return The corresponding cell
	 */
	public static DataCell nodeToIntCell(final JsonNode node) {
		if (isNull(node)) {
			return new MissingCell(null);
		} else {
			return new IntCell(node.asInt());
		}
	}

	/**
	 * Creates a DoubleCell from the given JsonNode. Will return a MissingCell
	 * if node.isNull() is true.
	 * 
	 * @param node
	 *            The JsonNode
	 * @return The corresponding cell
	 */
	public static DataCell nodeToDoubleCell(final JsonNode node) {
		if (isNull(node)) {
			return new MissingCell(null);
		} else {
			return new DoubleCell(node.asDouble());
		}
	}

	/**
	 * Creates a IntListCell from the given JsonNode. Will return a MissingCell
	 * if node.isNull() is true.
	 * 
	 * @param node
	 *            The JsonNode
	 * @return The corresponding cell
	 */
	public static DataCell nodeToIntListCell(final JsonNode node) {
		if (isNull(node)) {
			return new MissingCell(null);
		} else {
			List<DataCell> singleCells = new ArrayList<DataCell>();
			Iterator<JsonNode> iterator = node.iterator();
			while (iterator.hasNext()) {
				JsonNode subnode = iterator.next();
				singleCells.add(nodeToIntCell(subnode));
			}
			return CollectionCellFactory.createListCell(singleCells);
		}
	}

	/**
	 * Creates a StringListCell from the given JsonNode. Will return a MissingCell
	 * if node.isNull() is true.
	 * 
	 * @param node
	 *            The JsonNode
	 * @return The corresponding cell
	 */
	public static DataCell nodeToStringListCell(final JsonNode node) {
		if (isNull(node)) {
			return new MissingCell(null);
		} else {
			List<DataCell> singleCells = new ArrayList<DataCell>();
			Iterator<JsonNode> iterator = node.iterator();
			while (iterator.hasNext()) {
				JsonNode subnode = iterator.next();
				singleCells.add(nodeToStringCell(subnode));
			}
			return CollectionCellFactory.createListCell(singleCells);
		}
	}

	/**
	 * Creates an array of n MissingCells.
	 * 
	 * @param n
	 *            The size of the array
	 * @param message
	 *            Error message for the MissingCells
	 * @return The array of MissingCells
	 */
	public static DataCell[] createMissingCells(final int n, final String message) {
		DataCell[] cells = new DataCell[n];
		for (int i = 0; i < cells.length; i++) {
			cells[i] = new MissingCell(message);
		}
		return cells;
	}
	
	/**
	 * Checks if the given node is either a null pointer or a null node.
	 * 
	 * @param node The node to check
	 * @return True if node == null or node.isNull(), false otherwise
	 */
	public static boolean isNull(final JsonNode node) {
		return node == null || node.isNull();
	}

}
