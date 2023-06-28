package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestor_IdOrderByCreatedDesc(Long requestorId);

    @Query(value = " SELECT i" +
            " FROM ItemRequest i" +
            " WHERE i.requestor.id NOT IN ?1 " +
            " ORDER BY i.created DESC")
    List<ItemRequest> findAllNotByRequestorAndSort(Long userId);
}
