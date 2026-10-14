# Aplikacija za upravljanje projektima

Web aplikacija za upravljanje projektima, timovima i zadacima unutar organizacije. Članovi se prijavljuju za rad u timovima na projektima, rukovodioci im dodeljuju zadatke, a za urađene zadatke dobijaju bodove koji se vide na rang-listi.

Projekat je rađen u okviru diplomskog rada (predmet: Napredne Java tehnologije).

## Tehnologije

| Sloj | Tehnologije |
|------|-------------|
| Backend | Java 17, Spring Boot 3.2 (Web, Data JPA, Security, Validation, Mail), JWT (jjwt) |
| Baza | MySQL |
| Frontend | Angular 20, TypeScript |
| Testovi | JUnit 5, Mockito |

## Funkcionalnosti

Aplikacija ima tri uloge:

**Član organizacije**
- registracija i prijava (JWT autentifikacija)
- pregled projekata i prijava za rad u timu na projektu
- pregled dodeljenih zadataka i promena njihovog statusa (`Dodeljen`, `Uradjen`, `Nije uradjen`)
- rang-lista članova po bodovima

**Rukovodilac**
- sve što i član
- kreiranje zadataka i dodela članovima
- pregled zadataka koje je kreirao

**Administrator**
- upravljanje projektima, timovima i članovima (dodavanje, izmena, brisanje)
- odobravanje i odbijanje prijava za timove
- povezivanje članova sa timovima

**Bodovanje:** urađen zadatak donosi +10 bodova, a neurađen -3.

**Email obaveštenja** (opciono): administrator dobija mail o novoj prijavi, član o odobrenoj ili odbijenoj prijavi i o novom zadatku.

## Struktura projekta

```
app/
├── src/main/java/projekat/
│   ├── kontroler/     REST kontroleri (/api/...)
│   ├── servis/        poslovna logika
│   ├── repository/    Spring Data JPA repozitorijumi
│   ├── model/         JPA entiteti
│   ├── DTO/           objekti za prenos podataka
│   ├── Mapper/        mapiranje entitet <-> DTO
│   ├── security/      JWT filter, servis i uloge
│   └── config/        Security i CORS konfiguracija
├── src/main/resources/application.properties
├── src/test/          unit testovi servisa
├── local/             lokalna mail konfiguracija (nije u git-u)
└── njtfront/          Angular frontend
```

## Pokretanje

### Preduslovi

- JDK 17+
- Maven
- MySQL 8
- Node.js 20+ i npm

### 1. Baza podataka

Napravi praznu bazu (tabele se kreiraju automatski pri pokretanju):

```sql
CREATE DATABASE njt_up;
```

Podrazumevano se aplikacija povezuje kao `root` bez lozinke na `localhost:3306`. Ako je tvoja konfiguracija drugačija, izmeni `spring.datasource.*` u [application.properties](app/src/main/resources/application.properties).

### 2. Backend

```bash
cd app
mvn spring-boot:run
```

Backend radi na `http://localhost:8080`.

### 3. Frontend

```bash
cd app/njtfront
npm install
npm start
```

Frontend radi na `http://localhost:4200`.

### 4. Prvi administrator

Registruj se kroz aplikaciju, pa tom nalogu dodeli admin ulogu direktno u bazi:

```sql
UPDATE clan_organizacije SET admin = 1 WHERE email = 'tvoj@email.com';
```

Posle toga administrator iz aplikacije može da dodeljuje ulogu rukovodioca ostalim članovima.

## Email obaveštenja (opciono)

Slanje mailova je podrazumevano isključeno. Za uključivanje preko Gmail-a:

1. Napravi [Gmail App Password](https://myaccount.google.com/apppasswords).
2. Pokreni skriptu koja pravi `app/local/application-mail.properties`:
   ```powershell
   cd app
   .\setup-mail-config.ps1
   ```
   Ili ručno kopiraj [application-mail.properties.example](app/local/application-mail.properties.example) u `app/local/application-mail.properties` i popuni podatke.

Fajl `application-mail.properties` je u `.gitignore`, pa lozinka ne završava u repozitorijumu.

## Testovi

```bash
cd app
mvn test
```
