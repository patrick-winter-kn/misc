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
package de.unikonstanz.winter.bard.node.experimentgraphparser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.knime.base.data.xml.SvgValue;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.IntValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author Patrick Winter, University of Konstanz
 */
public class BardExperimentGraphParserNodeModel extends NodeModel {

	private BardExperimentGraphParserNodeConfig m_config = new BardExperimentGraphParserNodeConfig();

	/**
	 * Create this nodes model.
	 */
	public BardExperimentGraphParserNodeModel() {
		super(1, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		BufferedDataContainer container = exec.createDataContainer(createSpec());
		int pidIndex = inData[0].getSpec().findColumnIndex(m_config.getPidColumn());
		int svgIndex = inData[0].getSpec().findColumnIndex(m_config.getSvgColumn());
		int i = 0;
		for (DataRow row : inData[0]) {
			exec.checkCanceled();
			exec.setProgress(i/(double)inData[0].size());
			DataCell pidCell = row.getCell(pidIndex);
			DataCell svgCell = row.getCell(svgIndex);
			if (pidCell.isMissing() || svgCell.isMissing()) {
				throw new Exception("Missing value encountered. Missing values are not supported.");
			}
			int pid = ((IntValue)pidCell).getIntValue();
			String svg = ((StringValue)svgCell).getStringValue();
			ExperimentGraphParser parser = new ExperimentGraphParser(pid, container);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(parser);
			// Parses the SVG and fills the container with the found results
			xmlReader.parse(new InputSource(new StringReader(svg)));
			i++;
		}
		container.close();
		return new BufferedDataTable[] { container.getTable() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		int columnIndex = inSpecs[0].findColumnIndex(m_config.getPidColumn());
		if (columnIndex < 0 || !inSpecs[0].getColumnSpec(columnIndex).getType().isCompatible(IntValue.class)) {
			throw new InvalidSettingsException("No valid pid column selected");
		}
		int columnIndex2 = inSpecs[0].findColumnIndex(m_config.getSvgColumn());
		if (columnIndex2 < 0 || !inSpecs[0].getColumnSpec(columnIndex2).getType().isCompatible(SvgValue.class)) {
			throw new InvalidSettingsException("No valid svg column selected");
		}
		return new DataTableSpec[] { createSpec() };
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
		BardExperimentGraphParserNodeConfig config = new BardExperimentGraphParserNodeConfig();
		config.load(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		BardExperimentGraphParserNodeConfig config = new BardExperimentGraphParserNodeConfig();
		config.load(settings);
		m_config = config;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {

	}
	
	private DataTableSpec createSpec() {
		DataColumnSpec[] specs = new DataColumnSpec[3];
		specs[0] = new DataColumnSpecCreator("PID", IntCell.TYPE).createSpec();
		specs[1] = new DataColumnSpecCreator("EID", IntCell.TYPE).createSpec();
		specs[2] = new DataColumnSpecCreator("Successor EID", IntCell.TYPE).createSpec();
		return new DataTableSpecCreator().addColumns(specs).createSpec();
	}

}
