package server;


import jsonserver.common.Utils.Utilities;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.UserContainer;
import jsonserver.common.exception.InvalidRequestException;
import jsonserver.common.json.JsonDecoder;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static jsonserver.common.Utils.DateUtils.getCurrentTimestamp;

public class ServerThread implements Runnable
{
    private static final Logger logger = Logger.getLogger(ServerThread.class);
    private  final RequestHandler myRequestHandler; //should not create a new one each time..

    private Socket myClientSocket;
    private final static String FAILED_MESSAGE = "Failed";
    private final static AtomicInteger REQUEST_NUMBER = new AtomicInteger(0);


    public ServerThread(Socket clientSocket, boolean useCache) throws ClassNotFoundException
    {
        myRequestHandler = new RequestHandler(useCache);
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
                logger.info("Timestamp: " + getCurrentTimestamp());
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
            logger.error("Caught IOException : " + e.toString());
            sendExceptionResponse(outPrintWriter);
        }
    }

    private void cleanUpTime()
    {
        if (REQUEST_NUMBER.get() % 1000 == 50)
        {
            logger.info("Request number: " + REQUEST_NUMBER + ". cleaning lady coming through...");
            myRequestHandler.cleanCache();
        }
        else
        {
            logger.info("Request number: " + REQUEST_NUMBER + " No need for cleaning..");
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
        JsonObject response = null;
        try
        {
            UserContainer container = myRequestHandler.generateRequest(messageFromClient);
            logger.info("Executing request towards database");
            if (container != null)
            {
                response = myRequestHandler.executeRequest(container);
            }
            else
            {
                String errorMessage = "Not Enable to create container for request: " + messageFromClient;
                logger.info(errorMessage);
                response = JsonDecoder.createFailedResponseWithMessage(errorMessage, "JsonServer.ServerThread.handleReceivedMessage");
            }

        }
        catch (IOException | InvalidRequestException e)
        {
            logger.error("Failed to create request: " + e.toString());
            response = JsonDecoder.createFailedResponseWithMessage(e.getMessage(), "JsonServer.ServerThread.handleReceivedMessage");
        }
        return response;
    }
}
