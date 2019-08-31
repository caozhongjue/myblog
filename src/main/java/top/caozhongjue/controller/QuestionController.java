package top.caozhongjue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.caozhongjue.dto.QuestionDTO;
import top.caozhongjue.pojo.Question;
import top.caozhongjue.services.QuestionService;
@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    //首页中按id显示数据
    @RequestMapping("/question/{id}")
    public String selectQuestionById(Model model, @PathVariable(name="id")Integer id){
        questionService.incView(id);
        QuestionDTO questionDTO = questionService.selectQuestionById(id);
        model.addAttribute("questionDTO",questionDTO);
        return "detail";
    }
}
