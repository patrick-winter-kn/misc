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
package de.unikonstanz.winter.predictionfusion.node.predictionfusion;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.MissingCell;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.Prediction;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionMethod;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionMethodFactory;
import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionUtil;

/**
 * @author Patrick Winter, University of Konstanz
 */
public class PredictionFusionNodeModel extends NodeModel {

	private PredictionFusionNodeConfig m_config = new PredictionFusionNodeConfig();

	public PredictionFusionNodeModel() {
		super(1, 1);
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData,
			ExecutionContext exec) throws Exception {
		List<List<Prediction>> predictions = PredictionFusionUtil.extractPredictions(inData[0], Arrays.asList(m_config.getPredictionColumns()), Arrays.asList(m_config.getPredictionConfidenceColumns()), m_config.getPositiveClass(), exec.createSubProgress(0.5));
		Set<String> possibleValues = getPossibleValues(inData[0].getDataTableSpec());
		String positiveClass = m_config.getPositiveClass();
		possibleValues.remove(positiveClass);
		String negativeClass = possibleValues.iterator().next();
		PredictionFusionMethod method = PredictionFusionMethodFactory.getPredictionFusionMethod(m_config.getMethod(), positiveClass, negativeClass, exec.createSubProgress(0.5));
		List<Prediction> fusedPredictions = method.fusePredictions(predictions);
		ColumnRearranger rearranger = createColumnRearranger(inData[0].getDataTableSpec(), fusedPredictions, exec.createSubProgress(0.5));
		BufferedDataTable outTable = exec.createColumnRearrangeTable(inData[0], rearranger, exec);
		return new BufferedDataTable[] {outTable};
	}

    protected ColumnRearranger createColumnRearranger(final DataTableSpec inSpecs, final List<Prediction> fusedPredictions, final ExecutionMonitor execMonitor) throws InvalidSettingsException {
    	final AtomicInteger index = new AtomicInteger(0);
        ColumnRearranger rearranger = new ColumnRearranger(inSpecs);
    	CellFactory cellFactory = new CellFactory() {
			public void setProgress(int curRowNr, int rowCount, RowKey lastKey,
					ExecutionMonitor exec) {
				execMonitor.setProgress(curRowNr/(double)rowCount);
			}
			public DataColumnSpec[] getColumnSpecs() {
				DataColumnSpec[] columnSpecs = new DataColumnSpec[3];
				columnSpecs[0] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpecs, "Prediction (" + m_config.getMethod() + ")"), StringCell.TYPE).createSpec();
				columnSpecs[1] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpecs, "Prediction Confidence (" + m_config.getMethod() + ")"), DoubleCell.TYPE).createSpec();
				columnSpecs[2] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpecs, "Rank (" + m_config.getMethod() + ")"), IntCell.TYPE).createSpec();
				return columnSpecs;
			}
			public DataCell[] getCells(DataRow row) {
				Prediction prediction = fusedPredictions.get(index.get());
				index.set(index.get()+1);
				DataCell[] cells = new DataCell[3];
				cells[0] = prediction.getPrediction() == null ? new MissingCell(null) : new StringCell(prediction.getPrediction());
				cells[1] = prediction.getConfidence() == null ? new MissingCell(null) : new DoubleCell(prediction.getConfidence());
				cells[2] = prediction.getRank() == null ? new MissingCell(null) : new IntCell((int)Math.floor(prediction.getRank()));
				return cells;
			}
		};
    	rearranger.append(cellFactory);
        return rearranger;
    }

	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		DataTableSpec specs = inSpecs[0];
		if (!Arrays.asList(PredictionFusionMethodFactory.getAvailablePredictionFusionMethods()).contains(m_config.getMethod())) {
			throw new InvalidSettingsException("No valid method selected.");
		}
		if (m_config.getPositiveClass() == null) {
			throw new InvalidSettingsException("No positive class selected.");
		}
		Set<String> possibleValues = getPossibleValues(inSpecs[0]);
		if (possibleValues.size() != 2) {
			throw new InvalidSettingsException("The combined domain of all selected prediction columns contains " + possibleValues.size() + " classes. This node only supports binary classification problems.");
		}
		if (!possibleValues.contains(m_config.getPositiveClass())) {
			throw new InvalidSettingsException("The combined domain of all selected prediction columns does not contain the selected positive class " + m_config.getPositiveClass());
		}
		String[] predictionColumns = m_config.getPredictionColumns();
		String[] predictionConfidenceColumns = m_config.getPredictionConfidenceColumns();
		for (int i = 0; i < predictionColumns.length; i++) {
			int predictionIndex = specs.findColumnIndex(predictionColumns[i]);
			if (predictionIndex < 0) {
				throw new InvalidSettingsException("The second prediction column \"" + predictionColumns[i] + "\" was not found.");
			}
			if (!specs.getColumnSpec(predictionIndex).getType().isCompatible(StringValue.class)) {
				throw new InvalidSettingsException("The second prediction column \"" + predictionColumns[i] + "\" is not compatible with the type String.");
			}
			int predictionConfidenceIndex = specs.findColumnIndex(predictionConfidenceColumns[i]);
			if (predictionConfidenceIndex < 0) {
				throw new InvalidSettingsException("The first prediction confidence column \"" + predictionConfidenceColumns[i] + "\" was not found.");
			}
			if (!specs.getColumnSpec(predictionConfidenceIndex).getType().isCompatible(DoubleValue.class)) {
				throw new InvalidSettingsException("The first prediction confidence column \"" + predictionConfidenceColumns[i] + "\" is not compatible with the type Double.");
			}
		}
        ColumnRearranger rearranger = createColumnRearranger(inSpecs[0], null, null);
		return new DataTableSpec[] { rearranger.createSpec() };
	}
	
	private Set<String> getPossibleValues(final DataTableSpec spec) {
		String[] predictionColumns = m_config.getPredictionColumns();
		Set<String> possibleValues = new TreeSet<String>();
		for (int i = 0; i < predictionColumns.length; i++) {
			DataColumnSpec predictionSpec = spec.getColumnSpec(predictionColumns[i]);
			if (predictionSpec != null) {
				Set<DataCell> values = predictionSpec.getDomain().getValues();
				if (values != null) {
					for (DataCell value : values) {
						possibleValues.add(value.toString());
					}
				}
			}
		}
		return possibleValues;
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
		PredictionFusionNodeConfig config = new PredictionFusionNodeConfig();
		config.load(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		PredictionFusionNodeConfig config = new PredictionFusionNodeConfig();
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
