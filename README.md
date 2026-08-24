# X - Klon (Twitter-Klon)

`X - Klon` ist eine Spring-Boot-Webanwendung (MVC) fuer die LAP-Vorbereitung.  
Benutzer koennen sich registrieren, einloggen, Posts erstellen, den Feed lesen, liken/unliken, Profile ansehen, Benutzer suchen und im Dark‑Mode surfen.

---

## Features

### 🔐 Registrierung
- eindeutiger Benutzername
- eindeutige E-Mail
- Passwort + Passwort-Bestaetigung
- optionale Profilbeschreibung
- sichere Speicherung mit `bcrypt`

### 🔑 Login
- Login mit **Benutzername oder E-Mail**
- Spring Security mit Rollenverwaltung (`ROLE_USER`)

### 📰 Feed
- Anzeige aller Posts
- Sortierung:
  - **Neueste zuerst** (Standard)
  - **Meiste Likes zuerst**
- **Pagination**:
  - Seitenanzeige
  - Weiter/Zurück‑Navigation

### ✍️ Posts
- Erstellung mit Validierung:
  - nicht leer
  - max. 420 Zeichen
- Bearbeiten eigener Posts
- Löschen eigener Posts

### ❤️ Likes & 👎 Dislikes
- pro Benutzer nur ein Like/Dislike pro Post
- eigene Posts können nicht geliked werden
- Like‑ und Dislike‑Zähler
- DB‑Constraints verhindern Duplikate

### 👤 Profilseite
- Benutzername, Bio
- Anzahl eigener Posts
- Gesamtanzahl erhaltener Likes
- alle Posts des Benutzers (neueste zuerst)
- **Pagination für Profil‑Posts**
- Dark‑Mode‑Toggle (🌙)

### 🔍 Benutzer-Suche
- Suche nach Benutzernamen
- bei **exaktem Treffer → automatische Weiterleitung zum Profil**
- bei mehreren Treffern → Ergebnisliste

### 🌙 Dark‑Mode
- globaler Toggle
- Speicherung im LocalStorage

### 🧪 Seed‑Daten
- 2 Benutzer (`alice`, `bob`)
- 10 Posts (je 5)
- 10 Likes verteilt

---

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

---

## Projektstruktur

- `src/main/java/com/example/YPost/model` – JPA-Entities (`User`, `Post`, `PostLike`)
- `src/main/java/com/example/YPost/repository` – Repositories
- `src/main/java/com/example/YPost/service` – Geschäftslogik
- `src/main/java/com/example/YPost/web/controller` – MVC-Controller
- `src/main/resources/templates` – Thymeleaf-Templates
- `src/main/resources/static/css` – Styling

---

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
- `author_id` (FK → `users.id`, NOT NULL)

### Tabelle `post_likes`
- `id` (PK)
- `post_id` (FK → `posts.id`, NOT NULL)
- `user_id` (FK → `users.id`, NOT NULL)
- `created_at` (NOT NULL)
- UNIQUE (`post_id`, `user_id`)

---

## Start mit Docker + PostgreSQL

### 1. PostgreSQL starten


```powershell
docker compose up -d
```

### Anwendung starten

```powershell
.\mvnw.cmd spring-boot:run
```

### App im Browser öffnen
```powershell
https://localhost:8080
```

## Demo User
```Powershell
alice / Password123!
bob / Password123!
```


