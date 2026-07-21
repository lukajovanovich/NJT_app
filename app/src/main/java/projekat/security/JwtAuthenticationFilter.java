package projekat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import projekat.model.ClanOrganizacije;
import projekat.repository.ClanOrganizacijeRepository;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServis jwtServis;
    private final ClanOrganizacijeRepository clanRepo;

    public JwtAuthenticationFilter(JwtServis jwtServis, ClanOrganizacijeRepository clanRepo) {
        this.jwtServis = jwtServis;
        this.clanRepo = clanRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtServis.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null && jwtServis.isTokenValid(token)) {
                ClanOrganizacije korisnik = clanRepo.findByEmail(email);

                if (korisnik != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    korisnik.getEmail(),
                                    null,
                                    SecurityRoles.toAuthorities(korisnik)
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ignored) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
