package pl.dicedev.services.integrations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.dicedev.builders.ExpensesEntityBuilder;
import pl.dicedev.repositories.AssetsRepository;
import pl.dicedev.repositories.ExpensesRepository;
import pl.dicedev.repositories.UserRepository;
import pl.dicedev.repositories.entities.ExpensesEntity;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.AssetsService;
import pl.dicedev.services.ExpensesService;
import pl.dicedev.services.UserDetailsServiceImpl;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@SpringBootTest
@Transactional
@WithMockUser(username = "user123", password = "123user")
public abstract class InitIntegrationTestData {

    @Autowired
    protected UserDetailsServiceImpl userDetailsService;
    @Autowired
    protected ExpensesRepository expensesRepository;
    @Autowired
    protected ExpensesService expensesService;
    @Autowired
    protected AssetsRepository assetsRepository;
    @Autowired
    protected AssetsService service;
    @Autowired
    protected UserRepository userRepository;

    protected String USER_NAME_PRIME = "user123";
    protected String USER_PASSWORD_PRIME = "123user";
    protected String USER_NAME_SECOND = "secondUser123";
    protected String USER_PASSWORD_SECOND = "123SecondUser";


    protected UserEntity initDefaultMockUserInDatabase() {
        var user = new UserEntity();
        user.setUsername(USER_NAME_PRIME);
        user.setPassword(USER_PASSWORD_PRIME);

        return userRepository.save(user);
    }

    protected UserEntity initSecondMockUserInDatabase() {
        var user = new UserEntity();
        user.setUsername(USER_NAME_SECOND);
        user.setPassword(USER_PASSWORD_SECOND);

        return userRepository.save(user);
    }

    protected UUID initDatabaseByExpenses(UserEntity user, String date) {
        String instantSuffix = "T00:00:00.001Z";

        ExpensesEntity expensesEntity = new ExpensesEntityBuilder()
                .withAmount(BigDecimal.ONE)
                .withUser(user)
                .withPurchaseDate(Instant.parse(date+instantSuffix))
                .build();

        ExpensesEntity savedEntity = expensesRepository.save(expensesEntity);
        return savedEntity.getId();
    }

}
