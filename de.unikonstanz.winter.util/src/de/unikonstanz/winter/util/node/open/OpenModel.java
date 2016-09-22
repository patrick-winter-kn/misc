package de.unikonstanz.winter.util.node.open;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class OpenModel extends NodeModel {

    private OpenConfiguration m_config = new OpenConfiguration();

    /**
     * 
     */
    public OpenModel() {
        super(1,0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(File nodeInternDir, ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // Not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(File nodeInternDir, ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // Not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(NodeSettingsWO settings) {
        m_config.save(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
        OpenConfiguration config = new OpenConfiguration();
        config.loadInModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
        OpenConfiguration config = new OpenConfiguration();
        config.loadInModel(settings);
        m_config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // Not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(BufferedDataTable[] inTables, ExecutionContext exec) throws Exception {
    	int timesOpened = 0;
    	int columnIndex = inTables[0].getSpec().findColumnIndex(m_config.getReplacementColumn());
    	for (DataRow row : inTables[0]) {
    		String argument;
    		if (columnIndex < 0) {
    			argument = row.getKey().getString();
    		} else {
	    		DataCell cell = row.getCell(columnIndex);
	    		if (cell.isMissing()) {
	    			continue;
	    		}
	    		argument = cell.toString();
    		}
    		if (m_config.getUseSizeLimit() && timesOpened >= m_config.getSizeLimit()) {
    			throw new Exception("Exceeded limit of opens");
    		}
    		argument = m_config.getArgument().replaceAll("[$]", argument);
    		String command = null;
    		if (m_config.getUseCustomCommand()) {
    			command = m_config.getCustomCommand();
    		} else if (SystemUtils.IS_OS_WINDOWS){
    			command = "start";
    		} else if (SystemUtils.IS_OS_MAC) {
    			command = "open";
    		} else {
    			command = "xdg-open";
    		}
    		Runtime.getRuntime().exec(new String[]{command, argument});
    		timesOpened++;
    	}
        return new BufferedDataTable[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
    	String column = m_config.getReplacementColumn();
    	if (column != null && inSpecs[0].findColumnIndex(column)<0) {
    		throw new InvalidSettingsException("No valid replacement column selected");
    	}
        return new DataTableSpec[0];
    }

}
