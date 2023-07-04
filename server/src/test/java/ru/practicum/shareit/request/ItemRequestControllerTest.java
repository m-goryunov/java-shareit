package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final User requestor = User.builder()
            .id(2L)
            .name("user")
            .email("email@email.com")
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("description")
            .created(LocalDateTime.of(2023, 7, 1, 12, 12, 12))
            .requestor(requestor)
            .build();


    @Test
    void saveNewRequest() throws Exception {
        when(requestService.createItemRequest(any(), anyLong())).thenReturn(request);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(ItemRequestMapper.toDto(request)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(ItemRequestMapper.toDto(request))))
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription()), String.class));
    }

    @Test
    void getRequestsByRequestor() throws Exception {
        when(requestService.getAllOwnedRequestsWithResponses(anyLong())).thenReturn(List.of(request));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(ItemRequestMapper.toDto(request)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(ItemRequestMapper.toDto(request)))));
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestService.getAllAvailableItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(request));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(ItemRequestMapper.toDto(request)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(ItemRequestMapper.toDto(request)))));
    }

    @Test
    void getAllRequests_whenBadPagingArguments_thenThrownException() throws Exception {
        mvc.perform(get("/requests/all?from=-5&size=-1")
                        .content(mapper.writeValueAsString(ItemRequestMapper.toDto(request)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestById() throws Exception {
        when(requestService.getItemRequestByIdWithResponses(anyLong(), anyLong())).thenReturn(request);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(ItemRequestMapper.toDto(request)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(ItemRequestMapper.toDto(request))));
    }
}