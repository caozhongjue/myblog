package top.caozhongjue.dao;
import org.apache.ibatis.annotations.*;
import top.caozhongjue.pojo.User;

@Mapper
public interface UserMapper {
    @Insert("insert into user (account_id,name,token,gmt_create,gmt_modified,avatar_url) values (#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    public void insert(User user);
    @Select("select * from user where token = #{token}")
    User findByToken(@Param("token") String token);
    //通过id查询用户
    @Select("select * from user where id = #{id}")
    User findById(@Param("id") Integer id);
    //通过accountId查询用户是否存在
    @Select("select * from user where account_id = #{accountId}")
    User findByAcountId(@Param("accountId") String accountId);
    @Update("update user set name=#{name},token=#{token},gmt_modified=#{gmtModified},avatar_url=#{avatarUrl} where id = #{id} ")
    void update(User user);

}
