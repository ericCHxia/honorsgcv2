package cn.honorsgc.honorv2.hduhelper.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HduHelperResponse<T> implements Serializable {
    private boolean cache;
    private T data;
    private int error;
    private String msg;
}
