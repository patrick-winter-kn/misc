package de.unikonstanz.winter.util.node.open;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.DataValueColumnFilter;

public class OpenDialog extends NodeDialogPane {
	
	private JTextField m_argument = new JTextField(40);
	
	@SuppressWarnings("unchecked")
	private ColumnSelectionPanel m_replacementColumn = new ColumnSelectionPanel(null, new DataValueColumnFilter(DataValue.class), false, true);
	
	private JCheckBox m_useCustomCommand = new JCheckBox("Use custom command");
	
	private JTextField m_customCommand = new JTextField(40);
	
	private JCheckBox m_useSizeLimit = new JCheckBox("Limit number of windows to open");
	
	private JSpinner m_sizeLimit = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    /**
     * 
     */
    OpenDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("URL / File:"), gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_argument, gbc);
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Replacement column:"), gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_replacementColumn, gbc);
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(m_useCustomCommand, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_customCommand, gbc);
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(m_useSizeLimit, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_sizeLimit, gbc);
        addTab("Options", panel);
        ActionListener refresher = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		};
		m_useCustomCommand.addActionListener(refresher);
		m_useSizeLimit.addActionListener(refresher);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(NodeSettingsRO settings, DataTableSpec[] specs) throws NotConfigurableException {
        OpenConfiguration config = new OpenConfiguration();
        config.loadInDialog(settings);
        m_argument.setText(config.getArgument());
        m_useCustomCommand.setSelected(config.getUseCustomCommand());
        m_customCommand.setText(config.getCustomCommand());
        m_useSizeLimit.setSelected(config.getUseSizeLimit());
        m_sizeLimit.setValue(config.getSizeLimit());
        m_replacementColumn.update(specs[0], config.getReplacementColumn());
        if (config.getReplacementColumn() == null) {
        	m_replacementColumn.setRowIDSelected();
        }
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
        OpenConfiguration config = new OpenConfiguration();
        config.setArgument(m_argument.getText());
        config.setReplacementColumn(m_replacementColumn.getSelectedColumn());
        config.setUseCustomCommand(m_useCustomCommand.isSelected());
        config.setCustomCommand(m_customCommand.getText());
        config.setUseSizeLimit(m_useSizeLimit.isSelected());
        config.setSizeLimit((int)m_sizeLimit.getValue());
        config.save(settings);
    }
    
    private void refresh() {
    	m_customCommand.setEnabled(m_useCustomCommand.isSelected());
    	m_sizeLimit.setEnabled(m_useSizeLimit.isSelected());
    }

}
