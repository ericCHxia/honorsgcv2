package cn.honorsgc.honorv2.article;

import cn.honorsgc.honorv2.article.expection.*;
import cn.honorsgc.honorv2.core.ErrorEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ArticleExceptionHandler {
    @ExceptionHandler(ArticleAccessDeniedException.class)
    public ErrorEnum articleAccessDeniedHandler(){
        return ArticleErrorEnum.ACCESS_DENIED;
    }
    @ExceptionHandler(ArticleNotFoundException.class)
    public ErrorEnum articleNotFoundHandler(){
        return ArticleErrorEnum.NOT_FOUND;
    }
    @ExceptionHandler(ArticleIllegalParameterException.class)
    public ErrorEnum articleIllegalParameterHandler(ArticleIllegalParameterException e){
        return ArticleErrorEnum.ILLEGAL_PARAMETER.setErrorMsg(e.getMessage());
    }
    @ExceptionHandler(TagIsExistException.class)
    public ErrorEnum tagIsExist(){
        return ArticleErrorEnum.TAG_IS_EXIST;
    }
    @ExceptionHandler(TagCountIsNotEmpty.class)
    public ErrorEnum tagCountIsNotEmpty(){
        return ArticleErrorEnum.TAG_COUNT_IS_NOT_EMPTY;
    }
}
