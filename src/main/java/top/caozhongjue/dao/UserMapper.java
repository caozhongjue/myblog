package top.caozhongjue.dao;
import org.apache.ibatis.annotations.*;
import top.caozhongjue.pojo.Question;
import top.caozhongjue.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {
    //第一次授权登录时插入数据
    @Insert("insert into user (account_id,name,token,gmt_create,gmt_modified,avatar_url,gender,province,city) values (#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl},#{gender},#{province},#{city})")
    public void insert(User user);
    @Select("select * from user where token = #{token}")
    User findByToken(@Param("token") String token);
    //通过id查询用户
    @Select("select * from user where id = #{id}")
    User findById(@Param("id") Integer id);
    //第一次授权登录时通过accountId查询用户是否存在
    @Select("select * from user where account_id = #{accountId}")
    User findByAcountId(@Param("accountId") String accountId);
    @Update("update user set name=#{name},token=#{token},gmt_modified=#{gmtModified},avatar_url=#{avatarUrl} where id = #{id} ")
    void update(User user);
    @Select("select id,title,description,gmt_modified,creator,comment_count,view_count,like_count,tags ,date_format(FROM_UNIXTIME(GMT_CREATE/1000),'%Y-%m-%d') as gmt_create from question  where id = #{qid} ORDER BY gmt_create desc")
    List<Question> findByQId(@Param("qid") String qid);
}
