package demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class IndexController {

    @GetMapping
    public String index() {
        return "Hello";
    }
}

