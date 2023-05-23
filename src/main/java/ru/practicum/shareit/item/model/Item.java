package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {

    private long id;
    private String name;
    private String description;
    private boolean available;
    private String owner;
    private long request; //ссылка на соответствующий запрос

}
