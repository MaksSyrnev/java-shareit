package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255)
    private String name;
    @Email
    @NotEmpty(groups = Marker.OnCreate.class)
    @Size(max = 512)
    private String email;
}
