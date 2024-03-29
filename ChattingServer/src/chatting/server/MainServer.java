package chatting.server;

import chatting.domain.Account;
import chatting.domain.Room;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class MainServer {

  /**
   * 채팅방 번호를 이용해서 채팅방의 객체를 가져온다.
   */
  private static final Map<Long, Room> rooms = new HashMap<>();

  /**
   * 채팅방의 접속한 계정을 이해서 소켓 데이터를 가져온다.
   */
  private static final Map<Account, PrintWriter> user = new HashMap<>();

  /**
   * 유저 아이디가 현재 접속한 모든 채팅 방을 가져온다.
   */
  private static final Map<String, List<Room>> userInfo = new HashMap<>();

  private Socket sock;

  private ServerSocket serverSock;

  /**
   * 클라이언트 요청에 올때마다 스레드를 추가시킨다.
   */
  public void run() {

    try {
      Runnable clienthandler = () -> {
        try (Socket clientSocket = sock;
            BufferedReader br =
                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer =
                new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

          String line = "";
          String roomNumber = "";
          String name = "";
          line = br.readLine();
          System.out.println(line);
          StringTokenizer st = new StringTokenizer(line, "&");
          String order = st.nextToken();
          String userId = st.nextToken();

          switch (order) {
            // find account
            case "find":
              String pwd = st.nextToken();
              LoginServer.findByUserIdAndPwd(userId, pwd, clientSocket, writer);
              break;

            case "roomList":
              System.out.println("RoomServer.getRoom() : " + userId);
              RoomServer.getRoom(userId, writer);
              break;
            case "searchRoomList":
              String type = st.nextToken();
              name = st.nextToken();
              RoomServer.searchRoom(userId, type, name, writer);
              break;
            case "roomIn":
              roomNumber = st.nextToken();
              RoomServer.roomIn(userId, Long.parseLong(roomNumber), clientSocket);
              break;

            case "roomMake":
              StringJoiner title = new StringJoiner(" ");
              while (st.hasMoreTokens()) {
                title.add(st.nextToken());
              }
              RoomServer.makeRoom(userId, title.toString(), writer);
              break;

            case "signup":
              pwd = st.nextToken();
              name = st.nextToken();
              LoginServer.signUp(userId, pwd, name, writer);
              break;
            case "remove":
              LoginServer.removeUserFromProgram(userId);
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
        sock = serverSock.accept();
        Thread t = new Thread(clienthandler);
        t.start();
        System.out.println("server응답");
      }


    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static Map<Account, PrintWriter> getUser() {

    return user;
  }


  public static Map<Long, Room> getRooms() {

    return rooms;
  }

  public static Map<String, List<Room>> getUserInfo() {

    return userInfo;
  }
}
