import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

public class Server implements Runnable {
    private String version;
    private boolean keepAlive;
    private final int PORT = 8083;
    private String[] cipher_suites;

    private SSLServerSocket serverSocket;

    public Server(String tlsVersion, boolean keepAlive) {
        this.version = tlsVersion;
        this.keepAlive = keepAlive;
    }

    private SSLContext getContext() {
        SSLContext context = null;
        try {
            InputStream stream = this.getClass().getResourceAsStream("/sslserverkeys");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(stream, "7x*;^C(HU~5}@P?h".toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "7x*;^C(HU~5}@P?h".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();

            context = SSLContext.getInstance(version);
            context.init(km, tm, null);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return context;
    }

    public void acceptHandshake() throws IOException {
        //verbindung
        try (SSLSocket socket = (SSLSocket) serverSocket.accept()) {
            socket.setEnabledProtocols(new String[]{version});
            socket.startHandshake();

        }

    }
    public void configure(){
        SSLServerSocketFactory factory = getContext().getServerSocketFactory();
        try {
            serverSocket = ((SSLServerSocket) factory.createServerSocket(this.PORT));
            //config
            serverSocket.setEnabledProtocols(new String[]{version});
            serverSocket.setEnabledCipherSuites(serverSocket.getSupportedCipherSuites());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        configure();
            try {
                while(keepAlive){
                    acceptHandshake();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
