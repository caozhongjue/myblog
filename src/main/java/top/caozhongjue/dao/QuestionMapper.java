package top.caozhongjue.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;
import top.caozhongjue.dto.PaginationDTO;
import top.caozhongjue.pojo.Collect;
import top.caozhongjue.pojo.Collect1;
import top.caozhongjue.pojo.Question;

import java.util.List;

@Mapper
public interface QuestionMapper {
    //添加问题
    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,comment_count,view_count,like_count,tags) " +
            "values(#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{commentCount},#{viewCount},#{likeCount},#{tags})")
    public void create(Question question);
    //查询问题
    @Select("select id,title,description,gmt_modified,creator,comment_count,view_count,like_count,tags ,date_format(FROM_UNIXTIME(GMT_CREATE/1000),'%Y-%m-%d') as gmt_create from question ORDER BY gmt_create desc limit #{offset},#{size} ;")
    List<Question> listQuestin(@Param("offset") Integer offset, @Param("size") Integer size);
    @Select("select count(1) from question")
    Integer count();
    //个人信息中的我的问题
    @Select("select id,title,description,gmt_modified,creator,comment_count,view_count,like_count,tags ,date_format(FROM_UNIXTIME(GMT_CREATE/1000),'%Y-%m-%d') as gmt_create " +
            "from question where creator = #{userId}  ORDER BY gmt_create desc limit #{offset},#{size}")
    List<Question> list(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("size") Integer size);
    @Select("select count(1) from question where creator = 1")
    Integer count2();
    //微信小程序中的一个问题
    @Select("select id,title,description,gmt_modified,creator,comment_count,view_count,like_count,tags ,date_format(FROM_UNIXTIME(GMT_CREATE/1000),'%Y-%m-%d') as gmt_create from question where id = #{id}")
    Question selectQuestionById(@Param("id") Integer id);
    @Update("update question set view_count = (view_count+ 1) where id = #{id}")
    void incView(@Param("id") Integer id);
    @Insert("insert into collect (openid ,qid) value (#{openid},#{id})")
    void addCollect(@Param("id")String id, @Param("openid")String openid);

    @Delete("delete from collect where openid = #{openid} and qid=#{id}")
    void deleteCollect(@Param("id")String id, @Param("openid")String openid);
    @Update("update question set like_count = (like_count+ 1) where id = #{id}")
    void addLike(@Param("id")String id);
    @Update("update question set like_count = (like_count- 1) where id = #{id}")
    void deleteLike(@Param("id")String id);
    @Select("select * from collect where openid=#{openid} and qid = #{id}")
    Collect1 selectCollectById(@Param("id")String id, @Param("openid")String openid);

    //查询问题
    @Select("select * from collect where openid = #{openid}")
    List<Collect> selectMyCollectByOpenid(@Param("openid") String openid);
    //编辑问题，通过id
    @Select("select id,title,description,gmt_modified,creator,comment_count,view_count,like_count,tags ,date_format(FROM_UNIXTIME(GMT_CREATE/1000),'%Y-%m-%d') as gmt_create from question where id = #{id}")
    Question getById(@Param("id")Integer id);
    //登录后概据文章id更新内容
    @Update("update question set id=#{id},title=#{title},description=#{description},gmt_modified=#{gmtModified},tags=#{tags} where id = #{id}")
    void createOrUpdate(Question question);

}
