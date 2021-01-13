public class Main {
    public static void main(String[] args) {


        //false == Client Side Session (ID)
        //true == Server Side Session (Ticket)

        System.setProperty("jdk.tls.server.enableSessionTicketExtension","true");
        System.setProperty("jdk.tls.client.enableSessionTicketExtension","true");
        System.setProperty("javax.net.ssl.keyStore","/home/seby/IdeaProjects/Example/resources/keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","sebastian");
        System.setProperty("javax.net.ssl.trustStore","/home/seby/IdeaProjects/Example/resources/truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","sebastian");

        new Thread(new Server("TLSv1.2",true)).start();
        new Thread(new Client("TLSv1.2", 200, false)).start();

    }
}
