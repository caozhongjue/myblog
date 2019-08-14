package top.caozhongjue.pojo;

import lombok.Data;

@Data
public class Access_token {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;


}
