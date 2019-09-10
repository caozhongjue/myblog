package top.caozhongjue.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.caozhongjue.dao.QuestionMapper;
import top.caozhongjue.dao.UserMapper;
import top.caozhongjue.dto.PaginationDTO;
import top.caozhongjue.dto.QuestionDTO;
import top.caozhongjue.pojo.*;
import top.caozhongjue.provider.AesCbcUtil;
import top.caozhongjue.provider.HttpRequest;
import top.caozhongjue.services.QuestionService;
import top.caozhongjue.services.UserService;
import top.caozhongjue.services.WxAccountService;
import top.caozhongjue.services.WxAppletService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class WxIndexController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @Autowired
    private WxAppletService wxAppletService;
    //小程序首页显示所有数据
    @ResponseBody
    @RequestMapping("/main")
    public PaginationDTO main(HttpServletRequest request,
                              Model model, @RequestParam(value = "page",defaultValue = "1")Integer page,
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
        PaginationDTO paginationDTO = questionService.listQuestionDTO(page,size);
        return paginationDTO;
    }
    //小程序中按id显示数据
    @ResponseBody
    @RequestMapping("/selectQuestionById")
    public QuestionDTO selectQuestionById(@RequestParam("id")Integer id){
        questionService.incView(id);
        QuestionDTO question = questionService.selectQuestionById(id);
        return question;
    }
    //小程序登录
    @RequestMapping("/login")
    @ResponseBody
    public ResponseEntity login(@RequestParam("encryptedData")String encryptedData,
                                @RequestParam("iv")String iv,
                                @RequestParam("code")String code){
        Map map = new HashMap();
        // 登录凭证不能为空
        if (code == null || code.length() == 0) {
            map.put("status", 0);
            map.put("msg", "code 不能为空");
        }
        return new ResponseEntity<>(wxAppletService.wxUserLogin(encryptedData,iv,code), HttpStatus.OK);

    }
    //添加收藏
    @RequiresAuthentication
    @PostMapping(value="/addCollect")
    @ResponseBody
    public void addCollect(@RequestParam("id") String id,
                           HttpServletRequest request){
        //获取请求头部分的Authorization
        String token = request.getHeader("Authorization");
        String openid = JWT.decode(token).getClaim("wxOpenId").asString();
        questionService.addCollect(id,openid);
    }
    //取消收藏
    @RequiresAuthentication
    @PostMapping("/deleteCollect")
    @ResponseBody
    public void deleteCollect(@RequestParam("id") String id,
                              HttpServletRequest request){
        //获取请求头部分的Authorization
        String token = request.getHeader("Authorization");
        String openid = JWT.decode(token).getClaim("wxOpenId").asString();
        questionService.deleteCollect(id,openid);
    }
    //点击点赞
    @RequiresAuthentication
    @PostMapping(value="/addLike" )
    @ResponseBody
    public void addLike(@RequestParam("id")String id,
                        HttpServletRequest request){
        //获取请求头部分的Authorization
        String token = request.getHeader("Authorization");
        String openid = JWT.decode(token).getClaim("wxOpenId").asString();
        questionService.addLike(id);
    }
    //取消点赞
    @RequiresAuthentication
    @PostMapping("/deleteLike")
    @ResponseBody
    public void  deleteLike(@RequestParam("id")String id,
                            HttpServletRequest request){
        //获取请求头部分的Authorization
        String token = request.getHeader("Authorization");
        String openid = JWT.decode(token).getClaim("wxOpenId").asString();
        questionService.deleteLike(id);
    }
    @RequiresAuthentication
    @PostMapping("/selectCollectById")
    @ResponseBody
    public Map selectCollectById(@RequestParam("id") String id,
                                 HttpServletRequest request){
        Map map = new HashMap();
        //获取请求头部分的Authorization
        String token = request.getHeader("Authorization");
        String openid = JWT.decode(token).getClaim("wxOpenId").asString();
        Collect1 collet = questionService.selectCollectById(id,openid);
        if(collet != null){
            map.put("code",1);
        }else{
            map.put("code",0);
        }
        return map;
    }
    @RequiresAuthentication
    @RequestMapping("selectMyCollectByOpenid")
    @ResponseBody
    public Map selectMyCollectByOpenid(HttpServletRequest request) {
        Map map = new HashMap();
        String token = request.getHeader("Authorization");
        String openid = JWT.decode(token).getClaim("wxOpenId").asString();
        List<QuestionDTO> questions = questionService.selectMyCollectByOpenid(openid);
        if (!questions.isEmpty()){
            map.put("questions",questions);
        }else{
            map.put("num",0);
        }
        return map;
    }
    @RequestMapping("wxcallback")
    public String wxcallback(@RequestParam("code")String code,
                           @RequestParam("state")String state) {
        return null;
    }

}
