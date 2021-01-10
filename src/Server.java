import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class Server extends ClassLoader implements Runnable {
    private String version;
    private boolean keepAlive;
    private final int PORT = 8084;
    private String[] cipher_suites = {"TLS_DHE_DSS_WITH_AES_128_CBC_SHA"};

    private SSLServerSocket serverSocket;

    public Server(String tlsVersion, boolean keepAlive) {
        this.version = tlsVersion;
        this.keepAlive = keepAlive;
    }


    public void acceptHandshake() throws IOException {
        //verbindung
        try (SSLSocket socket = (SSLSocket) serverSocket.accept()) {
            socket.setEnabledProtocols(new String[]{version});
            socket.startHandshake();


        }

    }
    public void configure(){

        try {
            serverSocket = ((SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(PORT));
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
