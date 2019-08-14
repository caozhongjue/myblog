package top.caozhongjue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.caozhongjue.dao.UserMapper;
import top.caozhongjue.pojo.Access_token;
import top.caozhongjue.pojo.GithubUser;
import top.caozhongjue.pojo.User;
import top.caozhongjue.provider.GitHubProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 授权登录控制器
 */
@Controller
public class AuthorizenController {
    @Autowired
    private GitHubProvider gitHubProvider;
    @Value("${github.client.id}")
    private String clientid;
    @Value("${github.client.secret}")
    private String clientsecret;
    @Value("${github.redirect.uri}")
    private String redirecturi;
    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           HttpServletRequest request,
                           HttpServletResponse response){
        Access_token access_token = new Access_token();//封装一个token实体类，存值
        access_token.setCode(code);
        access_token.setRedirect_uri(redirecturi);
        access_token.setClient_id(clientid);
        access_token.setClient_secret(clientsecret);
        String stoken = gitHubProvider.getAccess_token(access_token);//获取stoken方法，返回stoken
        GithubUser githubUser = gitHubProvider.getGithubUser(stoken);//获取user信息方法，返回GithubUser
        //System.out.println(githubUser.getLogin()+"===="+githubUser.getId());
        if(githubUser != null &githubUser.getId() !=null){
            User user=new User();//数据库中对应的user表的实体类
            user.setName(githubUser.getLogin());
            user.setAccountId(String.valueOf(githubUser.getId()));
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userMapper.insert(user);//传入参数
            response.addCookie(new Cookie("token",token));
            //request.getSession().setAttribute("user",githubUser);
            return "redirect:/"; //redirect重定向到哪个路径
        }else{
            return "redirect:/";
        }
    }
}
