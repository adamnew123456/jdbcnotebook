package org.adamnew123456.JDBCNotebook;

import com.googlecode.jsonrpc4j.JsonRpcServer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/** Mediates between the embedded Jetty instance and the JSON-RPC processor. */
public class RpcHttpAdapter extends AbstractHandler {
  private Server jetty;
  private JsonRpcServer server;
  private Rpc rpc;

  public RpcHttpAdapter(Server jetty, JdbcConnection connection) {
    super();
    this.jetty = jetty;
    this.rpc = new Rpc(connection);
    this.server = new JsonRpcServer(rpc);
    this.server.setErrorResolver(new StandardErrorResolver());
  }

  private void processCORSRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
    response.addHeader("Access-Control-Allow-Headers", "Content-Type");
    response.addHeader("Access-Control-Max-Age", "86400");
  }

  public void handle(
      String s,
      Request request,
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse)
      throws IOException, ServletException {
    processCORSRequest(httpServletRequest, httpServletResponse);

    if (httpServletRequest.getMethod().equals("OPTIONS")) {
      httpServletResponse.setContentLength(0);
      httpServletResponse.setStatus(200);
      httpServletResponse.getOutputStream().flush();
      return;
    }

    server.handle(httpServletRequest, httpServletResponse);

    if (rpc.finished) {
      final Server localJetty = jetty;

      // This doesn't actually kill the server if done in the same
      // thread, so a new one needs to be spawned for this purpose.
      new Thread() {
        public void run() {
          try {
            localJetty.stop();
          } catch (Exception error) {
            throw new RuntimeException(error);
          }
        }
      }.start();
    }
  }
}
