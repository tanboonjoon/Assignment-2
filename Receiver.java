import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.zip.*;



class Receiver {
  public static void main(String[] args) throws Exception {
    // check if the number of command line argument is 4
    if (args.length != 1) {
      System.out.println("Usage: java Receiver <port>");
      System.exit(1);
    }
    new Receiver(Integer.parseInt(args[0]));
  }
  
  public Receiver(int port) throws Exception {
    // Do not change this
    DatagramSocket socket = new DatagramSocket(port);
    while (true) {
      String message = receiveMessage(socket);
      System.out.println(message);
    }
  }
  
  public String receiveMessage(DatagramSocket socket) throws Exception {
      // Implement me
    //set up socket
    StringBuilder sb;
    byte[] receiveBuffer = new byte[1024];
    
    while(true) {
      //set up to receive message
      DatagramPacket receivedPkt = new DatagramPacket(receiveBuffer, receiveBuffer.length);
      //receive message and block until a message is received
 
      socket.receive(receivedPkt);
      String receivedData = new String(receivedPkt.getData(), 0 , receivedPkt.getLength());
      sb = new StringBuilder();
      sb.append(receivedData);
      System.out.println(sb.toString());
      //deconstruct the message
      String[] msgArr = sb.toString().split("\n");
      InetAddress clientAddress = receivedPkt.getAddress();
      int clientPort = receivedPkt.getPort();
      
      if(msgArr.length != 3 ) {
        byte[] sendData = constructMsg(-1); 
      } else {

        String seqNo =  msgArr[0].trim();
        String checkSum = msgArr[1].trim();
        String message = msgArr[2];

        //check if it corrupted
        boolean isCorrupted = checkCorruption(seqNo, Long.parseLong(checkSum), message);
    
        //print message if not corrupted
        byte [] sendData = null;
        if (!isCorrupted) {
          System.out.println(message); 
          sendData = constructMsg(0);
        } else {
          sendData = constructMsg(-1); 
        }

   
        DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);

        socket.send(sendPkt);
      }
    }
      
    
  }
  public boolean checkCorruption(String seqNo, long originalCheckSum, String message) {
    String checksumFile = new StringBuilder().append(seqNo).append(message).toString();
    byte[] fileSize = checksumFile.getBytes();
    CRC32 getChecksum = new CRC32();
    getChecksum.update(fileSize);
    long checksum = getChecksum.getValue();
    
    if(originalCheckSum == checksum) {
      return false;
    }
    return true;
  }
  
  public byte[] constructMsg(int seqNo) {
    StringBuilder sb = new StringBuilder();
    sb.append(seqNo);
    String checksumFile = sb.toString();
    CRC32 checksum = new CRC32();
    checksum.update(checksumFile.getBytes());

    sb.append("\n").append(checksum.getValue());
    
    return sb.toString().getBytes();
    
    


  }
}