package org.adamnew123456.JDBCNotebook;

import java.sql.*;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** The actual implementation of the RPC methods. */
public class Rpc {
  private JdbcConnection connection;
  private JdbcResultPaginator paginator;
  private static final Logger logger = LogManager.getLogger("RpcHttpAdapter");

  public boolean finished;

  public Rpc(JdbcConnection connection) {
    this.finished = false;
    this.connection = connection;
    this.paginator = null;
  }

  public List<TableMetadata> tables() {
    logger.info("tables()");
    try {
      return connection.getTables();
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public List<TableMetadata> views() {
    logger.info("views()");
    try {
      return connection.getViews();
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public List<ColumnMetadata> columns(String catalog, String schema, String table) {
    logger.info("views({}, {}, {})", catalog, schema, table);
    try {
      return connection.getColumns(catalog, schema, table);
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public boolean execute(String query) {
    logger.info("execute({})", query);
    if (paginator != null) {
      logger.warn("- Called with active query");
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
    logger.info("metadata()");
    if (paginator == null) {
      logger.warn("- Called without active query");
      throw new RuntimeException("Cannot get metadata without active query");
    }

    return paginator.getMetadata();
  }

  public int count() {
    logger.info("count()");
    if (paginator == null) {
      logger.warn("- Called without active query");
      throw new RuntimeException("Cannot page results without active query");
    }

    try {
      return paginator.getResultCount();
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public List<Map<String, String>> page(int size) {
    logger.info("page({})", size);
    if (paginator == null) {
      logger.warn("- Called without active query");
      throw new RuntimeException("Cannot page results without active query");
    }

    if (size <= 0) {
      logger.warn("- Invalid page size");
      throw new RuntimeException("Page size must be positive integer");
    }
    try {
      return paginator.getPage(size);
    } catch (SQLException error) {
      throw new RuntimeException(error);
    }
  }

  public boolean finish() {
    logger.info("finish()");
    if (paginator == null) {
      logger.warn("- Called without active query");
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
    logger.info("quit()");
    if (paginator != null) {
      logger.warn("- Called with active query");
      throw new RuntimeException("Cannot quit when query active");
    }

    finished = true;
    return true;
  }
}
