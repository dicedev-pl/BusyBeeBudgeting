package pl.dicedev.services.integrations;

import org.assertj.core.util.Streams;
import org.junit.jupiter.api.Test;
import pl.dicedev.builders.AssetDtoBuilder;
import pl.dicedev.builders.AssetEntityBuilder;
import pl.dicedev.enums.AssetCategory;
import pl.dicedev.enums.FilterParametersCalendarEnum;
import pl.dicedev.repositories.entities.AssetEntity;
import pl.dicedev.repositories.entities.UserEntity;
import pl.dicedev.services.dtos.AssetDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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
        var allAssetsInDB = assetsService.getAllAssets();

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
        assetsService.setAsset(dto);

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
        var allAssetsWithOneCategory = assetsService.getAssetsByCategory(category);

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
        assetsService.deleteAssetByUser(userEntity);

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


    @Test
    void shouldGetAllAssetsByFilterByDateFromAndTo() {
        // given
        var fromDate = "2021-01-04";
        var toDate = "2021-01-10";
        var middleDate = "2021-01-08";
        var notInRangeDate = "2021-01-11";
        var user = initDefaultMockUserInDatabase();
        initDatabaseByAssets(user, fromDate);
        initDatabaseByAssets(user, toDate);
        initDatabaseByAssets(user, middleDate);
        initDatabaseByAssets(user, notInRangeDate);
        Map<String, String> filters = new HashMap<>();
        filters.put(FilterParametersCalendarEnum.DATE_FORM.getKey(), fromDate);
        filters.put(FilterParametersCalendarEnum.DATE_TO.getKey(), toDate);

        // when
        var result = assetsService.getFilteredAssets(filters);

        // then
        assertThat(result).hasSize(3);
        var dateAsString = result.stream()
                .map(dto -> dto.getIncomeDate().toString().substring(0, fromDate.length()))
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
        initDatabaseByAssets(user, fromDate);
        initDatabaseByAssets(user, toDate);
        initDatabaseByAssets(user, middleDate);
        initDatabaseByAssets(user, notInRangeDate);
        Map<String, String> filters = new HashMap<>();
        filters.put(FilterParametersCalendarEnum.MONTH.getKey(), "january");
        filters.put(FilterParametersCalendarEnum.YEAR.getKey(), "2021");

        // when
        var result = assetsService.getFilteredAssets(filters);

        // then
        assertThat(result).hasSize(3);
        var dateAsString = result.stream()
                .map(dto -> dto.getIncomeDate().toString().substring(0, fromDate.length()))
                .collect(Collectors.toSet());
        assertThat(dateAsString)
                .contains(fromDate, toDate, middleDate)
                .doesNotContain(notInRangeDate);

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
