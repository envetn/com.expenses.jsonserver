import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import server.ServerRunner;

import java.net.*;

/**
 * Created by olof on 2016-07-09.
 * <p>
 * Where it all starts
 */
public class StartServer
{
    private static final Logger logger = Logger.getLogger(StartServer.class);
    private final static int PORT = 9875;
    private static boolean shouldLogOutput;
    private static boolean isSecure = false;

    public static void main(String[] args) throws UnknownHostException
    {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        for (String arg : args)
        {
            shouldLogOutput = arg.equals("Debug=true");
        }

        if (logger.isDebugEnabled())
        {
            logger.info("Starting jsonServer...");
        }
        //mySocketServer = new Server(PORT);

        ServerRunner runner = new ServerRunner(isSecure, PORT);
        boolean hasRegistered = runner.registerService();

        if (hasRegistered)
        {
            runner.startServer();
        }
    }
}
