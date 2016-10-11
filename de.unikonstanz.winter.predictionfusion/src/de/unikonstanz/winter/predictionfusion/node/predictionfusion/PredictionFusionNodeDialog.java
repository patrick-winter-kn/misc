package de.unikonstanz.winter.predictionfusion.node.predictionfusion;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

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

import de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods.PredictionFusionMethodFactory;

/**
 * @author Patrick Winter, University of Konstanz
 */
public class PredictionFusionNodeDialog extends NodeDialogPane {

	private DataTableSpec m_spec;
	private JComboBox<String> m_method = new JComboBox<String>();
	private JComboBox<String> m_positiveClass = new JComboBox<String>();
	private List<ColumnSelectionComboxBox> m_predictionColumns = new ArrayList<ColumnSelectionComboxBox>();
	private List<ColumnSelectionComboxBox> m_predictionConfidenceColumns = new ArrayList<ColumnSelectionComboxBox>();
	private List<JButton> m_removes = new ArrayList<JButton>();
	private JButton m_add = new JButton("Add prediction");
	private JPanel m_columnSelectionPanel = new JPanel(new GridBagLayout());

	public PredictionFusionNodeDialog() {
		m_columnSelectionPanel.setBackground(Color.WHITE);
		m_columnSelectionPanel.setBorder(new LineBorder(Color.BLACK));
		m_method.setModel(new DefaultComboBoxModel<String>(PredictionFusionMethodFactory.getAvailablePredictionFusionMethods()));
		m_add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRow();
			}
		});
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(new JLabel("Method"), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		panel.add(m_method, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0;
		panel.add(new JLabel("Positive class"), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		panel.add(m_positiveClass, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 0, 0);
		panel.add(m_columnSelectionPanel, gbc);
		addTab("Config", panel);
	}

	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings)
			throws InvalidSettingsException {
		PredictionFusionNodeConfig config = new PredictionFusionNodeConfig();
		config.setMethod((String) m_method.getSelectedItem());
		config.setPositiveClass((String) m_positiveClass.getSelectedItem());
		String[] predictionColumns = new String[m_predictionColumns.size()];
		String[] predictionConfidenceColumns = new String[m_predictionConfidenceColumns.size()];
		for (int i = 0; i < m_predictionColumns.size(); i++) {
			predictionColumns[i] = m_predictionColumns.get(i).getSelectedColumn();
			predictionConfidenceColumns[i] = m_predictionConfidenceColumns.get(i).getSelectedColumn();
		}
		config.setPredictionColumns(predictionColumns);
		config.setPredictionConfidenceColumns(predictionConfidenceColumns);
		config.save(settings);
	}

	@Override
	protected void loadSettingsFrom(final NodeSettingsRO settings,
			final DataTableSpec[] specs) throws NotConfigurableException {
		m_spec = specs[0];
		PredictionFusionNodeConfig config = new PredictionFusionNodeConfig();
		config.loadWithDefaults(settings);
		m_method.setSelectedItem(config.getMethod());
		m_predictionColumns.clear();
		m_predictionConfidenceColumns.clear();
		m_removes.clear();
		String[] predictionColumns = config.getPredictionColumns();
		String[] predictionConfidenceColumns = config.getPredictionConfidenceColumns();
		for (int i = 0; i < predictionColumns.length; i++) {
			addRow();
			m_predictionColumns.get(i).setSelectedColumn(predictionColumns[i]);
			m_predictionConfidenceColumns.get(i).setSelectedColumn(predictionConfidenceColumns[i]);
		}
		m_positiveClass.setSelectedItem(config.getPositiveClass());
		updateColumnSelectionPanel();
		updatePossibleValues();
	}
	
	private synchronized void updatePossibleValues() {
		if (m_spec != null) {
			String selection = (String) m_positiveClass.getSelectedItem();
			Set<String> possibleValues = new TreeSet<String>();
			for (int i = 0; i < m_predictionColumns.size(); i++) {
				DataColumnSpec predictionSpec = m_spec.getColumnSpec(m_predictionColumns.get(i).getSelectedColumn());
				if (predictionSpec != null) {
					Set<DataCell> values = predictionSpec.getDomain().getValues();
					if (values != null) {
						for (DataCell value : values) {
							possibleValues.add(value.toString());
						}
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
	
	private void updateColumnSelectionPanel() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		m_columnSelectionPanel.removeAll();
		gbc.weightx = 0.5;
		m_columnSelectionPanel.add(new JLabel("Prediction"), gbc);
		gbc.gridx++;
		gbc.gridwidth = 2;
		m_columnSelectionPanel.add(new JLabel("Prediction confidence"), gbc);
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.gridx = 0;
		for (int i = 0; i < m_predictionColumns.size(); i++) {
			m_columnSelectionPanel.add(m_predictionColumns.get(i), gbc);
			gbc.gridx++;
			m_columnSelectionPanel.add(m_predictionConfidenceColumns.get(i), gbc);
			gbc.gridx++;
			gbc.weightx = 0;
			m_columnSelectionPanel.add(m_removes.get(i), gbc);
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.weightx = 0.5;
		}
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		m_columnSelectionPanel.add(m_add, gbc);
		m_columnSelectionPanel.revalidate();
		m_columnSelectionPanel.repaint();
		m_columnSelectionPanel.getParent().revalidate();
		m_columnSelectionPanel.getParent().repaint();
	}
	
	@SuppressWarnings("unchecked")
	private void addRow() {
		ColumnSelectionComboxBox predictionColumn = new ColumnSelectionComboxBox((Border) null, StringValue.class);
		try {
			predictionColumn.update(m_spec, "");
		} catch (NotConfigurableException e) {
			// TODO log error
		}
		predictionColumn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePossibleValues();
			}
		});
		m_predictionColumns.add(predictionColumn);
		ColumnSelectionComboxBox predictionConfidenceColumn = new ColumnSelectionComboxBox((Border) null, DoubleValue.class);
		try {
			predictionConfidenceColumn.update(m_spec, "");
		} catch (NotConfigurableException e) {
			// TODO log error
		}
		m_predictionConfidenceColumns.add(predictionConfidenceColumn);
		final JButton remove = new JButton("✖");
		remove.setSize(remove.getSize().height, remove.getSize().height);
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeRow(m_removes.indexOf(remove));
			}
		});
		m_removes.add(remove);
		updateColumnSelectionPanel();
		updatePossibleValues();
	}
	
	private void removeRow(final int index) {
		m_predictionColumns.remove(index);
		m_predictionConfidenceColumns.remove(index);
		m_removes.remove(index);
		updateColumnSelectionPanel();
		updatePossibleValues();
	}

}
