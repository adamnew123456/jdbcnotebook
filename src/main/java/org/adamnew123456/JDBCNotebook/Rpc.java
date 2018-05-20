package org.adamnew123456.JDBCNotebook;

import java.sql.*;
import java.util.List;
import java.util.Map;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;

/**
 * The actual implementation of the RPC methods.
 */
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

    public List<ColumnMetadata> columns() {
        try {
            return connection.getColumns();
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

    public ReaderMetadata metadata() {
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

    public List<Map<String, String>> page() {
        try {
            return paginator.getPage();
        } catch (SQLException error) {
            throw new RuntimeException(error);
        }
    }

    public boolean finish() {
        if (paginator == null) {
            throw new RuntimeException("Cannot finish without active query");
        }

        try
        {
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
