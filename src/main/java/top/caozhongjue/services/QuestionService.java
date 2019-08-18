package top.caozhongjue.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caozhongjue.dao.QuestionMapper;
import top.caozhongjue.dao.UserMapper;
import top.caozhongjue.dto.PaginationDTO;
import top.caozhongjue.dto.QuestionDTO;
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
}
