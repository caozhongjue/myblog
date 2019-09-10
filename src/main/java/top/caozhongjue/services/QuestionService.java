package top.caozhongjue.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caozhongjue.dao.QuestionMapper;
import top.caozhongjue.dao.UserMapper;
import top.caozhongjue.dto.PaginationDTO;
import top.caozhongjue.dto.QuestionDTO;
import top.caozhongjue.pojo.Collect;
import top.caozhongjue.pojo.Collect1;
import top.caozhongjue.pojo.Question;
import top.caozhongjue.pojo.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;


    public PaginationDTO listQuestionDTO(Integer page, Integer size) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTOS = new PaginationDTO();
        Integer totalCount = questionMapper.count();
        paginationDTOS.setPagination(totalCount,page,size);
        //防止超过页数据还显示
        if (page<1){
            page=1;
        }
        if(page>paginationDTOS.getTotalPages()&!paginationDTOS.getTotalPages().equals(0)){
            page=paginationDTOS.getTotalPages();
        }
        // mysql 分页 limit 0,5 从第0条数据开始，查出5条,第一页0-5 ，第二页5-10 ,第三页10-15
        Integer offset = (page-1)*size;
        List<Question> questions = questionMapper.listQuestin(offset,size);
        for(Question question:questions){
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTOS.setQuestions(questionDTOList);

        //return questionDTOList;
        return paginationDTOS;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTOS = new PaginationDTO();
        Integer totalCount = questionMapper.count2();
        paginationDTOS.setPagination(totalCount,page,size);
        //防止超过页数据还显示
        if (page<1){
            page=1;
        }
        if(page>paginationDTOS.getTotalPages()&!paginationDTOS.getTotalPages().equals(0)){
            page=paginationDTOS.getTotalPages();
        }
        // mysql 分页 limit 0,5 从第0条数据开始，查出5条,第一页0-5 ，第二页5-10 ,第三页10-15
        Integer offset = (page-1)*size;
        List<Question> questions = questionMapper.list(userId,offset,size);
        for(Question question:questions){
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTOS.setQuestions(questionDTOList);
        //return questionDTOList;
        return paginationDTOS;
    }
    //问题详情页services
    public QuestionDTO selectQuestionById(Integer id) {
        Question question = questionMapper.selectQuestionById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.findById(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void incView(Integer id) {
        questionMapper.incView(id);
    }

    public void addCollect(String id, String token) {

        questionMapper.addCollect(id,token);
    }

    public void deleteCollect(String id, String token) {
        questionMapper.deleteCollect(id,token);
    }

    public void addLike(String id) {
        questionMapper.addLike(id);
    }

    public void deleteLike(String id) {
        questionMapper.deleteLike(id);
    }

    public Collect1 selectCollectById(String id, String openid) {
        return questionMapper.selectCollectById(id,openid);
    }

    public List<QuestionDTO> selectMyCollectByOpenid(String token) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        List<Collect> collects = questionMapper.selectMyCollectByOpenid(token);
        for(Collect collect : collects){
            List<Question> questions = userMapper.findByQId(collect.getQid());
            for(Question question : questions) {
                User user = userMapper.findById(question.getCreator());
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question,questionDTO);
                questionDTO.setUser(user);
                questionDTOList.add(questionDTO);

            }
        }
        return questionDTOList;
    }

    public void createOrUpdate(Question question,User user) {
        if(question.getId() == null) {
            question.setCreator(user.getId());
//          question.setGmtCreate(System.currentTimeMillis());
//          question.setGmtModified(question.getGmtCreate());
            question.setGmtCreate(Long.toString(System.currentTimeMillis()));//Long类型转String
            question.setGmtModified(Long.parseLong(question.getGmtCreate()));//String转Long类型
            question.setCommentCount(0);
            question.setLikeCount(0);
            question.setViewCount(0);
            questionMapper.create(question);
        }else{
            question.setCreator(user.getId());
            question.setGmtModified(System.currentTimeMillis());//String转Long类型
            questionMapper.createOrUpdate(question);
        }
    }
}
