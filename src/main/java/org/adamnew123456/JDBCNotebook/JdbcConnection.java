package org.adamnew123456.JDBCNotebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/** A wrapper around a raw Connection that returns data in more useful formats. */
public class JdbcConnection {
  private String className;
  private String connectionString;
  private Properties connectionProperties;
  private Connection connection;

  public JdbcConnection(String className, String connectionString, Properties properties) {
    this.className = className;
    this.connectionString = connectionString;
    this.connectionProperties = properties;
    this.connection = null;
  }

  /** Opens a connection to the JDBC driver. */
  public void open() throws ClassNotFoundException, SQLException {
    Class.forName(className);
    connection = DriverManager.getConnection(connectionString, connectionProperties);
  }

  /** Gets a list of all tables in the database. */
  public List<TableMetadata> getTables() throws SQLException {
    ResultSet tableRows =
        connection.getMetaData().getTables(null, null, null, new String[] {"TABLE"});

    ArrayList<TableMetadata> tables = new ArrayList<>();
    while (tableRows.next()) {
      TableMetadata table = new TableMetadata();
      table.catalog = tableRows.getString("TABLE_CAT");
      if (table.catalog == null) table.catalog = "";

      table.schema = tableRows.getString("TABLE_SCHEM");
      if (table.schema == null) table.schema = "";

      table.table = tableRows.getString("TABLE_NAME");
      tables.add(table);
    }

    return tables;
  }

  /** Gets a list of all views in the database. */
  public List<TableMetadata> getViews() throws SQLException {
    ResultSet viewRows =
        connection.getMetaData().getTables(null, null, null, new String[] {"VIEW"});

    ArrayList<TableMetadata> views = new ArrayList<>();
    while (viewRows.next()) {
      TableMetadata view = new TableMetadata();
      view.catalog = viewRows.getString("TABLE_CAT");
      if (view.catalog == null) view.catalog = "";

      view.schema = viewRows.getString("TABLE_SCHEM");
      if (view.schema == null) view.schema = "";

      view.table = viewRows.getString("TABLE_NAME");
      views.add(view);
    }

    return views;
  }

  /** Gets a list of all columns in the database. */
  public List<ColumnMetadata> getColumns(String catalog, String schema, String table)
      throws SQLException {
    ResultSet columnRows = connection.getMetaData().getColumns(catalog, schema, table, "%");
    if (catalog == null) catalog = "";
    if (schema == null) schema = "";

    ArrayList<ColumnMetadata> columns = new ArrayList<>();
    while (columnRows.next()) {
      ColumnMetadata column = new ColumnMetadata();
      column.catalog = columnRows.getString("TABLE_CAT");
      if (column.catalog == null) column.catalog = "";

      column.schema = columnRows.getString("TABLE_SCHEM");
      if (column.schema == null) column.schema = "";

      column.table = columnRows.getString("TABLE_NAME");
      column.column = columnRows.getString("COLUMN_NAME");
      column.dataType = columnRows.getString("TYPE_NAME");
      columns.add(column);
    }

    return columns;
  }

  /** Executes a query, returning a paginator containing the results. */
  public JdbcResultPaginator execute(String sql) throws SQLException {
    Statement statement = connection.createStatement();
    boolean hasResults = statement.execute(sql);
    if (!hasResults) {
      return new JdbcResultPaginator(statement.getUpdateCount());
    } else {
      return new JdbcResultPaginator(statement.getResultSet());
    }
  }

  /*
   * Closes the connection to the JDBC driver.
   */
  public void close() throws SQLException {
    if (connection != null) {
      connection.close();
    }
  }
}
