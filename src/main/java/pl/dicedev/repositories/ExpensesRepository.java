package pl.dicedev.repositories;

import pl.dicedev.repositories.entities.ExpensesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.dtos.ExpensesDto;

import java.util.List;
import java.util.UUID;

public interface ExpensesRepository extends JpaRepository<ExpensesEntity, UUID> {
    List<ExpensesEntity> findAllByUser(UserEntity user);
}
