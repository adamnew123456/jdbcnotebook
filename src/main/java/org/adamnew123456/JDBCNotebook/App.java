package org.adamnew123456.JDBCNotebook;

import java.sql.*;

import org.eclipse.jetty.server.Server;

public class App
{
	static class RunConfiguration
	{
		public int portNumber;
		public String className;
		public String connectionString;
	}
	
    private static void printHelpAndDie() {
        System.err.println("server [-p <port-number>] -j <class-name> <connection-string>");
        System.exit(1);
    }
    
    private static RunConfiguration processCommandLineArguments(String[] args) {
    	RunConfiguration config = new RunConfiguration();
    	
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-p")) {
                    if (config.portNumber != -1) printHelpAndDie();

                    config.portNumber = Integer.parseInt(args[i + 1]);
                    if (config.portNumber < 1 || config.portNumber > 65535) return null;

                    i++;
                }
                else if (args[i].equals("-j")) {
                    if (config.className != null) return null;

                    config.className = args[i + 1];
                    config.connectionString = args[i + 2];
                    i += 2;
                }
            }
        } catch (IndexOutOfBoundsException error) {
        	return null;
        } catch (NumberFormatException error) {
        	return null;
        }
        
        config.portNumber = config.portNumber == -1 ? 1995 : config.portNumber;
        return config;
    }

    public static void main(String[] args)
    {
    	RunConfiguration config = new RunConfiguration();
    	if (config == null || config.className == null) printHelpAndDie();
        
        JdbcConnection connection = new JdbcConnection(config.className, config.connectionString);
        try {
        	connection.open();
        } catch (SQLException error) {
        	System.err.println("Could not open connection: " + error);
        	System.exit(1);
        } catch (ClassNotFoundException error) {
        	System.err.println("Could not load driver class " + config.className);
        	System.exit(1);
        }
        
        Server server = new Server(config.portNumber);
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
