package com.example.YPost.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostForm {

    @NotBlank(message = "Post darf nicht leer sein.")
    @Size(max = 420, message = "Ein Post darf maximal 420 Zeichen lang sein.")
    private String content;
}

