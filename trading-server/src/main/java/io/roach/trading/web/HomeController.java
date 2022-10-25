package io.roach.trading.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import io.roach.trading.util.CockroachFacts;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("title", "Roach Trading");
        model.addAttribute("randomFact", CockroachFacts.nextFact());

        return "home";
    }

    @PostMapping("/inform")
    public ResponseEntity<?> informPage(Model model) {
        return ResponseEntity.ok().build();
    }
}
