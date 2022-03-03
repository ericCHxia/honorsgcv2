package cn.honorsgc.honorv2.article;

import cn.honorsgc.honorv2.article.enity.Tag;
import cn.honorsgc.honorv2.article.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {
    @Autowired
    private TagRepository tagRepository;
    public List<Tag> getTagLimit(int limit){
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(0,limit,sort);
        return tagRepository.findAll(pageable).getContent();
    }
}
