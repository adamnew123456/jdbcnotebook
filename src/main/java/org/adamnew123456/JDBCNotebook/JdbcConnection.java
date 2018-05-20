package org.adamnew123456.JDBCNotebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JdbcConnection {
    private String className;
    private String connectionString;
    private Connection connection;

    public JdbcConnection(String className, String connectionString) {
        this.className = className;
        this.connectionString = connectionString;
        this.connection = null;
    }

    public void open() throws ClassNotFoundException, SQLException {
        Class.forName(className);
        connection = DriverManager.getConnection(connectionString);
    }

    public List<TableMetadata> getTables() throws SQLException {
    	ResultSet tableRows = connection.getMetaData().getTables(null, null, null, new String[] {"TABLE"});
    	
    	ArrayList<TableMetadata> tables = new ArrayList<>();
    	while (tableRows.next()) {
    		TableMetadata table = new TableMetadata();
    		table.catalog = tableRows.getString("TABLE_CAT");
    		table.table = tableRows.getString("TABLE_NAME");
    		tables.add(table);
    	}
    	
    	return tables;
    }
    
    public List<TableMetadata> getViews() throws SQLException {
    	ResultSet viewRows = connection.getMetaData().getTables(null, null, null, new String[] {"VIEW"});
    	
    	ArrayList<TableMetadata> views = new ArrayList<>();
    	while (viewRows.next()) {
    		TableMetadata view = new TableMetadata();
    		view.catalog = viewRows.getString("TABLE_CAT");
    		view.table = viewRows.getString("TABLE_NAME");
    		views.add(view);
    	}
    	
    	return views;
    }
    
    public List<ColumnMetadata> getColumns() throws SQLException {
    	ResultSet columnRows = connection.getMetaData().getColumns(null, null, "%", "%");
    	
    	ArrayList<ColumnMetadata> columns = new ArrayList<>();
    	while (columnRows.next()) {
    		ColumnMetadata column = new ColumnMetadata();
    		column.catalog = columnRows.getString("TABLE_CAT");
    		column.table = columnRows.getString("TABLE_NAME");
    		column.column = columnRows.getString("COLUMN_NAME");
    		column.dataType = columnRows.getString("TYPE_NAME");
    		columns.add(column);
    	}
    	
    	return columns;
    }
    
    public JdbcResultPaginator execute(String sql) throws SQLException {
    	Statement statement = connection.createStatement();
    	boolean hasResults = statement.execute(sql);
    	if (!hasResults) {
    		return new JdbcResultPaginator(statement.getUpdateCount());
    	} else {
    		return new JdbcResultPaginator(statement.getResultSet());
    	}
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
