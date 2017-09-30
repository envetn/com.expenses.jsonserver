package server;

import jsonserver.common.Utils.CertificateService;
import com.google.gson.JsonObject;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static jsonserver.common.Utils.Utilities.print;

/**
 * Created by Foten on 4/12/2017.
 */
public class ServerRunner
{
    private static final Logger logger = Logger.getLogger(ServerRunner.class);

    private final int myPort;
    private final boolean myIsSecure;
    private final boolean myUseCache;
    private boolean myIsRunning;

    public ServerRunner(boolean isSecure, boolean useCache, int port)
    {
        BasicConfigurator.configure();

        myUseCache = useCache;
        myIsSecure = isSecure;
        myPort = port;
    }

    public boolean registerService() throws UnknownHostException
    {
        InetAddress inetAddress = InetAddress.getLocalHost();
        System.out.println(inetAddress.toString());
        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("serverIp", "192.168.1.10"); //my own ip - TODO do it dynamically
        jsonObject.addProperty("serverIp", "127.0.0.1");
        jsonObject.addProperty("serverPort", myPort);

        String request = jsonObject.toString() + System.getProperty("line.separator");
        String response;
        int numberOfTries = 0;

        logger.info("Registering service...");
        boolean registered = false;

        while (! registered && numberOfTries <= 5)
        {
            try
            {
                HttpURLConnection connection = initUrlConnection();
                connection.setDoOutput(true);

                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                output.write(request.getBytes());
                output.flush();

                connection.connect();
                int result = connection.getResponseCode();
                if (result != HttpURLConnection.HTTP_ACCEPTED && result != HttpURLConnection.HTTP_CREATED && result != HttpURLConnection.HTTP_OK)
                {
                    connection.disconnect();
                    output.close();

                    String message = "Failed to connect Http status: " + result;
                    logger.info(message);
                    throw new IOException(message);
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                response = bufferedReader.readLine();

                connection.disconnect();

                bufferedReader.close();
                output.close();
                System.out.println(response);
                registered = true;
                logger.info("Service registered!");
            }
            catch (IOException e)
            {
                logger.info("Failed to register service: " + e.getMessage() + " --- Retrying");
                numberOfTries++;
                try
                {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e1)
                {
                    // Ignored
                }
            }
        }
        return registered;
    }

    private static HttpURLConnection initUrlConnection() throws IOException
    {
//        URL url = new URL("http://192.168.1.10:4567/register/json"); //remote ip
        URL localUrl = new URL("http://127.0.0.1:4567/register/json");
        HttpURLConnection connection = (HttpURLConnection) localUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setReadTimeout(10000 /* milliseconds */);
        connection.setConnectTimeout(15000 /* milliseconds */);
        connection.setDoInput(true);

        return connection;
    }

    public void tearDown()
    {
        myIsRunning = false;
    }

    public void startServer()
    {
        myIsRunning = true;
        Runnable serverThread;
        if (myIsSecure)
        {
            serverThread = this::startSslSever;

        }
        else
        {
            serverThread = this::startUnsecuredServer;
        }
        Thread thread = new Thread(serverThread);
        thread.start();
//        Runtime.getRuntime().addShutdownHook(thread);
        //TODO: Register shutdownhook
    }

    private void startSslSever()
    {
        logger.info("Listening on secure port: " + myPort);

        CertificateService certificateService = new CertificateService();
        try
        {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(certificateService.getKeyManagers(), certificateService.getTrustManagers(), null);
            SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(myPort);

            sslServerSocket.setEnabledProtocols(new String[]{"TLSv1.2"});

            while (myIsRunning)
            {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                String myMessageFromClient = bufferedReader.readLine();
                logger.info(myMessageFromClient);

                bufferedWriter.write("HELLO" + System.lineSeparator());
                bufferedWriter.flush();
            }
        }
        catch (IOException e)
        {
            logger.error("IOException during SSLsocket connections: ", e);
        }
        catch (NoSuchAlgorithmException | KeyManagementException e)
        {
            logger.error("Failed with SSL-connection: ", e);
        }
    }

    private void startUnsecuredServer()
    {
        logger.info("Listening on port: " + myPort);
        try
        {

            ServerSocket myServer = new ServerSocket(myPort);

            while (myIsRunning)
            {
                Socket clientSocket = myServer.accept();

                Thread thread = new Thread(new ServerThread(clientSocket, myUseCache));
                thread.start();
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            print("Caught IOException" + e);
            logger.error("Caught IOException", e);
        }
    }

}
