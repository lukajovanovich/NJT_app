package projekat.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekat.model.ClanOrganizacije;
import projekat.repository.ClanOrganizacijeRepository;

@Service
public class AuthContextServis {

    private final ClanOrganizacijeRepository clanRepo;

    public AuthContextServis(ClanOrganizacijeRepository clanRepo) {
        this.clanRepo = clanRepo;
    }

    public ClanOrganizacije getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Korisnik nije autentifikovan.");
        }

        ClanOrganizacije korisnik = clanRepo.findByEmail(authentication.getName());
        if (korisnik == null) {
            throw new AccessDeniedException("Korisnik nije pronađen.");
        }

        return korisnik;
    }

    public boolean isAdmin(ClanOrganizacije korisnik) {
        return korisnik != null && korisnik.isAdmin();
    }
}
