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
package de.unikonstanz.winter.evaluation.predictions.node.enrichmentfactor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
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

/**
 * @author Patrick Winter, University of Konstanz
 */
public class EnrichmentFactorNodeModel extends NodeModel {

	private EnrichmentFactorNodeConfig m_config = new EnrichmentFactorNodeConfig();

	public EnrichmentFactorNodeModel() {
		super(1, 1);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData,
			ExecutionContext exec) throws Exception {
		if (m_config.getNumberOfFractions() > inData[0].getRowCount()) {
			throw new InvalidSettingsException("The number of fractions must not be higher than the number of rows.");
		}
		DataTableSpec spec = inData[0].getDataTableSpec();
		int classIndex = spec.findColumnIndex(m_config.getClassColumn());
		int sortIndex = spec.findColumnIndex(m_config.getSortColumn());
		BufferedDataContainer container = exec.createDataContainer(createSpec());
		Double[] fractions = m_config.getFractions();
		List<SortItem<Double, Boolean>> sortedPositives = new ArrayList<SortItem<Double, Boolean>>(inData[0].getRowCount());
		int positives = 0;
		for (DataRow row : inData[0]) {
			DataCell classCell = row.getCell(classIndex);
			DataCell sortCell = row.getCell(sortIndex);
			if (classCell.isMissing() || sortCell.isMissing()) {
				throw new Exception("Missing values are not supported.");
			}
			double sortValue = ((DoubleValue)sortCell).getDoubleValue();
			boolean positive = ((StringValue)classCell).getStringValue().equals(m_config.getPositiveClass());
			if (positive) {
				positives++;
			}
			sortedPositives.add(new SortItem<Double, Boolean>(sortValue, positive));
		}
		if (positives == 0) {
			throw new Exception("No positives in input table.");
		}
		if (m_config.getDescending()) {
			Collections.sort(sortedPositives, Collections.reverseOrder());
		} else {
			Collections.sort(sortedPositives);
		}
		for (int i = 0; i < fractions.length; i++) {
			exec.checkCanceled();
			exec.setProgress(i/(double)fractions.length);
			double fraction = fractions[i];
			int elements = sortedPositives.size();
			int elementsInFraction = (int)Math.round(sortedPositives.size() * fraction);
			int positivesInFraction = 0;
			for (int j = 0; j < elementsInFraction; j++) {
				if (sortedPositives.get(j).getValueObject()) {
					positivesInFraction++;
				}
			}
			double enrichmentFactor = (positivesInFraction / (double)elementsInFraction) / (positives / (double)elements);
			DataCell[] cells = new DataCell[6];
			cells[0] = new IntCell(elements);
			cells[1] = new IntCell(positives);
			cells[2] = new DoubleCell(fraction);
			cells[3] = new IntCell(elementsInFraction);
			cells[4] = new IntCell(positivesInFraction);
			cells[5] = new DoubleCell(enrichmentFactor);
			container.addRowToTable(new DefaultRow(m_config.getSortColumn() + " " + fraction, cells));
		}
		container.close();
		return new BufferedDataTable[] { container.getTable() };
	}

	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		DataTableSpec specs = inSpecs[0];
		int classIndex = specs.findColumnIndex(m_config.getClassColumn());
		if (classIndex < 0) {
			throw new InvalidSettingsException("The class column \"" + m_config.getClassColumn() + "\" was not found.");
		}
		if (!specs.getColumnSpec(classIndex).getType().isCompatible(StringValue.class)) {
			throw new InvalidSettingsException("The class column \"" + m_config.getClassColumn() + "\" is not compatible with the type String.");
		}
		int predictionIndex = specs.findColumnIndex(m_config.getSortColumn());
		if (predictionIndex < 0) {
			throw new InvalidSettingsException("The sort column \"" + m_config.getSortColumn() + "\" was not found.");
		}
		if (!specs.getColumnSpec(predictionIndex).getType().isCompatible(DoubleValue.class)) {
			throw new InvalidSettingsException("The sort column \"" + m_config.getSortColumn() + "\" is not compatible with the type Double.");
		}
		if (m_config.getPositiveClass() == null) {
			throw new InvalidSettingsException("No positive class selected.");
		}
		return new DataTableSpec[] { createSpec() };
	}
	
	private DataTableSpec createSpec() {
		DataColumnSpec[] colSpecs = new DataColumnSpec[6];
		colSpecs[0] = new DataColumnSpecCreator("Rows", IntCell.TYPE).createSpec();
		colSpecs[1] = new DataColumnSpecCreator("Positives", IntCell.TYPE).createSpec();
		colSpecs[2] = new DataColumnSpecCreator("Relative Fraction Size", DoubleCell.TYPE).createSpec();
		colSpecs[3] = new DataColumnSpecCreator("Rows in Fraction", IntCell.TYPE).createSpec();
		colSpecs[4] = new DataColumnSpecCreator("Positives in Fraction", IntCell.TYPE).createSpec();
		colSpecs[5] = new DataColumnSpecCreator("Enrichment Factor", DoubleCell.TYPE).createSpec();
		return new DataTableSpec(colSpecs);
	}

	@Override
	protected void loadInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	@Override
	protected void saveInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_config.save(settings);
	}

	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		EnrichmentFactorNodeConfig config = new EnrichmentFactorNodeConfig();
		config.load(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		EnrichmentFactorNodeConfig config = new EnrichmentFactorNodeConfig();
		config.load(settings);
		m_config = config;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {

	}

}
