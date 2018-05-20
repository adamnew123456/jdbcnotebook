package org.adamnew123456.JDBCNotebook;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ColumnMetadata {
    @JsonProperty("catalog")
    public String catalog;

    @JsonProperty("table")
    public String table;

    @JsonProperty("column")
    public String column;

    @JsonProperty("datatype")
    public String dataType;
}