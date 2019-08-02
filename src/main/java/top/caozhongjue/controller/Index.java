package top.caozhongjue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Index {
    @RequestMapping("/")
    public String index(@RequestParam(name = "name" )String name , Model model) {
        model.addAttribute("name",name);

        return "index";
    }

}