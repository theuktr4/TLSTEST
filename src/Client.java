import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.Instant;


public class Client implements Runnable {
    private String version;
    private int count;
    private final int PORT = 8084;
    private String[] cipher_suites;
    private boolean sessionResumption;

    private SSLServerSocket serverSocket;

    public Client(String tlsVersion, int count, boolean resumeSession) {
        this.version = tlsVersion;
        this.count = count;
        this.sessionResumption = resumeSession;
    }


    private SSLContext getContext() {
        SSLContext context = null;
        try {
            InputStream stream = this.getClass().getResourceAsStream("/truststore");
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] trustStorePassword = "passphrase".toCharArray();
            trustStore.load(stream, trustStorePassword);
            context = SSLContext.getInstance(version);

            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(trustStore);
            TrustManager[] managers = factory.getTrustManagers();
            context.init(null, managers, null);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException e) {
            e.printStackTrace();

        }
        return context;
    }

    public void startHandshake(SSLSocketFactory sf) throws IOException {

        try (SSLSocket socket = createSocket(sf)) {

            Instant start = Instant.now();
            //InputStream is = new BufferedInputStream(socket.getInputStream());

            socket.startHandshake();

            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("Time Handshake " + timeElapsed);
        }


    }

    private SSLSocket createSocket(SSLSocketFactory sf) throws IOException {
        SSLSocket s = (SSLSocket) sf.createSocket("localhost", PORT);

        s.setEnabledProtocols(new String[]{version});
        //s.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_CBC_SHA256"});
        return s;
    }

    @Override
    public void run() {


        SSLSocketFactory s = null ;
        if (sessionResumption) {
            s = (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
        while (count > 0) {

            try {
                if (!sessionResumption) {
                    s = getContext().getSocketFactory();
                }
                startHandshake(s);
                count--;
                Thread.sleep(1);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                count = 0;
            }

        }

    }
}
