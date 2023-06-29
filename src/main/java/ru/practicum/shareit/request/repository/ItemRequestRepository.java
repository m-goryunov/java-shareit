package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(Long requestorId);

    @Query("SELECT i FROM ItemRequest i WHERE i.requestor.id <> ?1")
    List<ItemRequest> findAllByOwnerId(Long userId, Pageable pageable);
}
