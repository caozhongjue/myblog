package top.caozhongjue.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;//存放数据
    private boolean hasPrevious;//上一页
    private boolean hasNexts;//下一页
    private boolean hasFirstPage;//第一页
    private boolean hasEndPage;//最后一页

    private Integer currentPage;//当前页
    private List<Integer> pages = new ArrayList<>();//
    private Integer totalPages;
    public void setPagination(Integer totalCount, Integer page, Integer size) {
        //查总共多少页
        if(totalCount % size ==0){
            totalPages = totalCount / size;
        }else{
            totalPages = totalCount / size +1;
        }
        //防止超过页数据还显示
        if (page<1){
            page=1;
        }
        if(page>totalPages){
            page=totalPages;
        }
        this.currentPage=page;
        pages.add(page);
        for(int i = 1; i <= 3 ; i ++){
            if(page-i>0){
                pages.add(0,page-i);
            }
            if(page+i<=totalPages){
                pages.add(page+i);
            }
        }
        //是否展示上一页
        if(page == 1){
            hasPrevious =false;
        }else{
            hasPrevious = true;
        }
        //是否展示下一页
        if(page == totalPages){
            hasNexts = false;
        }else{
            hasNexts = true;
        }
        //是否展示第一页
        if(pages.contains(1)){
            hasFirstPage = false;
        }else{
            hasFirstPage = true;
        }
        //是否展示最后一页
        if(pages.contains(totalPages)){
            hasEndPage = false;
        }else{
            hasEndPage = true;
        }
    }
}
