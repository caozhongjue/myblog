package top.caozhongjue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.caozhongjue.dao.QuestionMapper;
import top.caozhongjue.dao.UserMapper;
import top.caozhongjue.pojo.Question;
import top.caozhongjue.pojo.User;
import top.caozhongjue.services.QuestionService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 发布文章控制器
 */
@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserMapper userMapper;
    @RequestMapping("/publish")
    public String publish(){
        return "publish";
    }
    @PostMapping("/dopublish")
    public String doPublish(
            @RequestParam(value = "id",required = false)Integer id,
            @RequestParam(value="title",required = false)String title,
            @RequestParam(value="description",required = false)String description,
            @RequestParam(value="tags",required = false)String tags,
            HttpServletRequest request,
            Model model){
        model.addAttribute("id",id);
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tags",tags);
        Question question = new Question();
        User user = null;
        Cookie[] cookies = request.getCookies();
        if(cookies !=null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    user = userMapper.findByToken(token);
                    if(user != null){
                        request.getSession().setAttribute("user",user);
                    }
                    break;
                }
            }
        }
        if(user== null){
           model.addAttribute("error","发表文章，请登录");
           return "publish";
        }
        question.setId(id);
        question.setTitle(title);
        question.setDescription(description);
        question.setTags(tags);

        questionService.createOrUpdate(question,user);
        return "redirect:/";
    }
    @RequestMapping("/publish/{id}")
    public String editPublish(@PathVariable(name="id")Integer id,
                              Model model) {
        Question question = questionMapper.getById(id);
        model.addAttribute("id",question.getId());
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tags",question.getTags());
        return "publish";
    }

}
