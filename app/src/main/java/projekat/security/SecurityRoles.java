package projekat.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import projekat.model.ClanOrganizacije;

public final class SecurityRoles {

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";
    public static final String RUKOVODILAC = "RUKOVODILAC";

    private SecurityRoles() {
    }

    public static Collection<? extends GrantedAuthority> toAuthorities(ClanOrganizacije korisnik) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + USER));

        if (korisnik != null && korisnik.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + ADMIN));
        }

        if (korisnik != null && korisnik.isRukovodilac()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + RUKOVODILAC));
        }

        return authorities;
    }
}
