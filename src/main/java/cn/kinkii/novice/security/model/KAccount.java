package cn.kinkii.novice.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Getter
public class KAccount implements UserDetails, Principal {

    private String id;
    private String username;
    private String encryptedPassword;

    public KAccount(String id, String username) {
        this(id, username, null);
    }

    public KAccount(String id, String username, String encryptedPassword) {
        this.id = id;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    @Setter
    private Collection<String> roleNames = new HashSet<>();
    @Setter
    private Collection<? extends GrantedAuthority> authorities = new HashSet<>();
    @Setter
    private boolean isSupervisor = false;

    @Setter
    private boolean enabled = true;
    @Setter
    private boolean accountExpired = false;
    @Setter
    private boolean accountLocked = false;
    @Setter
    private boolean passwordExpired = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return encryptedPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !passwordExpired;
    }

    @Override
    public String getName() {
        return username;
    }
}
