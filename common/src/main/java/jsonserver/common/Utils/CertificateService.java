package jsonserver.common.Utils;


import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * Created by Foten on 3/6/2017.
 */
public class CertificateService
{
    private static char[] password = "password".toCharArray();
    private final ClassLoader myClassLoader;
    private KeyManager[] myKeyManagers;
    private TrustManager[] myTrustManagers;

    public CertificateService()
    {
        myClassLoader = getClass().getClassLoader();
        try
        {
            String clientPath = "server/";
            readKeyStore(clientPath + "server.jks");
            readTrustStore(clientPath+ "serverTrust.jks");
        }
        catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e)
        {
            e.printStackTrace();
        }
    }

    public TrustManager[] getTrustManagers()
    {
        return myTrustManagers;
    }

    public KeyManager[] getKeyManagers()
    {
        return myKeyManagers;
    }

    private void readKeyStore(String path) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException
    {
        KeyStore keyStore;
        try (InputStream inputStream = myClassLoader.getResourceAsStream(path))
        {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, password);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);

            myKeyManagers = keyManagerFactory.getKeyManagers();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void readTrustStore(String path) throws NoSuchAlgorithmException
    {
        try (InputStream inputStream = myClassLoader.getResourceAsStream(path))
        {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, password);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            myTrustManagers = trustManagerFactory.getTrustManagers();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
