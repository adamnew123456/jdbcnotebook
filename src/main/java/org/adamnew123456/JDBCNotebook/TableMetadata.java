package org.adamnew123456.JDBCNotebook;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TableMetadata {
    @JsonProperty("catalog")
    public String catalog;

    @JsonProperty("table")
    public String table;
}