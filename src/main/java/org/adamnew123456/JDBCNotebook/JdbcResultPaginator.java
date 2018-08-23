package org.adamnew123456.JDBCNotebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A wrapper around a ResultSet that returns its rows in pages. */
public class JdbcResultPaginator {
  private ResultSet results;
  private List<ReaderMetadata> metadata;
  private int resultCount;

  /** Builds a new paginator from a query that did return a result set. */
  public JdbcResultPaginator(ResultSet results) throws SQLException {
    this.results = results;
    this.resultCount = -1;

    this.metadata = new ArrayList<ReaderMetadata>();
    populateMetadata();
  }

  /** Builds a new paginator from a query that didn't return a result set. */
  public JdbcResultPaginator(int resultCount) {
    this.results = null;
    this.resultCount = resultCount;
    this.metadata = new ArrayList<ReaderMetadata>();
  }

  /** Loads the column metdata from the result set. */
  private void populateMetadata() throws SQLException {
    ResultSetMetaData columns = results.getMetaData();
    for (int i = 1; i <= columns.getColumnCount(); i++) {
      metadata.add(new ReaderMetadata(columns.getColumnName(i), columns.getColumnTypeName(i)));
    }
  }

  /** Returns the query's column information. */
  public List<ReaderMetadata> getMetadata() {
    return metadata;
  }

  /** Returns the query's update count. Only valid if the query didn't return a result set. */
  public int getResultCount() throws SQLException {
    if (resultCount == -1) {
      throw new SQLException("Cannot get update count when results are available");
    }

    return resultCount;
  }

  /**
   * Returns the next page of restults from the result set. Only valid if the query returned a
   * result set.
   */
  public List<Map<String, String>> getPage(int size) throws SQLException {
    ArrayList<Map<String, String>> page = new ArrayList<>();
    while (results.next()) {
      if (page.size() == size) break;

      HashMap<String, String> row = new HashMap<>();
      for (int i = 1; i <= metadata.size(); i++) {
        String columnName = metadata.get(i - 1).column;
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

  /** Closes the current result set, if there is one. */
  public void close() throws SQLException {
    if (results != null) {
      results.close();
    }
  }
}
