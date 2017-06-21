package server;


import jsonserver.common.Utils.Utilities;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.UserContainer;
import org.apache.log4j.Logger;
import jsonserver.common.view.Request;
import server.internal.CachedRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static jsonserver.common.Utils.Utilities.getTimestamp;


/**
 * Created by olof on 2016-07-09.
 */
public class ServerThread implements Runnable
{
    private static final Logger logger = Logger.getLogger(ServerThread.class);
    private static final RequestHandler HANDLER = new RequestHandler(); //should not create a new one each time..

    private Socket myClientSocket;
    private final static String FAILED_MESSAGE = "Failed";
    private final static AtomicInteger REQUEST_NUMBER = new AtomicInteger(0);


    public ServerThread(Socket clientSocket) throws ClassNotFoundException
    {
        myClientSocket = clientSocket;
    }

    @Override
    public void run()
    {

        BufferedReader inBufferReader;
        PrintWriter outPrintWriter = null;

        cleanUpTime();


        try
        {
            //TODO Serialize data instead?
            inBufferReader = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream(), StandardCharsets.UTF_8)); // "ISO-8859-1"
            outPrintWriter = new PrintWriter(myClientSocket.getOutputStream());
            String messageFromClient = inBufferReader.readLine();

            if (messageFromClient != null)
            {
                logger.info("Timestamp: " + getTimestamp());
                logger.info("------------- START OF REQUEST NUMBER: " + REQUEST_NUMBER.incrementAndGet() + " -------------------\n");
                logger.info("Got connection from..." + myClientSocket.getInetAddress());
                logger.info("Trying to read data...");

                long before = System.currentTimeMillis();
                JsonObject responseMessage = handleReceivedMessage(messageFromClient);
                long executionTime = System.currentTimeMillis() - before;
                responseMessage.addProperty("executionTime", executionTime);


                logger.info("Sending response:" + Utilities.getPrettyJsonString(responseMessage));
                logger.info("request took: " + executionTime + " millis");


                outPrintWriter.write(responseMessage + System.lineSeparator());
                outPrintWriter.flush();

                logger.info("Closing connection....");
                logger.info("------------- END OF REQUEST NUMBER " + REQUEST_NUMBER.get() + " -------------------\n");
            }

            outPrintWriter.close();
            myClientSocket.close();
        }
        catch (SocketException socketException)
        {
            logger.info("**************************");
            logger.info("** Socket exception: " + socketException.toString());
            logger.info("**************************");
            sendExceptionResponse(outPrintWriter);
        }
        catch (IOException e)
        {
            //TODO: capture different exceptions
            logger.error("Caught IOException : " + e.toString());
            sendExceptionResponse(outPrintWriter);
//            e.printStackTrace();
        }
//        catch (Exception e)
//        {
//            //TODO: capture different exceptions
//            logger.error("Caught exception : " + e.toString());
//            sendExceptionResponse(outPrintWriter);
//        }
    }

    private void cleanUpTime()
    {
        if (REQUEST_NUMBER.get() % 100 == 0)
        {
            logger.info("cleaning lady coming through...");
            HANDLER.cleanCache();
        }
    }


    private void sendExceptionResponse(PrintWriter outPrintWriter)
    {
        outPrintWriter.write(FAILED_MESSAGE + System.lineSeparator()); //hm
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    private JsonObject handleReceivedMessage(String messageFromClient)
    {
        UserContainer container = HANDLER.generateRequest(messageFromClient);
        logger.info("Executing request towards database");
        return HANDLER.executeRequest(container);
    }
}
