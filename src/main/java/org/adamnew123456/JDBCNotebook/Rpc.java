package org.adamnew123456.JDBCNotebook;

import java.sql.*;
import java.util.List;
import java.util.Map;

/** The actual implementation of the RPC methods. */
public class Rpc {
  private JdbcConnection connection;
  private JdbcResultPaginator paginator;

  public boolean finished;

  public Rpc(JdbcConnection connection) {
    this.finished = false;
    this.connection = connection;
    this.paginator = null;
  }

  public List<TableMetadata> tables() {
    try {
      return connection.getTables();
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public List<TableMetadata> views() {
    try {
      return connection.getViews();
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public List<ColumnMetadata> columns(String catalog, String schema, String table) {
    try {
      return connection.getColumns(catalog, schema, table);
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public boolean execute(String query) {
    if (paginator != null) {
      throw new RuntimeException("Cannot execute when other query is active");
    }

    try {
      paginator = connection.execute(query);
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }

    return true;
  }

  public List<ReaderMetadata> metadata() {
    if (paginator == null) {
      throw new RuntimeException("Cannot get metadata without active query");
    }

    return paginator.getMetadata();
  }

  public int count() {
    try {
      return paginator.getResultCount();
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public List<Map<String, String>> page(int size) {
    if (size <= 0) {
      throw new RuntimeException("Page size must be positive integer");
    }
    try {
      return paginator.getPage(size);
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public boolean finish() {
    if (paginator == null) {
      throw new RuntimeException("Cannot finish without active query");
    }

    try {
      paginator.close();
      paginator = null;
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }

    return true;
  }

  public boolean quit() {
    if (paginator != null) {
      throw new RuntimeException("Cannot quit when query active");
    }

    finished = true;
    return true;
  }
}
