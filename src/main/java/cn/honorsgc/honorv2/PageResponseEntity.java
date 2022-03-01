package cn.honorsgc.honorv2;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponseEntity<T> {
    int page;
    int totalItem;
    int totalPage;
    List<T> items;

    public static <T> PageResponseEntity<T> fromPage(Page<T> page){
        PageResponseEntity<T> pageResponseEntity = new PageResponseEntity<>();
        pageResponseEntity.setItems(page.getContent());
        pageResponseEntity.setPage(page.getNumber());
        pageResponseEntity.setTotalPage(page.getTotalPages());
        return pageResponseEntity;
    }
}
