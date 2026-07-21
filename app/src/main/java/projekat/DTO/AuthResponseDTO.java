package projekat.DTO;

public class AuthResponseDTO implements Dto {

    private String token;
    private ClanOrganizacijeDTO korisnik;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, ClanOrganizacijeDTO korisnik) {
        this.token = token;
        this.korisnik = korisnik;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ClanOrganizacijeDTO getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(ClanOrganizacijeDTO korisnik) {
        this.korisnik = korisnik;
    }
}
