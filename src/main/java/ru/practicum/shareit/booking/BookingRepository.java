package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * ALL
     */
    @Query("select b from Booking as b where b.item.owner.id = :userId order by b.start desc ")
    Page<Booking> findAllByOwnerId(Long userId, Pageable pageable);

    /**
     * PAST booking
     */
    @Query("select b from Booking as b where b.item.owner.id = :userId and b.end < :date " +
            "order by b.start desc ")
    List<Booking> getBookingByOwnerIdAndEndBefore(Long userId, LocalDateTime date);

    /**
     * FUTURE booking
     */
    @Query("select b from Booking as b where b.item.owner.id = :userId and b.start > :date " +
            "order by b.start desc ")
    List<Booking> getBookingByOwnerIdAndStartAfter(Long userId, LocalDateTime date);

    /**
     * CURRENT booking
     */
    @Query("select b from Booking as b where b.item.owner.id = :userId and b.start < :date and b.end > :date " +
            "order by b.start desc ")
    List<Booking> getBookingByOwnerIdAndStartIsBeforeAndEndAfter(Long userId, LocalDateTime date);

    /**
     * WAITING ожид.подтв. REJECTED отклонён
     */
    @Query("select b from Booking as b where b.item.owner.id = :userId and b.status = :status " +
            "order by b.start desc ")
    List<Booking> getBookingByOwner_IdAndStatus(Long userId, Status status);

    @Query("select b from Booking as b where b.item.id = :itemId ")
    List<Booking> findAllByItemId(Long itemId);

    @Query("select b from Booking as b where b.booker.id = :userId and b.end < :date " +
            "order by b.start desc ")
    List<Booking> getBookingForBookerAndEndBefore(Long userId, LocalDateTime date);

    @Query("select b from Booking as b where b.booker.id = :userId and b.start < :date and b.end > :date " +
            "order by b.start desc ")
    List<Booking> getBookingForBookerAndStartIsBeforeAndEndAfter(Long userId, LocalDateTime date);

    @Query("select b from Booking as b where b.booker.id = :userId and b.start > :date " +
            "order by b.start desc ")
    List<Booking> getBookingForBookerIdAndStartAfter(Long userId, LocalDateTime date);

    @Query("select b from Booking as b where b.booker.id = :userId order by b.start desc ")
    Page<Booking> findAllByBookerId(Long userId, Pageable pageable);

    @Query("select b from Booking as b where b.booker.id = :userId and b.status = :status " +
            "order by b.start desc ")
    List<Booking> getBookingForBookerAndStatus(Long userId, Status status);
}

