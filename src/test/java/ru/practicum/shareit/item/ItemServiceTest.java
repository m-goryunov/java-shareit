package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private final long id = 1L;
    private final User user = User.builder().id(id).name("User").email("user@mail.ru").build();
    private final User notOwner = User.builder().id(2L).name("User2").email("user2@mail.ru").build();
    private final Item itemDtoIn = Item.builder().name("item").description("cool item").available(true).build();
    private final ItemResponseDto itemDtoOut = ItemResponseDto.builder().id(id).name("item").description("cool item").available(true).requestId(0L).build();
    private final Item item = Item.builder().id(1L).name("item").description("cool item").available(true).owner(user).build();
    private final Comment commentDto = Comment.builder().id(id).text("abc").author(user)
            .created(LocalDateTime.of(2023, 7, 1, 12, 12, 12)).build();
    private final Comment comment = new Comment(id, "abc", item, user,
            LocalDateTime.of(2023, 7, 1, 12, 12, 12));
    private final Booking booking = new Booking(id, null, null, item, user, BookingStatus.WAITING);

    @Test
    void saveNewItem_whenUserFound_thenSavedItem() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemResponseDto actualItemDto = ItemMapper.toItemDto(itemService.createItem(itemDtoIn, id));

        Assertions.assertEquals(ItemMapper.toItemDto(item), actualItemDto);
        Assertions.assertNull(item.getRequest());
    }

    @Test
    void saveNewItem_whenUserNotFound_thenNotSavedItem() {
        when((userRepository).findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.createItem(itemDtoIn, 2L));
    }

    @Test
    void saveNewItem_whenNoName_thenNotSavedItem() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        doThrow(DataIntegrityViolationException.class).when(itemRepository).save(any(Item.class));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> itemService.createItem(itemDtoIn, id));
    }

    @Test
    void updateItem_whenUserIsOwner_thenUpdatedItem() {
//        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ItemResponseDto actualItemDto = ItemMapper.toItemDto(itemService.updateItemById(itemDtoIn, id, id));

        Assertions.assertEquals(itemDtoOut, actualItemDto);
    }

    @Test
    void updateItem_whenUserNotOwner_thenNotUpdatedItem() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(notOwner));
//        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.updateItemById(itemDtoIn, id, 2L));
    }

    @Test
    void getItemById_whenItemFound_thenReturnedItem() {
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByEndAsc(anyLong(), any(), any()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItemId(id)).thenReturn(List.of(comment));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        final ItemResponseDto itemDto = ItemMapper.toItemDtoWithBookings(item);
        itemDto.setLastBooking(BookingMapper.toBookingDto(Optional.of(booking)));
        itemDto.setNextBooking(BookingMapper.toBookingDto(Optional.of(booking)));
        itemDto.setComments(List.of(CommentMapper.toDto(comment)));

        ItemResponseDto actualItemDto = ItemMapper.toItemDtoWithBookings(itemService.getItemById(id, id));

        Assertions.assertEquals(itemDto, actualItemDto);
    }

    @Test
    void getItemById_whenItemNotFound_thenExceptionThrown() {
        when((itemRepository).findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(2L, id));
    }

    @Test
    void getItemsByOwner_CorrectArgumentsForPaging_thenReturnItems() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));

        List<ItemResponseDto> targetItems = ItemMapper.toItemDto(itemService.getAllItemsByUserId(id, 0, 10));

        Assertions.assertNotNull(targetItems);
        Assertions.assertEquals(1, targetItems.size());
        verify(itemRepository, times(1))
                .findAllByOwnerId(anyLong(), any());
    }

    @Test
    void getItemBySearch_whenTextNotBlank_thenReturnItems() {
        when(itemRepository.search(any(), any())).thenReturn(List.of(item));

        List<ItemResponseDto> targetItems = ItemMapper.toItemDto(itemService.searchItem("abc", 0, 10));

        Assertions.assertNotNull(targetItems);
        Assertions.assertEquals(1, targetItems.size());
        verify(itemRepository, times(1))
                .search(any(), any());
    }

    @Test
    void getItemBySearch_whenTextIsBlank_thenReturnEmptyList() {
        List<ItemResponseDto> targetItems = ItemMapper.toItemDto(itemService.searchItem("", 0, 10));

        Assertions.assertTrue(targetItems.isEmpty());
        Assertions.assertEquals(0, targetItems.size());
        verify(itemRepository, never()).search(any(), any());
    }

    @Test
    void saveNewComment_whenUserWasBooker_thenSavedComment() {
        when(bookingRepository.existsAllByItemIdAndEndIsBeforeAndBooker_IdEquals(anyLong(), any(), any()))
                .thenReturn(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(commentRepository.save(any())).thenReturn(comment);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Comment actualComment = itemService.createComment(Comment.builder().text("abc").build(), id, id);

        Assertions.assertEquals(commentDto, actualComment);
    }

    @Test
    void saveNewComment_whenUserWasNotBooker_thenThrownException() {
/*        when((bookingRepository).existsAllByItemIdAndEndIsBeforeAndBooker_IdEquals(2L, LocalDateTime.now(),1L))
                .thenReturn(false);*/
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                itemService.createComment(Comment.builder().text("abc").build(), id, 2L));
    }
}