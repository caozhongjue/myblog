package top.caozhongjue.dto;

import lombok.Data;
import top.caozhongjue.pojo.User;

@Data
public class QuestionDTO {
    private Integer id;
    private String title;
    private String description;
    private String tags;
    private String gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
}
