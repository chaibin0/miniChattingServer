package chatting.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import chatting.domain.Room;

public class MainServer {

  private static Map<Long, Room> rooms = new HashMap<>();

  private static Map<String, Set<Integer>> userState = new HashMap<>();

  Socket clientSocket;

  PrintWriter writer;

  ServerSocket serverSock;

  String userId;

  BufferedReader br;

  public static Map<Long, Room> getRooms() {

    return rooms;
  }


  public static Map<String, Set<Integer>> getUserState() {

    return userState;
  }


  public Socket getClientSocket() {

    return clientSocket;
  }


  public PrintWriter getWriter() {

    return writer;
  }


  public ServerSocket getServerSock() {

    return serverSock;
  }


  public String getUserId() {

    return userId;
  }

  public void run() {

    try {
      Runnable Clienthandler = () -> {
        try {
          br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          StringTokenizer st;

          String line = "";
          String roomNumber = "";
          line = br.readLine();

          st = new StringTokenizer(line);
          switch (st.nextToken()) {
            // find account
            case "find":
              userId = st.nextToken();
              String pwd = st.nextToken();
              LoginServer.findByUserIdAndPwd(userId, pwd, clientSocket);
              break;

            // show roomList
            case "roomList":
              userId = st.nextToken();
              System.out.println("RoomServer.getRoom() : " + userId);
              RoomServer.getRoom(userId, clientSocket);
              break;
            // enter the room
            case "roomIn":
              userId = st.nextToken();
              roomNumber = st.nextToken();
              RoomServer.roomIn(userId, roomNumber, clientSocket);
              break;

            // out of the room
            case "roomOut":
              break;

            // make the room
            case "roomMake":
              userId = st.nextToken();
              StringJoiner title = new StringJoiner(" ");
              while (st.hasMoreTokens()) {
                title.add(st.nextToken());
              }
              RoomServer.makeRoom(userId, title.toString(), clientSocket);
              break;
            // chat the message
            case "sendMessage":
              userId = st.nextToken();
              roomNumber = st.nextToken();
              StringJoiner message = new StringJoiner(" ");
              while (st.hasMoreTokens()) {
                message.add(st.nextToken());
              }

              ChatServer.sendMessage(userId, roomNumber, message.toString());
              break;

            default:
              break;

          }


        } catch (IOException e) {
          e.printStackTrace();
        }

      };
      serverSock = new ServerSocket(5001);
      while (true) {
        clientSocket = serverSock.accept();
        Thread t = new Thread(Clienthandler);
        t.start();
        writer = new PrintWriter(clientSocket.getOutputStream());
        writer.println("응답");
        System.out.println("got a connection");



      }
    } catch (

    IOException e) {
      e.printStackTrace();
    }

  }

}
