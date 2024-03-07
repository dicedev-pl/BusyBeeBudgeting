package pl.dicedev.services.integrations;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.dicedev.builders.ExpensesDtoBuilder;
import pl.dicedev.builders.ExpensesEntityBuilder;
import pl.dicedev.enums.ExpensesCategory;
import pl.dicedev.repositories.ExpensesRepository;
import pl.dicedev.repositories.UserRepository;
import pl.dicedev.repositories.entities.ExpensesEntity;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.ExpensesService;
import pl.dicedev.services.dtos.ExpensesDto;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@WithMockUser(username = "user123", password = "123user")
public class ExpensesServiceIntegrationTest {

    @Autowired
    private ExpensesRepository expensesRepository;
    @Autowired
    private ExpensesService expensesService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveOneExpensesInToDatabase() {
        // given
        initDefaultMockUserInDatabase();
        ExpensesDto expensesDto = new ExpensesDtoBuilder()
                .withPurchaseDate(Instant.now())
                .withCategory(ExpensesCategory.EDUCATION)
                .withAmount(BigDecimal.ONE)
                .build();

        // when
        expensesService.setExpenses(expensesDto);

        // then
        var allExpensesInDB = expensesRepository.findAll();
        assertThat(allExpensesInDB).hasSize(1);
        var entity = allExpensesInDB.get(0);
        assertThat(entity.getCategory()).isEqualTo(expensesDto.getCategory());
        assertThat(entity.getAmount()).isEqualTo(expensesDto.getAmount());
        AssertionsForClassTypes.assertThat(entity.getPurchaseDate()).isEqualTo(expensesDto.getPurchaseDate());


    }

    @Test
    void shouldDeleteExpensesFromDatabase() {
        // given
        UserEntity user = initDefaultMockUserInDatabase();
        ExpensesEntity expensesEntity = new ExpensesEntityBuilder()
                .withUser(user)
                .withAmount(BigDecimal.TEN)
                .withCategory(ExpensesCategory.OTHERS)
                .withPurchaseDate(Instant.now())
                .build();
        expensesRepository.save(expensesEntity);
        List<ExpensesEntity> all = expensesRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
        UUID id = all.get(0).getId();

        // when
        expensesService.deleteExpenses(new ExpensesDtoBuilder().withId(id).build());

        // then
        List<ExpensesEntity> allAfterDeleting = expensesRepository.findAll();
        assertThat(allAfterDeleting.size()).isEqualTo(0);

    }

    @Test
    void shouldUpdateExpensesInDatabase() {
        // given
        UserEntity user = initDefaultMockUserInDatabase();
        ExpensesEntity expensesEntity = new ExpensesEntityBuilder()
                .withUser(user)
                .withAmount(BigDecimal.TEN)
                .withCategory(ExpensesCategory.OTHERS)
                .withPurchaseDate(Instant.now())
                .build();
        expensesRepository.save(expensesEntity);
        List<ExpensesEntity> all = expensesRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
        ExpensesEntity entity = all.get(0);
        ExpensesDto dtoTuUpdate = new ExpensesDtoBuilder()
                .withId(entity.getId())
                .withAmount(BigDecimal.ONE)
                .withCategory(ExpensesCategory.FUN)
                .withPurchaseDate(entity.getPurchaseDate())
                .build();

        // when
        expensesService.updateExpenses(dtoTuUpdate);

        // then
        ExpensesEntity afterUpdating = expensesRepository.findById(entity.getId()).get();
        assertThat(afterUpdating.getAmount()).isEqualTo(BigDecimal.ONE);
        assertThat(afterUpdating.getCategory()).isEqualTo(ExpensesCategory.FUN);
    }

    @Test
    void shouldReturnAllExpensesSavedInDatabase() {
        // given
        initDefaultMockUserInDatabase();
        ExpensesDto expensesDto = new ExpensesDtoBuilder()
                .withAmount(BigDecimal.TEN)
                .withCategory(ExpensesCategory.OTHERS)
                .withPurchaseDate(Instant.now())
                .build();
        expensesService.setExpenses(expensesDto);
        expensesService.setExpenses(expensesDto);

        // when
        List<ExpensesDto> allExpenses = expensesService.getAllExpenses();

        // then
        assertThat(allExpenses.size()).isEqualTo(2);

    }

    private UserEntity initDefaultMockUserInDatabase() {
        var user = new UserEntity();
        user.setUsername("user123");
        user.setPassword("123user");

        return userRepository.save(user);
    }
}
