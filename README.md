# yPosts (Twitter-Klon)

`yPosts` ist eine Spring-Boot-Webanwendung (MVC) fuer die LAP-Vorbereitung.
Benutzer koennen sich registrieren, einloggen, Posts erstellen, den Feed lesen, liken/unliken und Profile ansehen.

## Features

- Registrierung mit:
  - eindeutigem Benutzername
  - eindeutiger E-Mail
  - Passwort + Passwort-Bestaetigung
  - optionaler Profilbeschreibung
- Login mit Benutzername **oder** E-Mail
- Sichere Passwortspeicherung mit `bcrypt`
- Feed mit allen Posts:
  - Standard: neueste zuerst
  - alternativ: meiste Likes zuerst
- Post-Erstellung mit Validierung:
  - nicht leer
  - max. 420 Zeichen
- Like/Unlike:
  - nur 1 Like pro Benutzer und Post
  - Duplikate durch DB-Constraint verhindert
  - eigene Posts koennen nicht geliked werden
- Profilseite:
  - Benutzername, Bio
  - Anzahl eigener Posts
  - Gesamtanzahl erhaltener Likes
  - alle Posts des Benutzers (neueste zuerst)
- Testdaten (Seed):
  - 2 Benutzer
  - 10 Posts (je 5)
  - 10 Likes verteilt

## Technologien und Versionen

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Security
- Spring Data JPA / Hibernate
- Thymeleaf
- PostgreSQL 16 (Docker)
- Maven Wrapper (`mvnw`)
- H2 (nur Tests)

## Projektstruktur

- `src/main/java/com/example/YPost/model` - JPA-Entities (`User`, `Post`, `PostLike`)
- `src/main/java/com/example/YPost/repository` - Repositories
- `src/main/java/com/example/YPost/service` - Geschaeftslogik
- `src/main/java/com/example/YPost/web/controller` - MVC-Controller
- `src/main/resources/templates` - Thymeleaf-Templates
- `src/main/resources/static/css` - Styling

## Datenbankschema (vereinfacht)

### Tabelle `users`

- `id` (PK)
- `username` (UNIQUE, NOT NULL)
- `email` (UNIQUE, NOT NULL)
- `password_hash` (NOT NULL)
- `bio` (optional)
- `role` (NOT NULL, default `ROLE_USER`)
- `created_at` (NOT NULL)

### Tabelle `posts`

- `id` (PK)
- `content` (NOT NULL, max 420)
- `created_at` (NOT NULL)
- `author_id` (FK -> `users.id`, NOT NULL)

### Tabelle `post_likes`

- `id` (PK)
- `post_id` (FK -> `posts.id`, NOT NULL)
- `user_id` (FK -> `users.id`, NOT NULL)
- `created_at` (NOT NULL)
- Unique-Constraint auf (`post_id`, `user_id`)

## Start mit Docker + PostgreSQL

1. PostgreSQL starten:

```powershell
docker compose up -d
```

2. Anwendung starten:

```powershell
.\mvnw.cmd spring-boot:run
```

3. App im Browser oeffnen:

- `http://localhost:8080`

Hinweis: Die App verbindet sich auf `localhost:5433`, um Konflikte mit einer lokal installierten PostgreSQL-Instanz auf `5432` zu vermeiden.

## Demo-Benutzer (Seed-Daten)

- `alice` / `Password123!`
- `bob` / `Password123!`

## Tests

Die Tests laufen mit dem Profil `test` und H2-In-Memory-DB.

```powershell
.\mvnw.cmd test
```

## Troubleshooting

Wenn beim Start `Passwort-Authentifizierung fuer Benutzer 'yposts' fehlgeschlagen` erscheint:

```powershell
docker compose down -v
docker compose up -d
```

Danach die App erneut starten.

## Hinweise

- Produktivprofil nutzt PostgreSQL laut `src/main/resources/application.properties`.
- Testprofil nutzt H2 laut `src/test/resources/application-test.properties`.
- Seed-Daten koennen ueber `app.seed-data` gesteuert werden.
