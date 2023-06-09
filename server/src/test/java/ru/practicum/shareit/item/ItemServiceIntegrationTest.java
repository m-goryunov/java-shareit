package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.username=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceIntegrationTest {

    private final EntityManager entityManager;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void saveNewItem() {
        final User userDto = User.builder().name("user").email("user@mail.ru").build();
        final Item itemDtoIn = Item.builder().id(1L).name("item").description("cool").available(true).owner(userDto).build();

        User user = userService.createUser(userDto);
        itemService.createItem(itemDtoIn, user.getId());

        TypedQuery<Item> queryItem = entityManager.createQuery("Select i from Item i where i.name like :item", Item.class);
        Item item = queryItem.setParameter("item", itemDtoIn.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDtoIn.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoIn.getDescription()));
    }
}