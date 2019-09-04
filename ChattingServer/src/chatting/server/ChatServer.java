package chatting.server;

import chatting.domain.Account;
import chatting.domain.Room;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Set;



public class ChatServer {

  /**
   * RoomIn메서드가 성공되면 클라이언트와 서버와 계속 연결 유지를 시키고 클라이언트가 데이터 요청을 하면 메시지 파싱을 통해 데이터를 처리하는 메소드.
   * 
   * @param room 채팅방 번호
   * @param userId 클라이언트 유저
   * @param reader 클라이언트 입력 소켓
   * @param writer 클라이언트 출력 소켓
   * @throws IOException 메시지가 제대로 전송이 안될 경우 입출력 예외처리가 된다
   */
  public void run(Room room, String userId, BufferedReader reader, PrintWriter writer)
      throws IOException {

    Set<String> userIds;

    // 채팅 유지
    try {
      while (true) {
        String line = "";
        while ((line = reader.readLine()) != null) {
          System.out.println("chatServer" + line);
          String[] temp = line.split("&", -1);    //빈 채팅창도 출력
          userIds = room.getAccounts();

          switch (temp[0]) {
            case "sendMessage":
              sendMessage(temp, userIds, room);
              break;
            case "in":
              inMessage(temp[1], userIds, room);
              break;
            case "out":
              outMessage(temp[1], userIds, room);
              ChatServer.outOfChatRoom(temp[1], temp[2]);
              break;
            default:
          }
        }
      }
      // 소켓 강제종료 처리 (퇴장)
    } catch (SocketException e) {
      userIds = room.getAccounts();
      outMessage(userId, userIds, room);
      // userId의 모든 정보를 삭제처리
      LoginServer.removeUserFromProgram(userId);

    } catch (IOException e) {

      writer.write("fail");
      writer.flush();
    } finally {
      reader.close();
      writer.close();
    }
  }

  private void sendMessage(String[] message, Set<String> userIds, Room room) throws IOException {

    System.out.println("sendMaessage");
    for (String others : userIds) {
      PrintWriter otherWriter = MainServer.getUser().get(new Account(others, room));
      otherWriter
          .println("sendMessage" + "&" + message[1] + "&" + room.getRoomId() + "&" + message[3]);
      otherWriter.flush();
    }
  }

  private void outMessage(String userId, Set<String> userIds, Room room) {

    for (String others : userIds) {
      PrintWriter otherWriter = MainServer.getUser().get(new Account(others, room));
      otherWriter.println("out" + "&" + userId);
      otherWriter.flush();
    }
  }

  private void inMessage(String userId, Set<String> userIds, Room room) {

    for (String others : userIds) {
      PrintWriter otherWriter = MainServer.getUser().get(new Account(others, room));
      otherWriter.println("in" + "&" + userId);
      otherWriter.flush();
    }
  }



  private static void outOfChatRoom(String userId, String roomNumber) {

    System.out.println("채팅방 나감" + userId);
    Room room = MainServer.getRooms().get(Long.parseLong(roomNumber));

    // 계정접속 제거
    Account account = new Account(userId, room);
    MainServer.getUser().remove(account);

    // 방에서 퇴장
    room.getAccounts().remove(userId);
    if (room.getAccounts().size() == 0) {
      MainServer.getRooms().remove(room.getRoomId());
    } else {
      room.setCount(room.getCount() - 1);
    }

    MainServer.getUserInfo().remove(userId);
  }
}
