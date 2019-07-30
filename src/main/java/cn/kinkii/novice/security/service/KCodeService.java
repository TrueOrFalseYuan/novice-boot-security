package cn.kinkii.novice.security.service;

import cn.kinkii.novice.security.core.KAuthException;
import cn.kinkii.novice.security.model.KAccount;

public interface KCodeService {

    KAccount exchangeUserByCode(String code) throws KAuthException;

}
