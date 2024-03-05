package pl.dicedev.mappers;

import org.springframework.stereotype.Component;
import pl.dicedev.builders.ExpensesDtoBuilder;
import pl.dicedev.builders.ExpensesEntityBuilder;
import pl.dicedev.repositories.entities.ExpensesEntity;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.dtos.ExpensesDto;

@Component
public class ExpensesMapper {

    public ExpensesDto fromEntityToDto(ExpensesEntity entity) {
        return new ExpensesDtoBuilder()
                .withAmount(entity.getAmount())
                .withCategory(entity.getCategory())
                .withId(entity.getId())
                .withPurchaseDate(entity.getPurchaseDate())
                .build();
    }

    public ExpensesEntity fromDtoToEntity(ExpensesDto entity, UserEntity user) {
        return new ExpensesEntityBuilder()
                .withAmount(entity.getAmount())
                .withCategory(entity.getCategory())
                .withId(entity.getId())
                .withPurchaseDate(entity.getPurchaseDate())
                .withUser(user)
                .build();
    }

}
