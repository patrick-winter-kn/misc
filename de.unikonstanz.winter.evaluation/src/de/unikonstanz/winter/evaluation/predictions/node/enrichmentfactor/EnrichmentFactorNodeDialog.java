package de.unikonstanz.winter.evaluation.predictions.node.enrichmentfactor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;

/**
 * @author Patrick Winter, University of Konstanz
 */
public class EnrichmentFactorNodeDialog extends NodeDialogPane {

	DataTableSpec m_spec;
	@SuppressWarnings("unchecked")
	private ColumnSelectionComboxBox m_classColumn = new ColumnSelectionComboxBox((Border) null, StringValue.class);
	@SuppressWarnings("unchecked")
	private ColumnSelectionComboxBox m_sortColumn = new ColumnSelectionComboxBox((Border) null, DoubleValue.class);
	private JComboBox<String> m_positiveClass = new JComboBox<String>();
	private JCheckBox m_descending = new JCheckBox("Descending");
	private JSpinner m_fractionsSteps = new JSpinner(new SpinnerNumberModel(100, 1, Integer.MAX_VALUE, 1));

	public EnrichmentFactorNodeDialog() {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePossibleValues();
			}
		};
		m_classColumn.addActionListener(listener);
		m_sortColumn.addActionListener(listener);
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(new JLabel("Class column"), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		panel.add(m_classColumn, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0;
		panel.add(new JLabel("Sort column"), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		panel.add(m_sortColumn, gbc);
		gbc.gridy++;
		panel.add(m_descending, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0;
		panel.add(new JLabel("Positive class"), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		panel.add(m_positiveClass, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0;
		panel.add(new JLabel("Number of fractions"), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.gridwidth = 1;
		panel.add(m_fractionsSteps, gbc);
		addTab("Config", panel);
	}

	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings)
			throws InvalidSettingsException {
		EnrichmentFactorNodeConfig config = new EnrichmentFactorNodeConfig();
		config.setClassColumn(m_classColumn.getSelectedColumn());
		config.setSortColumn(m_sortColumn.getSelectedColumn());
		config.setPositiveClass((String) m_positiveClass.getSelectedItem());
		config.setDescending(m_descending.isSelected());
		config.setNumberOfFractions((int)m_fractionsSteps.getValue());
		config.save(settings);
	}

	@Override
	protected void loadSettingsFrom(final NodeSettingsRO settings,
			final DataTableSpec[] specs) throws NotConfigurableException {
		m_spec = specs[0];
		EnrichmentFactorNodeConfig config = new EnrichmentFactorNodeConfig();
		config.loadInDialog(settings);
		m_classColumn.update(specs[0], config.getClassColumn());
		m_sortColumn.update(specs[0], config.getSortColumn());
		m_positiveClass.setSelectedItem(config.getPositiveClass());
		m_descending.setSelected(config.getDescending());
		m_fractionsSteps.setValue(config.getNumberOfFractions());
	}
	
	private void updatePossibleValues() {
		if (m_spec != null) {
			String selection = (String) m_positiveClass.getSelectedItem();
			Set<String> possibleValues = new TreeSet<String>();
			DataColumnSpec classSpec = m_spec.getColumnSpec(m_classColumn.getSelectedColumn());
			if (classSpec != null) {
				Set<DataCell> values = classSpec.getDomain().getValues();
				if (values != null) {
					for (DataCell value : values) {
						possibleValues.add(value.toString());
					}
				}
			}
			m_positiveClass.setModel(new DefaultComboBoxModel<String>(possibleValues.toArray(new String[possibleValues.size()])));
			if (selection != null && possibleValues.contains(selection)) {
				m_positiveClass.setSelectedItem(selection);
			} else if (!possibleValues.isEmpty()) {
				m_positiveClass.setSelectedIndex(0);
			}
		}
	}

}
