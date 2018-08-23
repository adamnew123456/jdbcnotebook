package org.adamnew123456.JDBCNotebook;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Information about a result set retrieved from the server. */
public class ReaderMetadata {
  @JsonProperty("column")
  public String column;

  @JsonProperty("datatype")
  public String dataType;

  public ReaderMetadata(String column, String datatype) {
    this.column = column;
    this.dataType = datatype;
  }
}
