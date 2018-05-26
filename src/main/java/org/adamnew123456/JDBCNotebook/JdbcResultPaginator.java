package org.adamnew123456.JDBCNotebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper around a ResultSet that returns its rows in pages.
 */
public class JdbcResultPaginator {
	private ResultSet results;
	private ReaderMetadata metadata;
	private int resultCount;

	private int pageSize = 100;

	/**
	 * Builds a new paginator from a query that did return a result set.
	 */
	public JdbcResultPaginator(ResultSet results) throws SQLException {
		this.results = results;
		this.resultCount = -1;

		this.metadata = new ReaderMetadata();
		populateMetadata();
	}

	/**
	 * Builds a new paginator from a query that didn't return a result set.
	 */
	public JdbcResultPaginator(int resultCount) {
		this.results = null;
		this.resultCount = resultCount;
		this.metadata = new ReaderMetadata();
	}

	/**
	 * Loads the column metdata from the result set.
	 */
	private void populateMetadata() throws SQLException {
		ResultSetMetaData columns = results.getMetaData();
		for (int i = 1; i <= columns.getColumnCount(); i++) {
			metadata.columnNames.add(columns.getColumnName(i));
			metadata.columnTypes.add(columns.getColumnTypeName(i));
		}
	}

	/**
	 * Returns the query's column information.
	 */
	public ReaderMetadata getMetadata() {
		return metadata;
	}

	/**
	 * Returns the query's update count. Only valid if the query didn't return a
	 * result set.
	 */
	public int getResultCount() throws SQLException {
		if (resultCount == -1) {
			throw new SQLException("Cannot get update count when results are available");
		}

		return resultCount;
	}

	/**
	 * Returns the next page of restults from the result set. Only valid if the
	 * query returned a result set.
	 */
	public List<Map<String, String>> getPage() throws SQLException {
		ArrayList<Map<String, String>> page = new ArrayList<>();
		while (results.next()) {
			if (page.size() == pageSize)
				break;

			HashMap<String, String> row = new HashMap<>();
			for (int i = 1; i <= metadata.columnNames.size(); i++) {
				String columnName = metadata.columnNames.get(i - 1);
				Object value = results.getObject(i);
				if (value == null) {
					row.put(columnName, "null");
				} else {
					row.put(columnName, value.toString());
				}
			}

			page.add(row);
		}

		return page;
	}

	/**
	 * Closes the current result set, if there is one.
	 */
	public void close() throws SQLException {
		if (results != null) {
			results.close();
		}
	}
}
