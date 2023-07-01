package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    @Transactional
    public Item createItem(Item item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));

        if (item.getRequest() != null && item.getRequest().getId() != 0) {
            item.setRequest(itemRequestRepository.findById(item.getRequest().getId()).orElse(null));
        } else {
            item.setRequest(null);
        }

        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
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

        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItemById(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена.", getClass().toString()));

        return setLastAndNextBookingAndComments(item, userId);
    }

    public Item setLastAndNextBookingAndComments(Item item, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        if (Objects.equals(userId, item.getOwner().getId())) {
            item.setLastBooking(bookingRepository
                    .findFirstByItemIdAndStartLessThanEqualAndStatusOrderByEndDesc(item.getId(), now, BookingStatus.APPROVED)
                    .orElse(null));

            item.setNextBooking(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByEndAsc(item.getId(), now, BookingStatus.APPROVED)
                    .orElse(null));
        }

        item.setComments(new ArrayList<>(commentRepository.findAllByItemId(item.getId())));

        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getAllItemsByUserId(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не найден", getClass().toString()));

        Pageable pageable = getPageable(from, size);

        return setAllLastAndNextBookingAndComments(itemRepository.findAllByOwnerId(userId, pageable), pageable);
    }

    private List<Item> setAllLastAndNextBookingAndComments(List<Item> items, Pageable pageable) {
        final LocalDateTime now = LocalDateTime.now();

        Map<Item, Booking> lastBookings = bookingRepository
                .findByItemInAndStartLessThanEqualAndStatusOrderByEndDesc(items, now,
                        BookingStatus.APPROVED, pageable)
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (o1, o2) -> o1));

        Map<Item, Booking> nextBookings = bookingRepository
                .findByItemInAndStartAfterAndStatusOrderByEndAsc(items, now,
                        BookingStatus.APPROVED, pageable)
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (o1, o2) -> o1));

        Map<Item, List<Comment>> itemsWithComments = commentRepository
                .findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            Booking lastBooking = lastBookings.get(item);
            if (lastBookings.size() > 0 && lastBooking != null) {
                item.setLastBooking(lastBooking);
            }
            Booking nextBooking = nextBookings.get(item);
            if (nextBookings.size() > 0 && nextBooking != null) {
                item.setNextBooking(nextBooking);
            }

            List<Comment> comments = new ArrayList<>(itemsWithComments.getOrDefault(item, List.of()));
            item.setComments(comments);

            result.add(item);
        }
        return result;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Item> searchItem(String text, Integer from, Integer size) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }

        Pageable pageable = getPageable(from, size);

        return itemRepository.search(text, pageable);
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment, Long userId, Long itemId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь не найден: '%s' ", itemId), getClass().toString()));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Вещь не найдена: '%s' ", itemId), getClass().toString()));

        Boolean bookings = bookingRepository.existsAllByItemIdAndEndIsBeforeAndBooker_IdEquals(itemId, comment.getCreated(), userId);

        if (!bookings) {
            throw new IncorrectRequestException(String.format("Невозможно оставить отзыв пользователю: '%s'",
                    userId), getClass().toString());
        }

        comment.setAuthor(user);
        comment.setItem(item);

        return commentRepository.save(comment);
    }

    private Pageable getPageable(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new IncorrectRequestException("Переданные from/size невалидны.", getClass().getName());
        }

        int page = from == 0 ? 0 : (from / size);
        return PageRequest.of(page, size);
    }
}
