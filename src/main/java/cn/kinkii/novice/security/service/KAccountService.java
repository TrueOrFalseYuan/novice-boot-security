package cn.kinkii.novice.security.service;

import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.model.KAccount;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface KAccountService extends UserDetailsService {

    KAccount loadKAccountByUsername(String username) throws KAccountNotFoundException;

    @Override
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return loadKAccountByUsername(username);
        } catch (KAccountNotFoundException e) {
            throw new UsernameNotFoundException(KSecurityMessageUtils.getExceptionMessage(KAccountNotFoundException.class), e);
        }
    }
}
