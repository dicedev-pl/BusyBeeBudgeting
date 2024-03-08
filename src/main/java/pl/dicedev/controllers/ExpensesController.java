package pl.dicedev.controllers;

import org.springframework.web.bind.annotation.*;
import pl.dicedev.services.ExpensesService;
import pl.dicedev.services.dtos.ExpensesDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
public class ExpensesController {

    private final ExpensesService expensesService;

    public ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @GetMapping
    public List<ExpensesDto> getAllExpenses() {
        return expensesService.getAllExpenses();
    }

    @GetMapping("filter")
    public List<ExpensesDto> getAllExpenses(
            @RequestParam String from,
            @RequestParam String to
    ) {
        return expensesService.getAllExpensesBetweenDate(from, to);
    }

    @PostMapping
    public UUID setExpenses(@RequestBody ExpensesDto expensesDto) {
        return expensesService.setExpenses(expensesDto);
    }

    @PutMapping
    public void updateExpenses(@RequestBody ExpensesDto expensesDto) {
        expensesService.updateExpenses(expensesDto);
    }

    @DeleteMapping
    public void deleteExpenses(@RequestBody ExpensesDto expensesDto) {
        expensesService.deleteExpenses(expensesDto);
    }

}
