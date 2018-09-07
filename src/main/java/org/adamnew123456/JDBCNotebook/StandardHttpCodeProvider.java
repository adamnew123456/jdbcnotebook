package org.adamnew123456.JDBCNotebook;

import com.googlecode.jsonrpc4j.HttpStatusCodeProvider;

public class StandardHttpCodeProvider implements HttpStatusCodeProvider {
  @Override
  public int getHttpStatusCode(int resultCode) {
    // RPC-level errors are never reported as transport-level errors
    return 200;
  }

  @Override
  public Integer getJsonRpcCode(int httpStatusCode) {
    return null;
  }
}
