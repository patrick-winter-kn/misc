package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods;

import org.knime.core.node.ExecutionMonitor;

public abstract class AbstractPredictionFusionMethod implements PredictionFusionMethod {
	
	private String m_positiveClass;
	private String m_negativeClass;
	private ExecutionMonitor m_exec;
	
	public AbstractPredictionFusionMethod(final String positiveClass, final String negativeClass, final ExecutionMonitor exec) {
		m_positiveClass = positiveClass;
		m_negativeClass = negativeClass;
		m_exec = exec;
	}
	
	protected String getPositiveClass() {
		return m_positiveClass;
	}
	
	protected String getNegativeClass() {
		return m_negativeClass;
	}
	
	protected ExecutionMonitor getExecutionMonitor() {
		return m_exec;
	}

}
