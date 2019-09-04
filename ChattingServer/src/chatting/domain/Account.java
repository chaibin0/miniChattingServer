package chatting.domain;

public class Account {

  String userId;

  Room room;

  /**
   * 채팅방에 입장한 유저정보.
   * 
   * @param userId 유저 아이디
   * @param room 채팅방
   */
  public Account(String userId, Room room) {

    this.userId = userId;
    this.room = room;
  }



  public Account(String userId) {

    this.userId = userId;
  }

  public String getUserId() {

    return userId;
  }


  public void setUserId(String userId) {

    this.userId = userId;
  }


  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + ((room == null) ? 0 : room.hashCode());
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Account)) {
      return false;
    }
    Account other = (Account) obj;
    if (room == null) {
      if (other.room != null) {
        return false;
      }
    } else if (!room.equals(other.room)) {
      return false;
    }
    if (userId == null) {
      if (other.userId != null) {
        return false;
      }
    } else if (!userId.equals(other.userId)) {
      return false;
    }
    return true;
  }

}
