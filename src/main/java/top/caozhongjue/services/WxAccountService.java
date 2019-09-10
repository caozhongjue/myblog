package top.caozhongjue.services;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import top.caozhongjue.dao.UserMapper;
import top.caozhongjue.dto.Code2SessionResponse;
import top.caozhongjue.dto.JSONUtil;
import top.caozhongjue.dto.TokenDTO;
import top.caozhongjue.interceptor.config.jwt.JwtConfig;
import top.caozhongjue.pojo.User;
import top.caozhongjue.pojo.WXUser;
import top.caozhongjue.pojo.WxAccount;
import top.caozhongjue.provider.AesCbcUtil;
import top.caozhongjue.provider.HttpRequest;

import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by EalenXie on 2018/11/26 10:50.
 * 实体 行为描述
 */
@Service
public class WxAccountService implements WxAppletService {

    @Autowired
    private RestTemplate restTemplate;
    @Resource
    private JwtConfig jwtConfig;
    @Autowired
    private UserMapper userMapper;

    /**
     * 微信的 code2session 接口 获取微信用户信息
     * 官方说明 : https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
     */
    private String code2Session(String jsCode) {
        // 小程序唯一标识 (在微信小程序管理后台获取)
        String wxspAppid = "wx13fa6457d04c0e65";
        // 小程序的 app secret (在微信小程序管理后台获取)
        String wxspSecret = "a745cf0a2a3c90e727534372f968d854";
        // 授权（必填）
        String grant_type = "authorization_code";
        //////////////// 1、向微信服务器 使用登录凭证 code 获取 session_key 和 openid
        // 请求参数
        String params = "appid=" + wxspAppid + "&secret=" + wxspSecret + "&js_code=" + jsCode + "&grant_type="
                + grant_type;
        // 发送请求
        String sr = HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);
        return sr;
    }


    /**
     * 微信小程序用户登陆，完整流程可参考下面官方地址，本例中是按此流程开发
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html
     *
     * @param code 小程序端 调用 wx.login 获取到的code,用于调用 微信code2session接口
     * @return 返回后端 自定义登陆态 token  基于JWT实现
     */
    @Override
    public TokenDTO wxUserLogin(String encryptedData,String iv,String code) {
        //1 . code2session返回JSON数据
        String resultJson = code2Session(code);
        // 解析相应内容（转换成json对象）
        JSONObject json = JSONObject.parseObject(resultJson);
        // 获取会话密钥（session_key）
        String session_key = json.get("session_key").toString();
        // 用户的唯一标识（openid）
        String openid = (String) json.get("openid");
        //////////////// 2、对encryptedData加密数据进行AES解密 ////////////////

        String result = null;
        try {
            result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        if (null != result && result.length() > 0) {
            //保存数据到库里
            WXUser wxUser = JSON.parseObject(result, WXUser.class);
            User user = new User();
            user.setName(wxUser.getNickName());
            user.setAccountId(wxUser.getOpenId());
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setAvatarUrl(wxUser.getAvatarUrl());
            user.setGender(wxUser.getGender());
            user.setProvince(wxUser.getProvince());
            user.setCity(wxUser.getCity());
            User user1 = userMapper.findByAcountId(user.getAccountId());
            if (user1 == null) {
                user.setGmtCreate(System.currentTimeMillis());
                user.setGmtModified(user.getGmtCreate());
                userMapper.insert(user);
                String token1 = jwtConfig.createTokenByWxAccount(user);
                return new TokenDTO(token1);
            } else {
                user1.setName(user.getName());
                user1.setToken(user.getToken());
                user1.setGmtModified(System.currentTimeMillis());
                user1.setAvatarUrl(user.getAvatarUrl());
                userMapper.update(user1);
                String token1 = jwtConfig.createTokenByWxAccount(user);
                return new TokenDTO(token1);
            }
            //Map map = new HashMap();
            //返回解密的数据给小程序
            //map.put("status", 1);
            //map.put("msg", "解密成功");
            //JSONObject userInfoJSON = JSONObject.parseObject(result);
            //Map userInfo = new HashMap();
            //userInfo.put("openId", userInfoJSON.get("openId"));
            //userInfo.put("nickName", userInfoJSON.get("nickName"));
            //userInfo.put("gender", userInfoJSON.get("gender"));
            //userInfo.put("city", userInfoJSON.get("city"));
            //userInfo.put("province", userInfoJSON.get("province"));
            //userInfo.put("country", userInfoJSON.get("country"));
            //userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl"));
            // 解密unionId & openId;
            //System.out.println(userInfoJSON);
            //userInfo.put("unionId", userInfoJSON.get("unionId"));

           //3 . 先从本地数据库中查找用户是否存在
            //WxAccount wxAccount = wxAccountRepository.findByWxOpenid(response.getOpenid());
            //if (wxAccount == null) {
            // wxAccount = new WxAccount();
            //wxAccount.setWxOpenid(response.getOpenid());    //不存在就新建用户
            //}
            //4 . 更新sessionKey和 登陆时间
            //wxAccount.setSessionKey(response.getSession_key())
            // wxAccountRepository.save(wxAccount);
        }
        return new TokenDTO("");
    }
}
