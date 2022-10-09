package cn.honorsgc.honorv2.core;

import cn.honorsgc.honorv2.ResultInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GlobalResponseEntity<T> {
    private Boolean success = true;
    private Integer code = 0;
    private String message = "request successfully";
    private T data;

    public GlobalResponseEntity(Integer code, String message) {
        this.code = code;
        success = code==0;
        this.message = message;
    }

    public GlobalResponseEntity(T data) {
        this.data = data;
    }

    public GlobalResponseEntity(ResultInfo info, T data) {
        this.code = info.getCode();
        this.success = code == 0;
        this.message = info.getMessage();
        this.data = data;
    }
}
