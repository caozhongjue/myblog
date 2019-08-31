package top.caozhongjue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.caozhongjue.dao.QuestionMapper;
import top.caozhongjue.dao.UserMapper;
import top.caozhongjue.dto.PaginationDTO;
import top.caozhongjue.dto.QuestionDTO;
import top.caozhongjue.pojo.Question;
import top.caozhongjue.pojo.User;
import top.caozhongjue.services.QuestionService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 首页访问路径控制器
 */
@Controller
public class IndexController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionService questionService;

    @RequestMapping("/")
    public String index(HttpServletRequest request,
                        Model model,@RequestParam(value = "page",defaultValue = "1")Integer page,
                        @RequestParam(value = "size",defaultValue = "10")Integer size) {
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie :cookies){
                if(cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if(user!=null){
                        request.getSession().setAttribute("user",user);
                    }
                    break;
                }
            }
        }
//      List<QuestionDTO> listQuestions = questionService.listQuestionDTO(page,size);
        PaginationDTO paginationDTO = questionService.listQuestionDTO(page,size);
        model.addAttribute("paginationDTO",paginationDTO);
        return "index";
    }
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");
        response.addCookie(new Cookie("token",null));
        return "redirect:/";
    }
    @RequestMapping("demo")
    public String demo(){

        return "demo";
    }
}
