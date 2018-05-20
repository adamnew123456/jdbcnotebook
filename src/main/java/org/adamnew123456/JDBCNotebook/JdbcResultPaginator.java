package org.adamnew123456.JDBCNotebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcResultPaginator {
	private ResultSet results;
	private ReaderMetadata metadata;
	private int resultCount;
	
	private int pageSize = 100;
	
	public JdbcResultPaginator(ResultSet results) throws SQLException {
		this.results = results;
		this.resultCount = -1;
		
		this.metadata = new ReaderMetadata();
		populateMetadata();
	}
	
	public JdbcResultPaginator(int resultCount) {
		this.results = null;
		this.resultCount = resultCount;
		this.metadata = new ReaderMetadata();
	}
	
	private void populateMetadata() throws SQLException {
		ResultSetMetaData columns = results.getMetaData();
		for (int i = 1; i <= columns.getColumnCount(); i++) {
			metadata.columnNames.add(columns.getColumnName(i));
			metadata.columnTypes.add(columns.getColumnTypeName(i));
		}
	}
	
	public ReaderMetadata getMetadata() {
		return metadata;
	}
	
	public int getResultCount() throws SQLException {
		if (resultCount == -1) {
			throw new SQLException("Cannot get update count when results are available");
		}
		
		return resultCount;
	}
	
	public List<Map<String, String>> getPage() throws SQLException {
		ArrayList<Map<String, String>> page = new ArrayList<>();
		while (results.next()) {
			if (page.size() == pageSize) break;
			
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
	
	public void close() throws SQLException {
		if (results != null) {
			results.close();
		}
	}
}
