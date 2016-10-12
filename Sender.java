import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.zip.*;


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
    
    //set up to send packet
    byte[] sendData = constructMsg(message, 0);
    InetAddress serverAddress = InetAddress.getByName(host);
    DatagramSocket clientSocket = new DatagramSocket();
    clientSocket.setSoTimeout(500);
    DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, serverAddress, port);
      
    //set up to receive packet
    byte[] receivedBuffer = new byte[1024];
    DatagramPacket receivedPkt = new DatagramPacket(receivedBuffer, receivedBuffer.length);
    
    //send packet
    while(true) {

      clientSocket.send(sendPkt);
      boolean isFail = true;
    //receive packet
      while(true) {
   
        try {
        clientSocket.receive(receivedPkt);
     
        String receivedData = new String(receivedPkt.getData(), 0 , receivedPkt.getLength());
        boolean isOk = checkStatus(receivedData);
      
        if(!isOk) {
          break;
        }
        
        isFail = false;
        }catch(SocketTimeoutException e ) {
          isFail = true;
          
        }finally {
         break; 
        }
      }
      if(!isFail) {
        break;
      }
    }
    clientSocket.close();
    
  }
  public byte[] constructMsg(String message, int seq) {
    StringBuilder sb = new StringBuilder();
    
    String checksumFile = new StringBuilder().append(seq).append(message).toString();
    byte[] fileSize = checksumFile.getBytes();
    CRC32 checksum = new CRC32();
    checksum.update(fileSize);
    
    //build a string with seqNo, checksum and message
    sb.append(seq).append("\n").append(checksum.getValue()).append("\n").append(message);

    String newMessage = sb.toString();
    //System.out.println(newMessage);
    return newMessage.getBytes();
  }
  
  
  public boolean checkStatus(String message) {

    String[] msgArr = message.split("\n");

    String seqNo = msgArr[0].trim();
    String checksum = msgArr[1].trim();
    try {
      boolean isCorrupted = checkCorruption(seqNo, Long.parseLong(checksum));
      if(isCorrupted) {
        return false;
      }
      if(seqNo.equals("-1")) {
        return false;
      }
    }catch(NumberFormatException e) {
      return false;
    }
    return true;
  }
  
  public boolean checkCorruption(String seqNo, long originalChecksum) {
   CRC32 getChecksum = new CRC32();
   getChecksum.update(seqNo.getBytes());
   long checksum =  getChecksum.getValue();
   if(checksum != originalChecksum) {
     return true;
   }
   return false;
   
  }
}