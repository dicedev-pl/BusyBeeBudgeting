package pl.dicedev.services;

import org.springframework.stereotype.Service;
import pl.dicedev.builders.ExpensesDtoBuilder;
import pl.dicedev.enums.FilterExpensesParametersEnum;
import pl.dicedev.enums.MonthsEnum;
import pl.dicedev.excetpions.MissingExpensesFilterException;
import pl.dicedev.mappers.ExpensesMapper;
import pl.dicedev.repositories.ExpensesRepository;
import pl.dicedev.repositories.entities.ExpensesEntity;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.dtos.ExpensesDto;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpensesService {

    private final ExpensesMapper expensesMapper;
    private final ExpensesRepository expensesRepository;
    private final UserLogInfoService userLogInfoService;

    public ExpensesService(ExpensesMapper expensesMapper, ExpensesRepository expensesRepository, UserLogInfoService userLogInfoService) {
        this.expensesMapper = expensesMapper;
        this.expensesRepository = expensesRepository;
        this.userLogInfoService = userLogInfoService;
    }

    public void updateExpenses(ExpensesDto expensesDto) {
        Optional<ExpensesEntity> entityOptional = expensesRepository.findById(expensesDto.getId());
        if (entityOptional.isPresent()) {
            ExpensesEntity entity = entityOptional.get();
            if (!entity.getAmount().equals(expensesDto.getAmount())) {
                entity.setAmount(expensesDto.getAmount());
            }
            if (!entity.getCategory().equals(expensesDto.getCategory())) {
                entity.setCategory(expensesDto.getCategory());
            }
            if (!entity.getPurchaseDate().equals(expensesDto.getPurchaseDate())) {
                entity.setPurchaseDate(expensesDto.getPurchaseDate());
            }
            expensesRepository.saveAndFlush(entity);
        }
    }

    public List<ExpensesDto> getAllExpenses() {
        List<ExpensesEntity> expensesRepositoryAllByUser = expensesRepository.findAllByUser(userLogInfoService.getLoggedUserEntity());
        return expensesRepositoryAllByUser.stream().map(expensesEntity -> new ExpensesDtoBuilder().withId(expensesEntity.getId()).withAmount(expensesEntity.getAmount()).withCategory(expensesEntity.getCategory()).withPurchaseDate(expensesEntity.getPurchaseDate()).build()).collect(Collectors.toList());
    }

    public List<ExpensesDto> getFilteredExpenses(Map<String, String> filters) {
        assertFilters(filters);
        if (filters.containsKey(FilterExpensesParametersEnum.DATE_TO.getKey())) {
            String dateFrom = filters.get(FilterExpensesParametersEnum.DATE_FORM.getKey());
            String dateTo = filters.get(FilterExpensesParametersEnum.DATE_TO.getKey());

            return getAllExpensesBetweenDate(dateFrom, dateTo);
        } else if (filters.containsKey(FilterExpensesParametersEnum.YEAR.getKey())) {
            String year = filters.get(FilterExpensesParametersEnum.YEAR.getKey());
            String month = filters.get(FilterExpensesParametersEnum.MONTH.getKey());

            return getAllExpensesForMonthInYear(year, month);
        }
        return Collections.emptyList();
    }

    private void assertFilters(Map<String, String> filters) {
        if (containsYearAndMonth(filters)) return;
        if (containsDateFormAndTo(filters)) return;
        if (containsYearAndNotMonth(filters))
            throw new MissingExpensesFilterException("month", "a3ea21c3-4a40-43c1-889b-c9e9c5c3bbb3");
        if (containsMonthAndNotYear(filters))
            throw new MissingExpensesFilterException("year", "a3ea21c3-4a40-43c1-889b-c9e9c5c3bbb3");
        if (containsDateToAndNotFrom(filters))
            throw new MissingExpensesFilterException("date from", "a3ea21c3-4a40-43c1-889b-c9e9c5c3bbb3");
        if (containsDateFromAndNotTo(filters))
            throw new MissingExpensesFilterException("date to", "a3ea21c3-4a40-43c1-889b-c9e9c5c3bbb3");
    }

    private static boolean containsYearAndNotMonth(Map<String, String> filters) {
        return !filters.containsKey(FilterExpensesParametersEnum.MONTH.getKey()) && filters.containsKey(FilterExpensesParametersEnum.YEAR.getKey());
    }

    private static boolean containsMonthAndNotYear(Map<String, String> filters) {
        return filters.containsKey(FilterExpensesParametersEnum.MONTH.getKey()) && !filters.containsKey(FilterExpensesParametersEnum.YEAR.getKey());
    }

    private static boolean containsYearAndMonth(Map<String, String> filters) {
        return filters.containsKey(FilterExpensesParametersEnum.MONTH.getKey()) && filters.containsKey(FilterExpensesParametersEnum.YEAR.getKey());
    }

    private static boolean containsDateFormAndTo(Map<String, String> filters) {
        return filters.containsKey(FilterExpensesParametersEnum.DATE_FORM.getKey()) && filters.containsKey(FilterExpensesParametersEnum.DATE_TO.getKey());
    }

    private static boolean containsDateFromAndNotTo(Map<String, String> filters) {
        return filters.containsKey(FilterExpensesParametersEnum.DATE_FORM.getKey()) && !filters.containsKey(FilterExpensesParametersEnum.DATE_TO.getKey());
    }

    private static boolean containsDateToAndNotFrom(Map<String, String> filters) {
        return !filters.containsKey(FilterExpensesParametersEnum.DATE_FORM.getKey()) && filters.containsKey(FilterExpensesParametersEnum.DATE_TO.getKey());
    }

    private List<ExpensesDto> getAllExpensesForMonthInYear(String year, String month) {
        String dateFrom = MonthsEnum.valueOf(month.toUpperCase()).getFirstDayForYear(year);
        String dateTo = MonthsEnum.valueOf(month.toUpperCase()).getLastDayForYear(year);
        return getAllExpensesBetweenDate(dateFrom, dateTo);
    }

    private List<ExpensesDto> getAllExpensesBetweenDate(String dateFrom, String dateTo) {
        UserEntity user = userLogInfoService.getLoggedUserEntity();
        String instantSuffix = "T00:00:00.001Z";

        List<ExpensesEntity> expensesEntities = expensesRepository.findAllByDate(Instant.parse(dateFrom + instantSuffix), Instant.parse(dateTo + instantSuffix), user);

        List<ExpensesDto> expensesDtos = expensesEntities.stream().map(expensesEntity -> expensesMapper.fromEntityToDto(expensesEntity)).collect(Collectors.toList());

        return expensesDtos;
    }

    public void deleteExpenses(ExpensesDto expensesDto) {
        expensesRepository.deleteById(expensesDto.getId());
    }

    public UUID setExpenses(ExpensesDto expensesDto) {
        UserEntity user = userLogInfoService.getLoggedUserEntity();
        ExpensesEntity expensesEntity = expensesMapper.fromDtoToEntity(expensesDto, user);
        ExpensesEntity savedEntity = expensesRepository.save(expensesEntity);
        return savedEntity.getId();
    }
}
