package chatting.server;

import chatting.domain.Account;
import chatting.domain.Room;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;



public class LoginServer {

  /**
   * 로그인 확인해주고 클라이언트에게 이름을 반환하는 함수.
   * 
   * @param userId 유저 아이디
   * @param pwd 패스워드
   * @param clientSocket 클라이언트 소켓
   * @param writer 클라이언트 출력 소켓 버퍼
   */
  public static synchronized void findByUserIdAndPwd(String userId, String pwd, Socket clientSocket,
      PrintWriter writer) {

    try (BufferedReader br = new BufferedReader(new FileReader("account.txt"))) {

      String line = "";

      String tempId;
      String tempPwd;
      String tempName;

      while ((line = br.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line);

        tempId = st.nextToken();
        tempPwd = st.nextToken();
        tempName = st.nextToken();

        // 아이디와 비번이 맞으면 처리
        if (userId.equals(tempId) && pwd.equals(tempPwd)) {
          writer.println(tempName);
          System.out.println(tempName);
          writer.flush();
          return;
        }
      }

      writer.println("nothing");
      System.out.println("아이디가 없음");
      writer.flush();


    } catch (FileNotFoundException e) {
      System.out.println("로그인 파일이 없습니다 loginService");
      writer.println("nothing");
      writer.flush();
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("로그인 데이터 읽는 도중 에러가 걸렸습니다. loginService");
      writer.println("nothing");
      writer.flush();
      e.printStackTrace();
    }
  }

  /**
   * 클라이언트 강제종료로 인해 소켓이 강제로 끊어졌을 경우 유저아이디의 모든 채팅방을 나가게 만드는 메소드 입니다.
   * 
   * @param userId 끊어진 유저 아이디.
   */
  public static void removeUserFromProgram(String userId) {

    System.out.println("아이디 제거" + userId);
    List<Room> data = MainServer.getUserInfo().get(userId);
    System.out.println("remove data " + data.size());

    for (Room room : data) {
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

  /**
   * 회원가입 데이터 저장 메소드이다.
   * 
   * @param userId 유저 아이디
   * @param pwd 패스워드
   * @param name 이름
   * @param writer 클라이언트 소켓 출력스트림
   */
  public static void signUp(String userId, String pwd, String name, PrintWriter writer) {

    System.out.println("signup()");
    try (BufferedWriter bw = new BufferedWriter(new FileWriter("account.txt", true))) {
      bw.write(userId + " " + pwd + " " + name + "\n");
      writer.println("success");
      writer.flush();
    } catch (IOException e) {
      writer.println("fail");
      e.printStackTrace();
    }

  }
}
