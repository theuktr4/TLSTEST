import javax.net.ssl.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class Client implements Runnable {
    private String version;
    private int count;
    private final int PORT = 8083;
    private String[] cipher_suites = {"TLS_AES_128_GCM_SHA256"};
    private boolean sessionResumption;

    private SSLServerSocket serverSocket;

    public Client(String tlsVersion,int count, boolean resumeSession) {
        this.version = tlsVersion;
        this.count = count;
        this.sessionResumption = resumeSession;
    }

    private SSLContext getContext() {
        SSLContext context = null;
        try {
            InputStream stream = this.getClass().getResourceAsStream("/sslclienttrust");
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] trustStorePassword = "]3!z2Tb?@EHu%d}Q".toCharArray();
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
        long startTime = -1;
        try (SSLSocket socket = createSocket(sf)) {
            //InputStream is = new BufferedInputStream(socket.getInputStream());
            startTime = System.currentTimeMillis();
            socket.startHandshake();
        }
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Time Handshake " + timeElapsed );

    }

    private SSLSocket createSocket(SSLSocketFactory sf) throws IOException {
        SSLSocket s = (SSLSocket) sf.createSocket("localhost", PORT);
        s.setEnabledProtocols(new String[]{version});
        //s.setEnabledCipherSuites(cipher_suites);
        return s;
    }

    @Override
    public void run() {
        SSLSocketFactory s = null;
        if(sessionResumption){
            s = getContext().getSocketFactory();
        }
        while(count>0){
            try {
                if(!sessionResumption){
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
