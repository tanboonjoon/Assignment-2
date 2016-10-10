import java.net.*;
import java.nio.*;
import java.util.Scanner;


class Sender {
  public static void main(String[] args) throws Exception {
    // check if the number of command line argument is 4
    if (args.length != 1) {
      System.out.println("Usage: java Sender <unreliNetPort>");
      System.exit(1);
    }
    new Sender("localhost",  Integer.parseInt(args[0]));
  }
  
  public Sender(String host, int port) throws Exception {
    // Do not change this
    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      String line = sc.nextLine();
      sendMessage(line, host, port);
      // Sleep a bit. Otherwise sunfire might get so busy
      // that it actually drops UDP packets.
      Thread.sleep(20);
    }
  }
  
  public void sendMessage(String message, String host, int port) throws Exception {
    // You can assume that a single message is shorter than 750 bytes and thus
    // fits into a single packet.
    // Implement me
  }
}