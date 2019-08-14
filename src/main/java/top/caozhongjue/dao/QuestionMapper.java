package top.caozhongjue.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.caozhongjue.pojo.Question;

import java.util.List;

@Mapper
public interface QuestionMapper {
    //添加问题
    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,comment_count,view_count,like_count,tags) " +
            "values(#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{commentCount},#{viewCount},#{likeCount},#{tags})")
    public void create(Question question);
    //查询问题
    @Select("select * from question limit #{offset},#{size}")
    List<Question> listQuestin(@Param("offset") Integer offset, @Param("size") Integer size);
    @Select("select count(1) from question")
    Integer count();
}
