package chatting;

import chatting.server.MainServer;

public class Main {

  public static void main(String[] args) {
    MainServer server=new MainServer();
    server.run();
  }

}
