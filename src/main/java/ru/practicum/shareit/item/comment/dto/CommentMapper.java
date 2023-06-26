package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.comment.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment fromDto(CommentRequestDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentResponseDto> toDto(Optional<List<Comment>> comments) {

        if (comments.isPresent()) {
            return comments.get().stream()
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }
}
