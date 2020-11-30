package cn.kinkii.novice.security.service;

import cn.kinkii.novice.security.core.KAuthException;
import cn.kinkii.novice.security.model.KAccount;

import java.util.Map;

public interface KCodeService {

    KAccount exchangeUserByCode(String code, Map<String, String[]> additionalParams) throws KAuthException;

}
