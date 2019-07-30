package cn.kinkii.novice.security.service;

import cn.kinkii.novice.security.core.KAuthException;
import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.model.KAccount;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface KAccountService extends UserDetailsService {

    KAccount loadKAccountByUsername(String username) throws KAuthException;

    @Override
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return loadKAccountByUsername(username);
        } catch (KAuthException e) {
            if (e instanceof KAccountNotFoundException) {
                throw new UsernameNotFoundException(KSecurityMessageUtils.getExceptionMessage(KAccountNotFoundException.class), e);
            } else {
                throw new InternalAuthenticationServiceException(e.getMessage(), e);
            }
        }
    }
}
