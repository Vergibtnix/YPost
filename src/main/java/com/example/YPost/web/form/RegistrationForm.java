package com.example.YPost.web.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationForm {

    @NotBlank(message = "Benutzername ist erforderlich.")
    @Size(min = 3, max = 50, message = "Benutzername muss zwischen 3 und 50 Zeichen lang sein.")
    private String username;

    @NotBlank(message = "E-Mail ist erforderlich.")
    @Email(message = "Bitte eine gültige E-Mail-Adresse eingeben.")
    @Size(max = 120, message = "E-Mail darf maximal 120 Zeichen lang sein.")
    private String email;

    @NotBlank(message = "Passwort ist erforderlich.")
    @Size(min = 8, max = 100, message = "Passwort muss mindestens 8 Zeichen lang sein.")
    private String password;

    @NotBlank(message = "Passwort-Bestätigung ist erforderlich.")
    private String confirmPassword;

    @Size(max = 500, message = "Profilbeschreibung darf maximal 500 Zeichen lang sein.")
    private String bio;

    @AssertTrue(message = "Passwort und Passwort-Bestätigung müssen übereinstimmen.")
    public boolean isPasswordConfirmed() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}

