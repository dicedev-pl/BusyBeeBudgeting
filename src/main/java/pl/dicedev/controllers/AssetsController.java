package pl.dicedev.controllers;

import pl.dicedev.enums.AssetCategory;
import pl.dicedev.services.AssetsService;
import pl.dicedev.services.dtos.AssetDto;
import org.springframework.web.bind.annotation.*;
import pl.dicedev.services.dtos.ExpensesDto;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assets")
public class AssetsController {

    private final AssetsService assetsService;

    public AssetsController(AssetsService assetsService) {
        this.assetsService = assetsService;
    }

    @GetMapping
    public List<AssetDto> getAssets() {
        return assetsService.getAllAssets();
    }

    @GetMapping("filter")
    public List<AssetDto> getAllExpenses(
            @RequestParam Map<String, String> filters) {
        return assetsService.getFilteredAssets(filters);
    }

    @PostMapping
    public void setAsset(@RequestBody AssetDto dto) {
        assetsService.setAsset(dto);
    }

    @DeleteMapping
    public void deleteAsset(@RequestBody AssetDto dto) {
        assetsService.deleteAsset(dto);
    }

    @PutMapping
    public void updateAsset(@RequestBody AssetDto dto) {
        assetsService.updateAsset(dto);
    }

    @GetMapping("/find")
    public List<AssetDto> getAllAssetsByCategory(@PathParam("category") String category) {
        return assetsService.getAssetsByCategory(AssetCategory.valueOf(category.toUpperCase()));
    }

}
