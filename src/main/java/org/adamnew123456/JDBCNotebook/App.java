package org.adamnew123456.JDBCNotebook;

import java.sql.*;
import java.util.Properties;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class App {
  static class RunConfiguration {
    public int portNumber;
    public String className;
    public String connectionString;
    public Properties properties;
    public boolean ssl;
    public String keystoreFile;
    public String keystorePassword;
    public String keyPassword;
  }

  private static void printHelpAndDie() {
    System.err.println(
        "server [-p <port-number>] [-s <keystore> <store-password> <key-password>] -j <class-name> <connection-string> (-P <property> <value>)*");
    System.exit(1);
  }

  private static RunConfiguration processCommandLineArguments(String[] args) {
    RunConfiguration config = new RunConfiguration();
    config.properties = new Properties();

    try {
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-p")) {
          if (config.portNumber != -1) printHelpAndDie();

          config.portNumber = Integer.parseInt(args[i + 1]);
          if (config.portNumber < 1 || config.portNumber > 65535) return null;

          i++;
        } else if (args[i].equals("-j")) {
          if (config.className != null) return null;

          config.className = args[i + 1];
          config.connectionString = args[i + 2];
          i += 2;
        } else if (args[i].equals("-P")) {
          config.properties.setProperty(args[i + 1], args[i + 2]);
          i += 2;
        } else if (args[i].equals("-s")) {
          config.ssl = true;
          config.keystoreFile = args[i + 1];
          config.keystorePassword = args[i + 2];
          config.keyPassword = args[i + 3];
          i += 3;
        }
      }
    } catch (IndexOutOfBoundsException error) {
      return null;
    } catch (NumberFormatException error) {
      return null;
    }

    config.portNumber = config.portNumber == 0 ? 1995 : config.portNumber;
    return config;
  }

  public static void main(String[] args) {
    RunConfiguration config = processCommandLineArguments(args);
    if (config == null || config.className == null) printHelpAndDie();

    JdbcConnection connection =
        new JdbcConnection(config.className, config.connectionString, config.properties);
    try {
      connection.open();
    } catch (SQLException error) {
      System.err.println("Could not open connection: " + error);
      System.exit(1);
    } catch (ClassNotFoundException error) {
      System.err.println("Could not load driver class " + config.className);
      System.exit(1);
    }

    Server server;
    if (config.ssl) {
      server = new Server();

      HttpConfiguration https = new HttpConfiguration();
      https.addCustomizer(new SecureRequestCustomizer());

      SslContextFactory sslContextFactory = new SslContextFactory();
      sslContextFactory.setKeyStorePath(config.keystoreFile);
      sslContextFactory.setKeyStorePassword(config.keystorePassword);
      sslContextFactory.setKeyManagerPassword(config.keyPassword);
      ServerConnector sslConnector =
          new ServerConnector(
              server,
              new SslConnectionFactory(sslContextFactory, "http/1.1"),
              new HttpConnectionFactory(https));
      sslConnector.setPort(config.portNumber);
      server.setConnectors(new Connector[] {sslConnector});
    } else {
      server = new Server(config.portNumber);
    }

    RpcHttpAdapter adapter = new RpcHttpAdapter(server, connection);
    server.setHandler(adapter);

    try {
      server.start();
      server.dumpStdErr();
    } catch (Exception error) {
      System.out.println(error.toString());
    }

    try {
      server.join();
    } catch (InterruptedException error) {
      System.out.println("Unable to stop server: " + error.toString());
    }
  }
}
