package top.caozhongjue.provider;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;
import top.caozhongjue.pojo.Access_token;
import top.caozhongjue.pojo.GithubUser;

import java.io.IOException;

@Component
public class GitHubProvider {
    //通过回调url携带code，获取token
    public String getAccess_token(Access_token access_token){
        final MediaType json = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(json, JSON.toJSONString(access_token));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String[] split =string.split("&");
            String stoken = split[0];
            String[] splitstoken =stoken.split("=");
            String token = splitstoken[1];
            //System.out.println(token);
            return token;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    //携带token，获取用户信息
    public GithubUser getGithubUser(String accesstoken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accesstoken)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string,GithubUser.class);
            return githubUser;
        }catch (IOException e){
        }
        return  null;
    }

}
