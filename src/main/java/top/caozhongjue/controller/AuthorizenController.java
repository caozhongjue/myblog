package top.caozhongjue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.caozhongjue.pojo.Access_token;
import top.caozhongjue.pojo.GithubUser;
import top.caozhongjue.provider.GitHubProvider;

import javax.servlet.http.HttpServletRequest;

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

    @RequestMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name="state")String state,
                           HttpServletRequest request){
        Access_token access_token = new Access_token();//封装一个token实体类，存值
        access_token.setCode(code);
        access_token.setState(state);
        access_token.setRedirect_uri(redirecturi);
        access_token.setClient_id(clientid);
        access_token.setClient_secret(clientsecret);
        String stoken = gitHubProvider.getAccess_token(access_token);//获取stoken方法，返回stoken
        GithubUser githubUser = gitHubProvider.getGithubUser(stoken);//获取user信息方法，返回GithubUser
        //System.out.println(githubUser.getLogin()+"===="+githubUser.getId());
        if(githubUser != null){
            request.getSession().setAttribute("user",githubUser);
            return "redirect:/"; //redirect重定向到哪个路径
        }else{
            return "redirect:/";
        }
    }
}
