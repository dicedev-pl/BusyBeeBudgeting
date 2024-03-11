package pl.dicedev.services;

import org.springframework.stereotype.Service;
import pl.dicedev.builders.ExpensesDtoBuilder;
import pl.dicedev.enums.FilterExpensesParametersEnum;
import pl.dicedev.enums.MonthsEnum;
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
        return expensesRepositoryAllByUser.stream()
                .map(expensesEntity -> new ExpensesDtoBuilder()
                        .withId(expensesEntity.getId())
                        .withAmount(expensesEntity.getAmount())
                        .withCategory(expensesEntity.getCategory())
                        .withPurchaseDate(expensesEntity.getPurchaseDate())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ExpensesDto> getFilteredExpenses(Map<String, String> filters) {
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

    private List<ExpensesDto> getAllExpensesForMonthInYear(String year, String month) {
        String dateFrom = MonthsEnum.valueOf(month.toUpperCase()).getFirstDayForYear(year);
        String dateTo = MonthsEnum.valueOf(month.toUpperCase()).getLastDayForYear(year);
        return getAllExpensesBetweenDate(dateFrom, dateTo);
    }

    private List<ExpensesDto> getAllExpensesBetweenDate(String dateFrom, String dateTo) {
        UserEntity user = userLogInfoService.getLoggedUserEntity();
        String instantSuffix = "T00:00:00.001Z";

        List<ExpensesEntity> expensesEntities = expensesRepository.findAllByDate(
                Instant.parse(dateFrom + instantSuffix),
                Instant.parse(dateTo + instantSuffix),
                user);

        List<ExpensesDto> expensesDtos = expensesEntities.stream()
                .map(expensesEntity -> expensesMapper.fromEntityToDto(expensesEntity))
                .collect(Collectors.toList());

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
