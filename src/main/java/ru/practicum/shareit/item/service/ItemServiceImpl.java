package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
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
    public ItemResponseDto getItemById(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена.", getClass().toString()));

        ItemResponseDto itemResponseDto = ItemMapper.toItemDto(item);

        if (Objects.equals(userId, item.getOwner().getId())) {
            itemResponseDto.setNextBooking(
                    (bookingRepository
                            .findNextBooking(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now(),
                                    PageRequest.of(0, 1)))
                            .get().findFirst().orElse(null));

            itemResponseDto.setLastBooking(
                    (bookingRepository
                            .findLastBooking(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now(),
                                    PageRequest.of(0, 1)))
                            .get().findFirst().orElse(null));
        }

        List<CommentResponseDto> comments = CommentMapper.toDto(commentRepository.findAllByItemId(itemId));
        itemResponseDto.setComments(comments);

        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> getAllItemsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не найден", getClass().toString()));

        List<ItemResponseDto> response = ItemMapper.toItemDto(itemRepository.findAllByOwnerIdOrderByIdAsc(userId));

        return response
                .stream()
                .peek(itemsDto -> {
                    itemsDto.setNextBooking(
                            (bookingRepository
                                    .findNextBooking(itemsDto.getId(), userId, BookingStatus.APPROVED, LocalDateTime.now(),
                                            PageRequest.of(0, 1)))
                                    .get().findFirst().orElse(null));

                    itemsDto.setLastBooking(
                            (bookingRepository
                                    .findLastBooking(itemsDto.getId(), userId, BookingStatus.APPROVED, LocalDateTime.now(),
                                            PageRequest.of(0, 1)))
                                    .get().findFirst().orElse(null));
                })
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
    public Comment createComment(CommentRequestDto request, Long userId, Long itemId) {
        Comment comment = CommentMapper.toDto(request);

        User author = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Вещь не найдена: '%s' ", itemId), getClass().toString()));

        List<Booking> bookings = bookingRepository.findByItemIdAndEndIsBefore(itemId, comment.getCreated())
                .stream()
                .filter(booking -> Objects.equals(booking.getBooker().getId(), userId))
                .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new IncorrectRequestException(String.format("Невозможно оставить отзыв пользователю: '%s'",
                    userId), getClass().toString());
        }

        comment.setAuthor(author);
        comment.setItem(item);

        return commentRepository.save(comment);
    }
}
