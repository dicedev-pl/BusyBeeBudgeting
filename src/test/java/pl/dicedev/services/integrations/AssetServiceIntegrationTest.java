package pl.dicedev.services.integrations;

import pl.dicedev.builders.AssetDtoBuilder;
import pl.dicedev.builders.AssetEntityBuilder;
import pl.dicedev.enums.AssetCategory;
import pl.dicedev.repositories.AssetsRepository;
import pl.dicedev.repositories.UserRepository;
import pl.dicedev.repositories.entities.AssetEntity;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.AssetsService;
import pl.dicedev.services.dtos.AssetDto;
import org.assertj.core.util.Streams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class AssetServiceIntegrationTest extends InitIntegrationTestData {

    @Test
    void shouldReturnListWithThreeElements() {
        // given
        initDataBaseByDefaultMockUserAndHisAssets();
        initDataBaseBySecondMockUserAndHisAssets();

        // when
        var allAssetsInDB = service.getAllAssets();

        // then
        assertThat(allAssetsInDB).hasSize(3);

    }

    @Test
    void shouldAddAssetToDB() {
        // given
        initDefaultMockUserInDatabase();
        AssetDto dto = new AssetDtoBuilder()
                .withAmount(new BigDecimal(11))
                .withIncomeDate(Instant.now())
                .withCategory(AssetCategory.BONUS)
                .build();

        // when
        service.setAsset(dto);

        // then
        var allAssetInDB = assetsRepository.findAll();
        assertThat(allAssetInDB).hasSize(1);
        var entity = allAssetInDB.get(0);
        assertThat(entity.getCategory()).isEqualTo(dto.getCategory());
        assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(entity.getIncomeDate()).isEqualTo(dto.getIncomeDate());

    }

    @Test
    void shouldReturnListOnlyWithOneCategory() {
        // given
        initDataBaseByDefaultMockUserAndHisAssets();
        var category = AssetCategory.OTHER;

        // when
        var allAssetsWithOneCategory = service.getAssetsByCategory(category);

        // then
        assertThat(allAssetsWithOneCategory).hasSize(1);
        var entity = allAssetsWithOneCategory.get(0);
        assertThat(entity.getCategory()).isEqualTo(category);
    }

    @Test
    void shouldDeleteAllAssetsOfChosenUser() {
        // given
        initDataBaseByDefaultMockUserAndHisAssets();
        initDataBaseBySecondMockUserAndHisAssets();
        int numberOfAllAssets = 6;
        int numberOfLeaveAssets = 3;

        var allUsers = userRepository.findAll();
        var userToDeleteAssets = Streams.stream(allUsers).findFirst();
        UserEntity userEntity = userToDeleteAssets.get();
        var userToLeaveAssets = Streams.stream(allUsers)
                .filter(entity -> !entity.equals(userEntity))
                .findFirst().get();

        var allAssetsInDatabase = assetsRepository.findAll();
        assertThat(allAssetsInDatabase).hasSize(numberOfAllAssets);

        // when
        service.deleteAssetByUser(userEntity);

        // then
        var assetsAfterDelete = assetsRepository.findAll();
        assertThat(assetsAfterDelete).hasSize(numberOfLeaveAssets);

        var assetsUserId = assetsAfterDelete.stream()
                .map(assetEntity -> assetEntity.getUser())
                .map(ue -> ue.getId())
                .collect(Collectors.toSet());
        assertThat(assetsUserId).hasSize(1)
                .containsExactly(userToLeaveAssets.getId());

    }

    private void initDataBaseByDefaultMockUserAndHisAssets() {
        var userEntity = initDefaultMockUserInDatabase();
        AssetEntity entity1 = new AssetEntityBuilder()
                .withAmount(new BigDecimal(1))
                .withIncomeDate(Instant.now())
                .withCategory(AssetCategory.OTHER)
                .withUser(userEntity)
                .build();
        AssetEntity entity2 = new AssetEntityBuilder()
                .withAmount(new BigDecimal(3))
                .withIncomeDate(Instant.now())
                .withCategory(AssetCategory.SALARY)
                .withUser(userEntity)
                .build();
        AssetEntity entity3 = new AssetEntityBuilder()
                .withAmount(new BigDecimal(5))
                .withIncomeDate(Instant.now())
                .withCategory(AssetCategory.RENT)
                .withUser(userEntity)
                .build();

        assetsRepository.saveAll(asList(entity1, entity2, entity3));
    }

    private void initDataBaseBySecondMockUserAndHisAssets() {
        var userEntity = initSecondMockUserInDatabase();
        AssetEntity entity1 = new AssetEntityBuilder()
                .withAmount(new BigDecimal(1))
                .withIncomeDate(Instant.now())
                .withCategory(AssetCategory.OTHER)
                .withUser(userEntity)
                .build();
        AssetEntity entity2 = new AssetEntityBuilder()
                .withAmount(new BigDecimal(3))
                .withIncomeDate(Instant.now())
                .withCategory(AssetCategory.SALARY)
                .withUser(userEntity)
                .build();
        AssetEntity entity3 = new AssetEntityBuilder()
                .withAmount(new BigDecimal(5))
                .withIncomeDate(Instant.now())
                .withCategory(AssetCategory.RENT)
                .withUser(userEntity)
                .build();

        assetsRepository.saveAll(asList(entity1, entity2, entity3));
    }
}
