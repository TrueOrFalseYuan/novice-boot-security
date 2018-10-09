package cn.kinkii.novice.security.web;

public interface KClientContainer {

  KClientDetails getKClientDetails();

  void setKClientDetails(KClientDetails details);

  default String getClientId() {
    if (getKClientDetails() != null) {
      return getKClientDetails().getClientId();
    } else {
      return null;
    }
  }

}
