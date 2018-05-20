package org.adamnew123456.JDBCNotebook;

import java.sql.*;

import org.eclipse.jetty.server.Server;

public class App
{
    private static void printHelpAndDie() {
        System.err.println("server [-p <port-number>] -j <class-name> <connection-string>");
        System.exit(1);
    }

    public static void main(String[] args)
    {
        int portNumber = -1;
        String className = null;
        String connectionString = null;

        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-p")) {
                    if (portNumber != -1) printHelpAndDie();

                    portNumber = Integer.parseInt(args[i + 1]);
                    if (portNumber < 1 || portNumber > 65535) printHelpAndDie();

                    i++;
                }
                else if (args[i].equals("-j")) {
                    if (className != null) printHelpAndDie();

                    className = args[i + 1];
                    connectionString = args[i + 2];
                    i += 2;
                }
            }
        } catch (IndexOutOfBoundsException error) {
            printHelpAndDie();
        } catch (NumberFormatException error) {
            printHelpAndDie();
        }
        
        if (className == null) printHelpAndDie();

        portNumber = portNumber == -1 ? 1995 : portNumber;
        
        JdbcConnection connection = new JdbcConnection(className, connectionString);
        try {
        	connection.open();
        } catch (SQLException error) {
        	System.err.println("Could not open connection: " + error);
        	System.exit(1);
        } catch (ClassNotFoundException error) {
        	System.err.println("Could not load driver class " + className);
        	System.exit(1);
        }
        
        Server server = new Server(portNumber);
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
