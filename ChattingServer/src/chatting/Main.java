package chatting;

import chatting.server.MainServer;

public class Main {

  /**
   * 프로그램을 시작되면 MainServer를 가동하는 main 메소드.
   * @param args nothing
   */
  public static void main(String[] args) {

    MainServer server = new MainServer();
    server.run();
  }

}
