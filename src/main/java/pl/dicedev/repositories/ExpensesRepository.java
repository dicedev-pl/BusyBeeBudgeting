package pl.dicedev.repositories;

import pl.dicedev.repositories.entities.ExpensesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.dicedev.repositories.entities.UserEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ExpensesRepository extends JpaRepository<ExpensesEntity, UUID> {
    List<ExpensesEntity> findAllByUser(UserEntity user);

    @Query("SELECT e FROM ExpensesEntity e WHERE e.user = :user AND e.purchaseDate >= :fromDate AND e.purchaseDate <= :toDate")
    List<ExpensesEntity> findAllByDate(Instant fromDate, Instant toDate, UserEntity user);
}
