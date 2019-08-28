package chatting.server;

import chatting.domain.Room;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.StringJoiner;

public class RoomServer {

  /**
   * 모든 방 리스트를 가져와서 클라이언트에게 리스트를 넘겨준다.
   * 
   * @param userId 유저 아이디
   * @param clientSocket 클라이언트 소켓
   */
  public static void getRoom(String userId, Socket clientSocket) {

    System.out.println("RoomServer : getRoom()");

    try (PrintWriter writer =
        new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

      Set<Long> keys = MainServer.getRooms().keySet();

      StringJoiner sj = new StringJoiner(",");
      if (keys.isEmpty()) {
        writer.println(sj);
      }
      for (long key : keys) {
        sj.add(String.valueOf(MainServer.getRooms().get(key).getRoomId()));
        sj.add(MainServer.getRooms().get(key).getName());
        sj.add(String.valueOf(MainServer.getRooms().get(key).getCount()));
        writer.println(sj);
      }

      writer.flush();
    } catch (EOFException e) {
      System.out.println("리스트에 아무것도 없습니다.");
    } catch (IOException e) {
      System.out.println("파일이 없거나 데이터를 찾을 수 없습니다");
      e.printStackTrace();
    }
  }


  /**
   * 요청한 클라이언트에게 방을 만들어준다.
   * 
   * @param userId 유저아이디
   * @param title 제목명
   * @param clientSocket 클라이언트 소켓
   */
  public static synchronized void makeRoom(String userId, String title, Socket clientSocket) {

    System.out.print("RoomServer.makeRoom() : " + userId + " " + title);

    try (BufferedWriter writerToClient =
        new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
      Room room = new Room();
      room.setName(title);
      room.setCount(1);
      room.setRoomId(Room.getIncrement());
      MainServer.getRooms().put(room.getRoomId(), room);

      // 클라이언트에게 응답해줌
      writerToClient.write(String.valueOf(room.getRoomId()));
      writerToClient.flush();

    } catch (IOException e) {
      System.out.println("파일이 없거나 데이터를 찾을 수 없습니다");
      e.printStackTrace();
    }

  }

  /**
   * 선택한 채팅방에 입장합니다.
   * 
   * @param userId 유저아이디
   * @param number 방 번호
   * @param clientSocket 데이터 전송할 클라이언트 소켓
   */
  public static void roomIn(String userId, String number, Socket clientSocket) {

    System.out.print("RoomServer.roomIn() : " + userId + " " + number);

    try (PrintWriter writer =
        new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
      if (MainServer.getRooms().containsKey(Long.parseLong(number))) {
        // 들어갈 문에 인원수 추가
        MainServer.getRooms().get(Long.parseLong(number)).addCount();
        for (int i = 0; i < MainServer.getRooms().get(Long.parseLong(number)).getUsers()
            .size(); i++) {
          // 다른 사람의 아이디를 저장한다.
          writer.println(MainServer.getRooms().get(Long.parseLong(number)).getUsers().get(i));
        }
      }
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
