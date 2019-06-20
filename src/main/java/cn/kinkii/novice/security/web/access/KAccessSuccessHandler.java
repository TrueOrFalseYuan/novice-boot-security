package cn.kinkii.novice.security.web.access;

import cn.kinkii.novice.security.model.KAccount;

public interface KAccessSuccessHandler {

    void handle(KAccount kAccount);

}
