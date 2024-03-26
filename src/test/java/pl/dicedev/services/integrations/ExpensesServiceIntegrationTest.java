package pl.dicedev.services.integrations;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.dicedev.builders.ExpensesDtoBuilder;
import pl.dicedev.builders.ExpensesEntityBuilder;
import pl.dicedev.enums.ExpensesCategory;
import pl.dicedev.enums.ExpensesExceptionErrorMessages;
import pl.dicedev.enums.FilterParametersCalendarEnum;
import pl.dicedev.excetpions.MissingExpensesFilterException;
import pl.dicedev.repositories.entities.ExpensesEntity;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.dtos.ExpensesDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpensesServiceIntegrationTest extends InitIntegrationTestData {

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

    @Test
    void shouldReturnAllExpensesSavedInDatabaseFilterDateFrom_To() {
        // given
        var fromDate = "2021-01-04";
        var toDate = "2021-01-10";
        var middleDate = "2021-01-08";
        var notInRangeDate = "2021-01-11";
        var user = initDefaultMockUserInDatabase();
        initDatabaseByExpenses(user, fromDate);
        initDatabaseByExpenses(user, toDate);
        initDatabaseByExpenses(user, middleDate);
        initDatabaseByExpenses(user, notInRangeDate);
        Map<String, String> filters = new HashMap<>();
        filters.put(FilterParametersCalendarEnum.DATE_FORM.getKey(), fromDate);
        filters.put(FilterParametersCalendarEnum.DATE_TO.getKey(), toDate);

        // when
        var result = expensesService.getFilteredExpenses(filters);

        // then
        assertThat(result).hasSize(3);
        var dateAsString = result.stream()
                .map(dto -> dto.getPurchaseDate().toString().substring(0, fromDate.length()))
                .collect(Collectors.toSet());
        assertThat(dateAsString)
                .contains(fromDate, toDate, middleDate)
                .doesNotContain(notInRangeDate);

    }

    @Test
    void shouldReturnAllExpensesSavedInDatabaseFilterYear_Month() {
        // given
        var fromDate = "2021-01-04";
        var toDate = "2021-01-10";
        var middleDate = "2021-01-08";
        var notInRangeDate = "2021-03-11";
        var user = initDefaultMockUserInDatabase();
        initDatabaseByExpenses(user, fromDate);
        initDatabaseByExpenses(user, toDate);
        initDatabaseByExpenses(user, middleDate);
        initDatabaseByExpenses(user, notInRangeDate);
        Map<String, String> filters = new HashMap<>();
        filters.put(FilterParametersCalendarEnum.MONTH.getKey(), "january");
        filters.put(FilterParametersCalendarEnum.YEAR.getKey(), "2021");

        // when
        var result = expensesService.getFilteredExpenses(filters);

        // then
        assertThat(result).hasSize(3);
        var dateAsString = result.stream()
                .map(dto -> dto.getPurchaseDate().toString().substring(0, fromDate.length()))
                .collect(Collectors.toSet());
        assertThat(dateAsString)
                .contains(fromDate, toDate, middleDate)
                .doesNotContain(notInRangeDate);

    }

    @ParameterizedTest(name = "missing filter key: {0} test ")
    @CsvSource({
            "month, year",
            "year, month",
            "date from, date_to",
            "date to, date_form"
    })
    void shouldThrowExceptionWhenOneOfTheFilterKeyIsMissing(String missingKey, String keyInFilter) {
        // given
        initDefaultMockUserInDatabase();
        String expectedErrorMessage = ExpensesExceptionErrorMessages.MISSING_FILTER_KEY.getMessage() + " " + missingKey;
        String filterKey = FilterParametersCalendarEnum.valueOf(keyInFilter.toUpperCase()).getKey();
        Map<String, String> filters = new HashMap<>();
        filters.put(filterKey, "fake value");

        // when
        var result = assertThrows(MissingExpensesFilterException.class,
                () -> expensesService.getFilteredExpenses(filters));

        // then
        assertThat(result.getMessage()).isEqualTo(expectedErrorMessage);
    }
}
