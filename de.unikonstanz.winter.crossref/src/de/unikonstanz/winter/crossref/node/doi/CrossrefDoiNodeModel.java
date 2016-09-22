/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   Sep 25, 2014 (Patrick Winter): created
 */
package de.unikonstanz.winter.crossref.node.doi;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.IntValue;
import org.knime.core.data.MissingCell;
import org.knime.core.data.StringValue;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.container.AbstractCellFactory;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.databind.JsonNode;

import de.unikonstanz.winter.crossref.CrossrefUtil;

/**
 * @author Patrick Winter, University of Konstanz
 */
public class CrossrefDoiNodeModel extends NodeModel {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(CrossrefDoiNodeModel.class);

	private CrossrefDoiNodeConfig m_config = new CrossrefDoiNodeConfig();

	/**
	 * Create this nodes model.
	 */
	public CrossrefDoiNodeModel() {
		super(1, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		ColumnRearranger rearranger = createColumnRearranger(inData[0].getDataTableSpec());
		BufferedDataTable out = exec.createColumnRearrangeTable(inData[0], rearranger, exec);
		return new BufferedDataTable[] { out };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		int columnIndex = inSpecs[0].findColumnIndex(m_config.getDoiColumn());
		if (columnIndex < 0 || !inSpecs[0].getColumnSpec(columnIndex).getType().isCompatible(StringValue.class)) {
			throw new InvalidSettingsException("No valid doi column selected");
		}
		return new DataTableSpec[] { createColumnRearranger(inSpecs[0]).createSpec() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_config.save(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		CrossrefDoiNodeConfig config = new CrossrefDoiNodeConfig();
		config.load(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		CrossrefDoiNodeConfig config = new CrossrefDoiNodeConfig();
		config.load(settings);
		m_config = config;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {

	}

	/**
	 * Creates the column rearranger that adds the additional columns to the
	 * input table.
	 * 
	 * @param inSpec
	 *            Spec of the input table
	 * @return Column rearranger that adds the additional columns
	 * @throws InvalidSettingsException
	 *             If the configured doi column could not be found in the input
	 *             spec
	 */
	private ColumnRearranger createColumnRearranger(final DataTableSpec inSpec) throws InvalidSettingsException {
		final int columnIndex = inSpec.findColumnIndex(m_config.getDoiColumn());
		if (columnIndex < 0) {
			throw new InvalidSettingsException("No valid doi column selected");
		}
		ColumnRearranger rearranger = new ColumnRearranger(inSpec);
		DataColumnSpec[] newColSpecs = new DataColumnSpec[37];
		newColSpecs[0] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Publisher"),
				StringCell.TYPE).createSpec();
		newColSpecs[1] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Title"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[2] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Reference Count"),
				IntCell.TYPE).createSpec();
		newColSpecs[3] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Source"), StringCell.TYPE)
				.createSpec();
		newColSpecs[4] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Prefix"), StringCell.TYPE)
				.createSpec();
		newColSpecs[5] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Member"), StringCell.TYPE)
				.createSpec();
		newColSpecs[6] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Type"), StringCell.TYPE)
				.createSpec();
		newColSpecs[7] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Created"),
				DateAndTimeCell.TYPE).createSpec();
		newColSpecs[8] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Deposited"),
				DateAndTimeCell.TYPE).createSpec();
		newColSpecs[9] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Indexed"),
				DateAndTimeCell.TYPE).createSpec();
		newColSpecs[10] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Issued"),
				ListCell.getCollectionType(IntCell.TYPE)).createSpec();
		newColSpecs[11] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Subtitle"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[12] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Container Title"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[13] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Issue"), StringCell.TYPE)
				.createSpec();
		newColSpecs[14] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Volume"),
				StringCell.TYPE).createSpec();
		newColSpecs[15] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Page"), StringCell.TYPE)
				.createSpec();
		newColSpecs[16] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Article Number"),
				StringCell.TYPE).createSpec();
		newColSpecs[17] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Published Print"),
				ListCell.getCollectionType(IntCell.TYPE)).createSpec();
		newColSpecs[18] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Published Online"),
				ListCell.getCollectionType(IntCell.TYPE)).createSpec();
		newColSpecs[19] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Subject"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[20] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "ISSN"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[21] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "ISBN"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[22] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Archive"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[23] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "License"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[24] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Funder"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[25] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Assertion"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[26] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Author"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[27] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Editor"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[28] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Chair"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[29] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Translator"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[30] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Update To"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[31] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Update Policy"),
				StringCell.TYPE).createSpec();
		newColSpecs[32] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Link"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[33] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Clinical Trial Number"),
				ListCell.getCollectionType(StringCell.TYPE)).createSpec();
		newColSpecs[34] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Alternative ID"),
				StringCell.TYPE).createSpec();
		newColSpecs[35] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "Score"), DoubleCell.TYPE)
				.createSpec();
		newColSpecs[36] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, "URL"), StringCell.TYPE)
				.createSpec();
		CellFactory factory = new AbstractCellFactory(newColSpecs) {
			@Override
			public DataCell[] getCells(DataRow row) {
				DataCell doiCell = row.getCell(columnIndex);
				return informationForDoi(doiCell);
			}
		};
		rearranger.append(factory);
		return rearranger;
	}

	/**
	 * Creates a cell array containing all retrieved information for the given
	 * DOI.
	 * 
	 * @param doiCell
	 *            Cell containing the DOI
	 * @return Cells containing the retrieved information or missing cells if
	 *         the doiCell is invalid
	 */
	private static DataCell[] informationForDoi(final DataCell doiCell) {
		if (doiCell.isMissing() || !doiCell.getType().isCompatible(StringValue.class)) {
			return CrossrefUtil.createMissingCells(37, null);
		}
		String doi = ((StringValue) doiCell).getStringValue();
		String url = CrossrefUtil.API_BASE_URL + "/works/" + doi;
		JsonNode root = null;
		try {
			root = CrossrefUtil.getJsonForUrl(url);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			return CrossrefUtil.createMissingCells(37, e.getMessage());
		}
		DataCell[] cells = new DataCell[37];
		JsonNode message = root.get("message");
		cells[0] = CrossrefUtil.nodeToStringCell(message.get("publisher"));
		cells[1] = CrossrefUtil.nodeToStringListCell(message.get("title"));
		cells[2] = CrossrefUtil.nodeToIntCell(message.get("reference-count"));
		cells[3] = CrossrefUtil.nodeToStringCell(message.get("source"));
		cells[4] = CrossrefUtil.nodeToStringCell(message.get("prefix"));
		cells[5] = CrossrefUtil.nodeToStringCell(message.get("member"));
		cells[6] = CrossrefUtil.nodeToStringCell(message.get("type"));
		cells[7] = nodeToDateCell(message.get("created"));
		cells[8] = nodeToDateCell(message.get("deposited"));
		cells[9] = nodeToDateCell(message.get("indexed"));
		cells[10] = partialDateNodeToIntListCell(message.get("issued"));
		cells[11] = CrossrefUtil.nodeToStringListCell(message.get("subtitle"));
		cells[12] = CrossrefUtil.nodeToStringListCell(message.get("container-title"));
		cells[13] = CrossrefUtil.nodeToStringCell(message.get("issue"));
		cells[14] = CrossrefUtil.nodeToStringCell(message.get("volume"));
		cells[15] = CrossrefUtil.nodeToStringCell(message.get("page"));
		cells[16] = CrossrefUtil.nodeToStringCell(message.get("article-number"));
		cells[17] = partialDateNodeToIntListCell(message.get("published-print"));
		cells[18] = partialDateNodeToIntListCell(message.get("published-online"));
		cells[19] = CrossrefUtil.nodeToStringListCell(message.get("subject"));
		cells[20] = CrossrefUtil.nodeToStringListCell(message.get("ISSN"));
		cells[21] = CrossrefUtil.nodeToStringListCell(message.get("ISBN"));
		cells[22] = CrossrefUtil.nodeToStringListCell(message.get("archive"));
		cells[23] = toStringListCell(message, "license"); // TODO
		cells[24] = toStringListCell(message, "funder"); // TODO
		cells[25] = toStringListCell(message, "assertion"); // TODO
		cells[26] = toStringListCell(message, "author"); // TODO
		cells[27] = toStringListCell(message, "editor"); // TODO
		cells[28] = toStringListCell(message, "chair"); // TODO
		cells[29] = toStringListCell(message, "translator"); // TODO
		cells[30] = toStringListCell(message, "update-to"); // TODO
		cells[31] = CrossrefUtil.nodeToStringCell(message.get("update-policy"));
		cells[32] = toStringListCell(message, "link"); // TODO
		cells[33] = toStringListCell(message, "clinical-trail-number"); // TODO
		cells[34] = CrossrefUtil.nodeToStringCell(message.get("alternative-id"));
		cells[35] = CrossrefUtil.nodeToDoubleCell(message.get("score"));
		cells[36] = CrossrefUtil.nodeToStringCell(message.get("URL"));
		return cells;
	}

	private static DataCell nodeToDateCell(final JsonNode node) {
		if (CrossrefUtil.isNull(node)) {
			return new MissingCell(null);
		}
		JsonNode timestamp = node.get("timestamp");
		if (CrossrefUtil.isNull(timestamp)) {
			return new MissingCell(null);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.asLong());
		return new DateAndTimeCell(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
	}
	
	private static DataCell partialDateNodeToIntListCell(final JsonNode node) {
		if (CrossrefUtil.isNull(node)) {
			return new MissingCell(null);
		}
		JsonNode dateParts = node.get("date-parts");
		if (CrossrefUtil.isNull(dateParts)) {
			return new MissingCell(null);
		}
		return CrossrefUtil.nodeToIntListCell(dateParts.iterator().next());
	}
	
	private static DataCell toStringListCell(final JsonNode node, final String field) {
		// TODO
		return new MissingCell(null);
	}

}
