package pl.dicedev.services.integrations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.dicedev.builders.AssetEntityBuilder;
import pl.dicedev.enums.AssetCategory;
import pl.dicedev.enums.AuthenticationMessageEnum;
import pl.dicedev.excetpions.BudgetUserAlreadyExistsInDatabaseException;
import pl.dicedev.excetpions.BudgetUserNotFoundException;
import pl.dicedev.repositories.AssetsRepository;
import pl.dicedev.repositories.UserRepository;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.UserDetailsServiceImpl;
import pl.dicedev.services.dtos.UserDetailsDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDetailsServiceImplIntegrationTest extends InitIntegrationTestData {

    @Test
    void shouldReturnUserWithUserNameAndPasswordFromDatabase() {
        // given
        initDefaultMockUserInDatabase();

        // when
        var result = userDetailsService.loadUserByUsername(USER_NAME_PRIME);

        // then
        assertThat(result.getUsername()).isEqualTo(USER_NAME_PRIME);
        assertThat(result.getPassword()).isEqualTo(USER_PASSWORD_PRIME);

    }

    @Test
    void shouldSaveUserInToDatabase() {
        // given
        UserDetailsDto dto = new UserDetailsDto();
        dto.setUsername(USER_NAME_PRIME);
        dto.setPassword(USER_PASSWORD_PRIME);
        var bCryptPrefix = "$2a$10$";
        var bCryptRegex = "^[$]2[abxy]?[$](?:0[4-9]|[12][0-9]|3[01])[$][./0-9a-zA-Z]{53}$";

        // when
        var userId = userDetailsService.saveUser(dto);

        // then
        assertThat(userId).isNotNull();
        var userEntityOptional = userRepository.findById(userId);
        var userEntity = userEntityOptional.get();
        assertAll(
                () -> assertThat(userEntity.getUsername()).isEqualTo(USER_NAME_PRIME),
                () -> assertThat(userEntity.getPassword()).contains(bCryptPrefix),
                () -> assertThat(userEntity.getPassword()).matches(Pattern.compile(bCryptRegex))
        );
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFoundInDatabase() {
        // given
        initDefaultMockUserInDatabase();

        // when
        var result = assertThrows(BudgetUserNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("fakeUser"));

        // then
        assertThat(result.getMessage()).isEqualTo(AuthenticationMessageEnum.USER_NOT_FOUND.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExistsInDatabase() {
        // given
        initDefaultMockUserInDatabase();
        UserDetailsDto dto = new UserDetailsDto();
        dto.setPassword(USER_PASSWORD_PRIME);
        dto.setUsername(USER_NAME_PRIME);

        // when
        var result = assertThrows(BudgetUserAlreadyExistsInDatabaseException.class,
                () -> userDetailsService.saveUser(dto));

        // then
        assertThat(result.getMessage()).isEqualTo(AuthenticationMessageEnum.USER_ALREADY_EXISTS.getMessage());

    }

    @Test
    void shouldRemoveUserWhichDoNotHaveAnyAssetsInDatabase() {
        // given
        initDefaultMockUserInDatabase();

        var userInDatabase = userRepository.findAll();
        assertThat(userInDatabase).hasSize(1);

        // when
        userDetailsService.deleteUser();

        // then
        var userInDatabaseAfterRemove = userRepository.findAll();
        assertThat(userInDatabaseAfterRemove).hasSize(0);

    }

    @Test
    void shouldRemoveUserWhichHasOneAssetInDatabase() {
        // given
        initDefaultMockUserInDatabase();
        var userEntity = userRepository.findByUsername(USER_NAME_PRIME).get();
        initDatabaseByAssetsForUser(userEntity);

        var userInDatabase = userRepository.findAll();
        assertThat(userInDatabase).hasSize(1);
        var assetsInDatabase = assetsRepository.findAll();
        assertThat(assetsInDatabase).hasSize(1);
        assertThat(assetsInDatabase.get(0).getUser()).isEqualTo(userEntity);

        // when
        userDetailsService.deleteUser();

        // then
        var userInDatabaseAfterDelete = userRepository.findAll();
        assertThat(userInDatabaseAfterDelete).hasSize(0);
        var assetsInDatabaseAfterDelete = assetsRepository.findAll();
        assertThat(assetsInDatabaseAfterDelete).hasSize(0);

    }

    private void initDatabaseByAssetsForUser(UserEntity userEntity) {
        var assetEntity = new AssetEntityBuilder()
                .withIncomeDate(Instant.now())
                .withUser(userEntity)
                .withAmount(BigDecimal.ONE)
                .withCategory(AssetCategory.BONUS)
                .build();

        assetsRepository.save(assetEntity);
    }

}