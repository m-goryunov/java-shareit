package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME")
    @NotBlank
    private String name;
    @Column(name = "EMAIL", unique = true)
    @Email(regexp = "[\\w._]{1,10}@[\\w]{2,}.[\\w]{2,}")
    @NotEmpty
    private String email;
}
