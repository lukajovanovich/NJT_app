package projekat.servis;

import jakarta.mail.internet.InternetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import projekat.model.Prijava;
import projekat.repository.ClanOrganizacijeRepository;

@Service
public class EmailServis {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServis.class);

    private final JavaMailSender mailSender;
    private final ClanOrganizacijeRepository clanRepo;
    private final boolean mailEnabled;
    private final String fromAddress;
    private final String displayName;
    private final String appName;
    private final List<String> dodatniAdminPrimaoci;

    public EmailServis(
            JavaMailSender mailSender,
            ClanOrganizacijeRepository clanRepo,
            @Value("${app.mail.enabled:false}") boolean mailEnabled,
            @Value("${app.mail.from:}") String fromAddress,
            @Value("${app.mail.display-name:}") String displayName,
            @Value("${app.name:App za upravljanje projektima}") String appName,
            @Value("${app.mail.admin-recipients:}") String adminRecipientsRaw
    ) {
        this.mailSender = mailSender;
        this.clanRepo = clanRepo;
        this.mailEnabled = mailEnabled;
        this.fromAddress = fromAddress == null ? "" : fromAddress.trim();
        this.displayName = displayName == null ? "" : displayName.trim();
        this.appName = appName == null ? "App za upravljanje projektima" : appName.trim();
        this.dodatniAdminPrimaoci = parseRecipients(adminRecipientsRaw);
    }

    public void posaljiAdminObavestenjeONovojPrijavi(Prijava prijava) {
        Set<String> primaoci = new LinkedHashSet<>(clanRepo.findUoEmails());
        primaoci.addAll(dodatniAdminPrimaoci);

        String nazivProjekta = prijava.getTim() != null && prijava.getTim().getProjekat() != null
                ? prijava.getTim().getProjekat().getNaziv()
                : "Nepoznat projekat";
        String nazivTima = prijava.getTim() != null ? prijava.getTim().getNaziv() : "Nepoznat tim";

        String subject = String.format("[%s] Nova prijava za projekat: %s", appName, nazivProjekta);
        String text = String.format(
                """
                Stigla je nova prijava na projekat.

                Korisnik: %s %s
                Email: %s
                Broj telefona: %s
                Projekat: %s
                Tim: %s
                Status: %s

                Pozdrav,
                %s
                """,
                prijava.getClan().getIme(),
                prijava.getClan().getPrezime(),
                prijava.getClan().getEmail(),
                prijava.getClan().getBrojTelefona(),
                nazivProjekta,
                nazivTima,
                prijava.getStatus(),
                appName
        );

        send(primaoci, subject, text);
    }

    public void posaljiKorisnikuStatusPrijave(Prijava prijava) {
        if (prijava.getClan() == null || prijava.getClan().getEmail() == null || prijava.getClan().getEmail().isBlank()) {
            LOGGER.info("Prijava {} nema email korisnika, preskacem slanje obavestenja.", prijava.getId());
            return;
        }

        String nazivProjekta = prijava.getTim() != null && prijava.getTim().getProjekat() != null
                ? prijava.getTim().getProjekat().getNaziv()
                : "Nepoznat projekat";
        String nazivTima = prijava.getTim() != null ? prijava.getTim().getNaziv() : "Nepoznat tim";
        boolean prihvacena = "Prihvacen".equalsIgnoreCase(prijava.getStatus());

        String subject = String.format(
                "[%s] Prijava %s: %s",
                appName,
                prihvacena ? "prihvacena" : "odbijena",
                nazivProjekta
        );
        String text = String.format(
                """
                Zdravo %s,

                Vasa prijava za projekat "%s" i tim "%s" je %s.

                Pozdrav,
                %s
                """,
                prijava.getClan().getIme(),
                nazivProjekta,
                nazivTima,
                prihvacena ? "prihvacena" : "odbijena",
                appName
        );

        send(List.of(prijava.getClan().getEmail()), subject, text);
    }

    private void send(Collection<String> recipients, String subject, String text) {
        if (!mailEnabled) {
            LOGGER.info("Slanje maila je iskljuceno. Subject: {}", subject);
            return;
        }

        List<String> cistiPrimaoci = recipients.stream()
                .filter(email -> email != null && !email.isBlank())
                .distinct()
                .collect(Collectors.toList());

        if (cistiPrimaoci.isEmpty()) {
            LOGGER.info("Nema validnih primalaca za slanje maila. Subject: {}", subject);
            return;
        }

        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

            if (!fromAddress.isBlank()) {
                InternetAddress adresa = displayName.isBlank()
                        ? new InternetAddress(fromAddress)
                        : new InternetAddress(fromAddress, displayName, StandardCharsets.UTF_8.name());
                helper.setFrom(adresa);
                helper.setReplyTo(adresa.toUnicodeString());
            }

            helper.setTo(cistiPrimaoci.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(text, false);
            mailSender.send(message);
        } catch (Exception ex) {
            LOGGER.warn("Neuspesno slanje email poruke. Subject: {}", subject, ex);
        }
    }

    private List<String> parseRecipients(String rawRecipients) {
        if (rawRecipients == null || rawRecipients.isBlank()) {
            return List.of();
        }

        return Arrays.stream(rawRecipients.split(","))
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
