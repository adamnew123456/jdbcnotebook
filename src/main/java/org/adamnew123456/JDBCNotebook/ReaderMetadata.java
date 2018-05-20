package org.adamnew123456.JDBCNotebook;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReaderMetadata {
    @JsonProperty("columnnames")
    public List<String> columnNames;

    @JsonProperty("columntypes")
    public List<String> columnTypes;
    
    public ReaderMetadata() {
    	columnNames = new ArrayList<String>();
    	columnTypes = new ArrayList<String>();
    }
}