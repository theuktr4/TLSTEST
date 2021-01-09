public class Main {
    public static void main(String[] args) {
        //false == Client Side Session (ID)
        //true == Server Side Session (Ticket)
        System.setProperty("jdk.tls.server.enableSessionTicketExtension","true");
        System.setProperty("jdk.tls.client.enableSessionTicketExtension","true");
        new Thread(new Server("TLSv1.3",true)).start();
        new Thread(new Client("TLSv1.3",300,false)).start();

    }
}
