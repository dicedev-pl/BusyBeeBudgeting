package pl.dicedev.filters;

import org.springframework.stereotype.Component;
import pl.dicedev.enums.FilterSpecification;
import pl.dicedev.repositories.AssetsRepository;
import pl.dicedev.repositories.entities.AssetEntity;
import pl.dicedev.repositories.entities.UserEntity;

import java.time.Instant;
import java.util.List;

@Component
public class AssetsFilterRange extends FilterRange<AssetEntity> {

    private final AssetsRepository assetsRepository;

    public AssetsFilterRange(AssetsRepository assetsRepository) {
        this.assetsRepository = assetsRepository;
    }

    @Override
    public List<AssetEntity> getAllEntityBetweenDate(Instant fromDate, Instant toDate, UserEntity user) {
        return assetsRepository.findByIncomeDateBeforeAndByUser(fromDate, toDate, user);
    }

    @Override
    protected String getFilterName() {
        return FilterSpecification.FOR_ASSETS.getValidator();
    }
}
