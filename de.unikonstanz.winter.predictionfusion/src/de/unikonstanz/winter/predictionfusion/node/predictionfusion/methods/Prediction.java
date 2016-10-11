package de.unikonstanz.winter.predictionfusion.node.predictionfusion.methods;

public class Prediction implements Comparable<Prediction> {
	
	private int m_index;
	private boolean m_positive;
	private String m_prediction;
	private Double m_confidence;
	private Double m_rank;
	
	public Prediction(final int index, final boolean positive, final String prediction, final Double confidence, final Double rank) {
		m_index = index;
		m_positive = positive;
		m_prediction = prediction;
		m_confidence = confidence;
		m_rank = rank;
	}
	
	public int getIndex() {
		return m_index;
	}
	
	public boolean isPositive() {
		return m_positive;
	}
	
	public boolean hasPrediction() {
		return m_prediction != null;
	}
	
	public String getPrediction() {
		return m_prediction;
	}
	
	public boolean hasConfidence() {
		return m_confidence != null;
	}
	
	public Double getConfidence() {
		return m_confidence;
	}
	
	public Double getPositiveConfidence() {
		if (!m_positive && m_confidence != null) {
			return 1 - m_confidence;
		} else {
			return m_confidence;
		}
		
	}
	
	public boolean hasRank() {
		return m_rank != null;
	}
	
	public Double getRank() {
		return m_rank;
	}
	
	public void setRank(final Double rank) {
		m_rank = rank;
	}

	@Override
	public int compareTo(Prediction o) {
		if (hasRank() && o.hasRank()) {
			if (getRank() < o.getRank()) {
				return -1;
			} else if (getRank() > o.getRank()) {
				return 1;
			}
		} else if (hasConfidence() && o.hasConfidence()) {
			if (getPositiveConfidence() > o.getPositiveConfidence()) {
				return -1;
			} else if (getPositiveConfidence() < o.getPositiveConfidence()) {
				return 1;
			}
		}
		return 0;
	}

}
