package edu.kit.ifv.mobitopp.data;

import java.util.List;

public interface Matrix<T> {

	public void set(int row, int col, T value);

	public List<ZoneId> ids();

  public T get(ZoneId row, ZoneId col);

}
