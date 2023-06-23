package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public Item createItem(Item item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItemById(Item itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Не совпадают владельцы вещей.", getClass().toString());
        }

        if (StringUtils.hasText(itemDto.getName())) {
            item.setName(itemDto.getName());
        }

        if (StringUtils.hasText(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }

        if ((itemDto.getAvailable() != null)) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemRepository.save(item);
    }

    @Override
    public ItemResponseDto getItemById(Long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена.", getClass().toString()));

        ItemResponseDto itemResponseDto = ItemMapper.toItemDto(item);

        if (Objects.equals(userId, item.getOwner().getId())) {
            itemResponseDto.setNextBooking(
                    (bookingRepository
                            .findNextBooking(itemId, userId, Status.APPROVED, LocalDateTime.now(),
                                    PageRequest.of(0, 1)))
                            .get().findFirst().orElse(null));

            itemResponseDto.setLastBooking(
                    (bookingRepository
                            .findLastBooking(itemId, userId, Status.APPROVED, LocalDateTime.now(),
                                    PageRequest.of(0, 1)))
                            .get().findFirst().orElse(null));
        }

        List<CommentResponse> comments = commentService.findAllByItemId(itemId);
        itemResponseDto.setComments(comments);

        return
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String text) {
        if (!StringUtils.hasLength(text)) {
            return List.of();
        }
        return itemRepository.search(text);
    }

    @Override
    //@Transactional
    public Comment createComment(CommentRequestDto request, Long userId, Long itemId) {
        Comment comment = CommentMapper.dtoToObject(request);

        User author = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь не найдена: '%s' ", itemId), getClass().toString()));

        List<Booking> bookings = bookingRepository.findByItemIdAndEndIsBefore(itemId, comment.getCreated())
                .stream()
                .filter(booking -> Objects.equals(booking.getBooker().getId(), userId))
                .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new IllegalArgumentException(String.format("Невозможно оставить отзыв пользователю: '%s'",
                    userId));
        }

        comment.setAuthor(author);
        comment.setItem(item);

        return commentRepository.save(comment);
    }
}
