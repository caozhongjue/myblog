package top.caozhongjue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.caozhongjue.dao.QuestionMapper;
import top.caozhongjue.dao.UserMapper;
import top.caozhongjue.pojo.Question;
import top.caozhongjue.pojo.User;

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
    private UserMapper userMapper;
    @RequestMapping("/publish")
    public String publish(){
        return "publish";
    }
    @PostMapping("/dopublish")
    public String doPublish(
            @RequestParam("title")String title,
            @RequestParam("description")String description,
            @RequestParam("tags")String tags,
            HttpServletRequest request,
            Model model){
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
        question.setTitle(title);
        question.setDescription(description);
        question.setTags(tags);
        question.setCreator(user.getId());
//        question.setGmtCreate(System.currentTimeMillis());
//        question.setGmtModified(question.getGmtCreate());
        question.setGmtCreate(Long.toString(System.currentTimeMillis()));//Long类型转String
        question.setGmtModified(Long.parseLong(question.getGmtCreate()));//String转Long类型
        question.setCommentCount(0);
        question.setLikeCount(0);
        question.setViewCount(0);
        questionMapper.create(question);
        return "redirect:/";
    }

}
