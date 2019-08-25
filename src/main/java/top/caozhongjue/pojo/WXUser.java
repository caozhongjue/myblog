package top.caozhongjue.pojo;

import lombok.Data;

@Data
public class WXUser {
    private String openId;
    private String nickName;
    private String avatarUrl;
    private String city;
    private String gender;
    private String province;
    private String country;
}
