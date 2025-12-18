package lab;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class WebController {
    @Value("${API_URL:}")
    private String url;

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        model.addAttribute("url", url);
        return "index";
    }
}
