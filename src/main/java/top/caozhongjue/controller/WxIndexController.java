package top.caozhongjue.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import top.caozhongjue.pojo.*;
import top.caozhongjue.provider.AesCbcUtil;
import top.caozhongjue.provider.HttpRequest;
import top.caozhongjue.services.QuestionService;
import top.caozhongjue.services.UserService;

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
//      List<QuestionDTO> listQuestions = questionService.listQuestionDTO(page,size);
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
    @RequestMapping("/login")
    @ResponseBody
    public Map login(@RequestParam("encryptedData")String encryptedData,
                      @RequestParam("iv")String iv,
                      @RequestParam("code")String code){
        Map map = new HashMap();
        // 登录凭证不能为空
        if (code == null || code.length() == 0) {
            map.put("status", 0);
            map.put("msg", "code 不能为空");
            return map;
        }
        // 小程序唯一标识 (在微信小程序管理后台获取)
        String wxspAppid = "wx13fa6457d04c0e65";
        // 小程序的 app secret (在微信小程序管理后台获取)
        String wxspSecret = "a745cf0a2a3c90e727534372f968d854";
        // 授权（必填）
        String grant_type = "authorization_code";
        //////////////// 1、向微信服务器 使用登录凭证 code 获取 session_key 和 openid
        // 请求参数
        String params = "appid=" + wxspAppid + "&secret=" + wxspSecret + "&js_code=" + code + "&grant_type="
                + grant_type;
        // 发送请求
        String sr = HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);
        // 解析相应内容（转换成json对象）
        JSONObject json = JSONObject.parseObject(sr);
        // 获取会话密钥（session_key）
        String session_key = json.get("session_key").toString();
        // 用户的唯一标识（openid）
        String openid = (String) json.get("openid");
        //////////////// 2、对encryptedData加密数据进行AES解密 ////////////////
        try {
            String result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
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
                userService.createOrUpdateUser(user);
                //返回解密的数据给小程序
                map.put("status", 1);
                map.put("msg", "解密成功");
                JSONObject userInfoJSON = JSONObject.parseObject(result);
                Map userInfo = new HashMap();
                userInfo.put("openId", userInfoJSON.get("openId"));
                userInfo.put("nickName", userInfoJSON.get("nickName"));
                userInfo.put("gender", userInfoJSON.get("gender"));
                userInfo.put("city", userInfoJSON.get("city"));
                userInfo.put("province", userInfoJSON.get("province"));
                userInfo.put("country", userInfoJSON.get("country"));
                userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl"));
                // 解密unionId & openId;
                System.out.println( userInfoJSON);
                userInfo.put("unionId", userInfoJSON.get("unionId"));
                map.put("userInfo", userInfo);

            } else {
                map.put("status", 0);
                map.put("msg", "解密失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;

    }
    @RequestMapping("/addCollect")
    @ResponseBody
    public void addCollect(@RequestParam("id")String id,@RequestParam("openid")String openid){
        questionService.addCollect(id,openid);
        System.out.println(id+"  "+openid);
    }
    @RequestMapping("/deleteCollect")
    @ResponseBody
    public void deleteCollect(@RequestParam("id")String id,@RequestParam("openid")String openid){
        questionService.deleteCollect(id,openid);
        System.out.println(id+"  "+openid);
    }
    @RequestMapping("/addLike")
    @ResponseBody
    public void addLike(@RequestParam("id")String id,@RequestParam("openid")String openid){
        questionService.addLike(id);
    }
    @RequestMapping("/deleteLike")
    @ResponseBody
    public void  deleteLike(@RequestParam("id")String id,@RequestParam("openid")String openid){
        questionService.deleteLike(id);
        System.out.println(id+openid);
    }
    @RequestMapping("/selectCollectById")
    @ResponseBody
    public Map selectCollectById(@RequestParam("id")String id,@RequestParam("openid")String openid){
        Map map = new HashMap();
        Collect1 collet = questionService.selectCollectById(id,openid);
        if(collet != null){
            map.put("code",1);
        }else{
            map.put("code",0);
        }
        return map;
    }
    @RequestMapping("selectMyCollectByOpenid")
    @ResponseBody
    public List<Question> selectMyCollectByOpenid(@RequestParam("openid") String openid) {
//      List<QuestionDTO> listQuestions = questionService.listQuestionDTO(page,size);
        List<Question> questions = questionService.selectMyCollectByOpenid(openid);
        return questions;
    }
}
