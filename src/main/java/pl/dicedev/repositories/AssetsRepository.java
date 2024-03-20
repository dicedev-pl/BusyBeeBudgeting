package pl.dicedev.repositories;

import org.springframework.data.jpa.repository.Query;
import pl.dicedev.enums.AssetCategory;
import pl.dicedev.repositories.entities.AssetEntity;
import pl.dicedev.repositories.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AssetsRepository extends JpaRepository<AssetEntity, UUID> {

    List<AssetEntity> getAssetEntitiesByCategory(AssetCategory category);

    List<AssetEntity> getAssetEntitiesByUser(UserEntity userEntity);

    void deleteAllByUser(UserEntity userEntity);

    @Query("SELECT e FROM AssetEntity e WHERE e.user = :user AND e.incomeDate >= :fromDate AND e.incomeDate <= :toDate")
    List<AssetEntity> findByIncomeDateBeforeAndByUser(Instant fromDate, Instant toDate, UserEntity user);
}
