package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String name;
    @Email
    private String email;
}
