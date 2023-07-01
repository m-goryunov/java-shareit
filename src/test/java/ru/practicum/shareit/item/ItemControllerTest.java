package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = UserDto.builder().id(1L).name("User").build();


    private final ItemRequestDto itemDtoOut = ItemRequestDto.builder().id(1L).name("item")
            .description("cool item").available(true).build();

    private final Item item = Item.builder().id(1L).name("item")
            .description("cool item").available(true).build();

    private final ItemRequestDto itemBlankName = ItemRequestDto.builder().id(1L).name("")
            .description("cool item").available(true).build();


    @Test
    void saveNewItem() throws Exception {
        when(itemService.createItem(any(), anyLong())).thenReturn(ItemMapper.fromItemDto(itemDtoOut));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)))
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable()), Boolean.class));
    }

    @Test
    void saveNewItem_whenBlankName_thenThrownException() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemBlankName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItemById(any(), anyLong(), anyLong())).thenReturn(item);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)))
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable()), Boolean.class));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));
    }

    @Test
    void getItemsByOwner() throws Exception {
        when(itemService.getAllItemsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(item));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item))));
    }

    @Test
    void getFilmBySearch() throws Exception {
        when(itemService.searchItem(any(), any(), any())).thenReturn(List.of(item));

        mvc.perform(get("/items/search?text=a&from=0&size=4")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item))));
    }

    @Test
    void saveNewComment() throws Exception {


        final Comment commentDtoOut = Comment.builder().id(1L).text("comment")
                .author(User.builder().name("name").build()).created(LocalDateTime.now()).build();

        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentDtoOut);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDtoOut)))
                .andExpect(jsonPath("$.id", is(commentDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoOut.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoOut.getAuthor().getName())));
    }
}