package pl.dicedev.services;

import org.springframework.stereotype.Service;
import pl.dicedev.builders.ExpensesDtoBuilder;
import pl.dicedev.enums.FilterSpecification;
import pl.dicedev.filters.FilterRangeStrategy;
import pl.dicedev.mappers.ExpensesMapper;
import pl.dicedev.repositories.ExpensesRepository;
import pl.dicedev.repositories.entities.ExpensesEntity;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.dtos.ExpensesDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExpensesService {

    private final ExpensesMapper expensesMapper;
    private final ExpensesRepository expensesRepository;
    private final UserLogInfoService userLogInfoService;
    private final FilterRangeStrategy<ExpensesEntity> expensesFilterRange;

    public ExpensesService(
            ExpensesMapper expensesMapper,
            ExpensesRepository expensesRepository,
            UserLogInfoService userLogInfoService,
            FilterRangeStrategy<ExpensesEntity> expensesFilterRange) {
        this.expensesMapper = expensesMapper;
        this.expensesRepository = expensesRepository;
        this.userLogInfoService = userLogInfoService;
        this.expensesFilterRange = expensesFilterRange;
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
        UserEntity user = userLogInfoService.getLoggedUserEntity();
        return expensesFilterRange.getFilteredDataForSpecification(user, filters, FilterSpecification.FOR_EXPENSES).stream()
                .map(expensesMapper::fromEntityToDto)
                .collect(Collectors.toList());
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
