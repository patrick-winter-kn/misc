package de.unikonstanz.winter.evaluation.predictions.node.enrichmentfactor;

public class SortItem<T1 extends Comparable<T1>, T2> implements Comparable<SortItem<T1, T2>> {
	
	private T1 m_sortObject;
	private T2 m_valueObject;
	
	public SortItem(final T1 sortObject, final T2 valueObject) {
		m_sortObject = sortObject;
		m_valueObject = valueObject;
	}
	
	public T2 getValueObject() {
		return m_valueObject;
	}
	
	@Override
	public int compareTo(final SortItem<T1, T2> o) {
		return m_sortObject.compareTo(o.m_sortObject);
	}

}
