package spring.security.basic.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @GetMapping
    public String getHomePage(){
        return "Welcome to Home page";
    }

    @GetMapping("/dashboard")
    public String getDashboardPage(){
        return "Welcome to Dashboard page";
    }
}
