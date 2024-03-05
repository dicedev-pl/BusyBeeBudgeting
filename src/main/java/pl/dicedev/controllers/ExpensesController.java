package pl.dicedev.controllers;

import pl.dicedev.services.ExpensesService;
import pl.dicedev.services.dtos.ExpensesDto;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
public class ExpensesController {

    private ExpensesService expensesService;

    public ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @GetMapping
    public List<ExpensesDto> getAllExpenses() {
        return expensesService.getAllExpenses();
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
