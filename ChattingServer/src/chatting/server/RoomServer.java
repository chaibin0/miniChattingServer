package chatting.server;

import chatting.domain.Account;
import chatting.domain.Room;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.StringJoiner;



public class RoomServer {

  /**
   * 모든 방 리스트를 가져와서 클라이언트에게 리스트를 넘겨준다.
   * 
   * @param userId 유저 아이디
   * @param writer 소켓 출력 버퍼
   */
  public static void getRoom(String userId, PrintWriter writer) {

    System.out.println("RoomServer : getRoom()");

    Set<Long> keys = MainServer.getRooms().keySet();

    if (keys.isEmpty()) {
      writer.println("");
      writer.flush();

    }
    for (long key : keys) {
      StringJoiner sj = new StringJoiner(",");
      sj.add(String.valueOf(MainServer.getRooms().get(key).getRoomId()));
      sj.add(MainServer.getRooms().get(key).getName());
      sj.add(String.valueOf(MainServer.getRooms().get(key).getCount()));
      writer.println(sj);
      writer.flush();

    }

  }

  /**
   * 클라이언트로부터 정보를 받아 채팅방을 찾는 메소드.
   * 
   * @param userId 유저아이디
   * @param type 카테고리
   * @param name 정보
   * @param writer 클라이언트 출력 소켓
   */
  public static void searchRoom(String userId, String type, String name, PrintWriter writer) {

    System.out.println("searchRoom : getRoom()");

    boolean isNotFind = true;
    Set<Long> keys = MainServer.getRooms().keySet();

    for (long key : keys) {
      if (search(key, type, name)) {
        isNotFind = false;
        StringJoiner sj = new StringJoiner(",");
        sj.add(String.valueOf(MainServer.getRooms().get(key).getRoomId()));
        sj.add(MainServer.getRooms().get(key).getName());
        sj.add(String.valueOf(MainServer.getRooms().get(key).getCount()));
        writer.println(sj);
        writer.flush();
      }
    }
    if (isNotFind) {
      writer.println("");
      writer.flush();
    }
  }

  private static boolean search(long key, String type, String name) {

    if (type.equals("number") && key == Long.parseLong(name)) {
      return true;
    }

    if (type.equals("title") && checkName(MainServer.getRooms().get(key).getName(), name)) {
      return true;
    }

    return false;
  }

  private static boolean checkName(String searchName, String roomName) {

    if (searchName.contains(roomName)) {
      return true;
    }

    if (roomName.contains(searchName)) {
      return true;
    }

    return false;
  }

  /**
   * 요청한 클라이언트에게 방을 만들어준다.
   * 
   * @param userId 유저아이디
   * @param title 제목명
   * @param writer 클라이언트 소켓
   */
  public static synchronized void makeRoom(String userId, String title, PrintWriter writer) {

    System.out.print("RoomServer.makeRoom() : " + userId + " " + title);

    Room room = new Room();
    room.setName(title);
    room.setCount(0);
    room.setRoomId(Room.getIncrement());
    MainServer.getRooms().put(room.getRoomId(), room);

    // 클라이언트에게 응답해줌
    writer.write(String.valueOf(room.getRoomId()));
    writer.flush();

    System.out.println(String.valueOf(room.getRoomId()));


  }

  /**
   * 선택한 채팅방에 입장합니다. 클라이언트에 대한 소켓 정보는 항상 유지하고 있습니다.
   * 
   * @param userId 유저아이디
   * @param number 방 번호
   * @param clientSocket 데이터 전송할 클라이언트 소켓
   */
  public static void roomIn(String userId, long number, Socket clientSocket) {

    try (PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

      if (MainServer.getRooms().containsKey(number)) {
        Room room = MainServer.getRooms().get(number);

        // 채팅방에 유저정보와 인원수 업데이트
        room.getAccounts().add(userId);
        room.addCount();

        if (!MainServer.getUserInfo().containsKey(userId)) {
          MainServer.getUserInfo().put(userId, new ArrayList<>(Arrays.asList(room)));
        } else {
          MainServer.getUserInfo().get(userId).add(room);
        }

        // 유저 소켓정보 저장
        Account account = new Account(userId, room);
        MainServer.getUser().put(account, writer);
        System.out.println("RoomIn : account: " + account.getUserId());


        // 계정에 채팅방 정보를 저장한다.
        MainServer.getRooms().put(number, room);

        // AccessRoom 에서 해당 방에 대한 유저정보를 가져와서 클라이언트에게 되돌려준다.
        Set<String> userIds = room.getAccounts();
        for (String others : userIds) {
          writer.println(others);
        }
        writer.println("");
        writer.flush();

        // 다른사람에게 입장했다는 것을 알린다.
        System.out.println("userId : " + userId + " number : " + number);
        for (String others : userIds) {
          if (others == userId) {
            continue;
          }
          PrintWriter otherWriter = MainServer.getUser().get(new Account(others, room));
          otherWriter.println("in" + "&" + userId + "&" + number);
          otherWriter.flush();
        }

        ChatServer chatServer = new ChatServer();
        chatServer.run(room, userId, reader, writer);

      } else {
        System.out.println("방이 없습니다");
        writer.write("fail");
        writer.flush();

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }



}
