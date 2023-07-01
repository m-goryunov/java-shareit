package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private final User user = User.builder().name("user").email("user@mail.ru").build();
    private final User requestor = User.builder().name("user2").email("user2@mail.ru").build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requestor(requestor)
            .created(LocalDateTime.now())
            .build();

    private final ItemRequest request2 = ItemRequest.builder()
            .id(2L)
            .description("2")
            .requestor(user)
            .created(LocalDateTime.now())
            .build();


    private final Item item = Item.builder().name("item").description("cool").available(true).owner(user)
            .request(request)
            .build();
    private final Item item2 = Item.builder().name("i2").description("2").available(true).owner(requestor)
            .request(request2)
            .build();


    @Test
    void saveNewRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.save(any())).thenReturn(request);

        final ItemRequest actualRequest = requestService.createItemRequest(
                ItemRequest.builder().description("description").build(), 2L);

        Assertions.assertEquals(request, actualRequest);
    }

    @Test
    void getRequestsByRequestor_whenUserFound_thenSavedRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.findAllByRequestorId(anyLong())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        final ItemRequestDtoOut requestDtoOut = ItemRequestMapper.toDto(request);
        requestDtoOut.setItems(List.of(ItemMapper.toItemDto(item)));

        List<ItemRequestDtoOut> actualRequests = ItemRequestMapper
                .toDto(requestService.getAllOwnedRequestsWithResponses(2L));

        Assertions.assertEquals(List.of(requestDtoOut), actualRequests);
    }

    @Test
    void getRequestsByRequestor_whenUserNotFound_thenThrownException() {
        when((userRepository).findById(3L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                requestService.getAllOwnedRequestsWithResponses(3L));
    }

    @Test
    void getAllRequests_whenCorrectPageArguments_thenReturnRequests() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.findAllByRequestorIdIsNot(anyLong(), any())).thenReturn(List.of(request2));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item2));

        final ItemRequestDtoOut requestDtoOut = ItemRequestMapper.toDto(request2);
        requestDtoOut.setItems(List.of(ItemMapper.toItemDto(item2)));

        List<ItemRequestDtoOut> actualRequests = ItemRequestMapper
                .toDto(requestService.getAllAvailableItemRequests(2L, 0, 10));

        Assertions.assertEquals(List.of(requestDtoOut), actualRequests);
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        final ItemRequestDtoOut requestDto = ItemRequestMapper.toDto(request);
        requestDto.setItems(List.of(ItemMapper.toItemDto(item)));

        ItemRequestDtoOut actualRequest = ItemRequestMapper
                .toDto(requestService.getItemRequestByIdWithResponses(1L, 1L));

        Assertions.assertEquals(requestDto, actualRequest);
    }
}