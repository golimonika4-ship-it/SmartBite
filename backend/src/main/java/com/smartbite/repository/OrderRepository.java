package com.smartbite.repository;

import com.smartbite.model.Order;
import com.smartbite.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByPickupToken(String pickupToken);

    boolean existsByPickupToken(String pickupToken);

    List<Order> findByCustomerIdentifierOrderByCreatedAtDesc(String customerIdentifier);

    List<Order> findByCustomerIdentifierIgnoreCaseOrderByCreatedAtDesc(String customerIdentifier);

    List<Order> findByUserOrderByCreatedAtDesc(com.smartbite.model.User user);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findAllByOrderByCreatedAtDesc();

    long countByStatus(OrderStatus status);

    long countByCreatedAtGreaterThanEqual(Instant since);

    List<Order> findByTableIdOrderByCreatedAtDesc(String tableId);

    @Query("SELECT COALESCE(SUM(o.grandTotal), 0.0) FROM Order o WHERE o.createdAt >= :since")
    Double sumGrandTotalSince(@Param("since") Instant since);

    @Query("SELECT COALESCE(SUM(o.grandTotal), 0.0) FROM Order o WHERE o.status != com.smartbite.model.OrderStatus.CANCELLED")
    Double sumTotalSales();
}
