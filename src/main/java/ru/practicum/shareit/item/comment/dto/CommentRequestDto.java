package ru.practicum.shareit.item.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CommentRequestDto {
    @NotBlank
    private String text;
}
