package com.jpmc.midascore;


import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final UserRepository userRepo;

    public BalanceController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping(value = "/balance")
    public Balance balance(@RequestParam("userId") Long userId) {
        UserRecord user = userRepo.findById(userId).orElse(null);
        float amt = (user != null) ? user.getBalance() : 0;
        return new Balance(amt);
    }

}
