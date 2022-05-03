package cn.honorsgc.honorv2.core;

import java.util.List;

public class ExcelBase<T> {
    public final List<T> data;
    public final Class<?> header;

    public ExcelBase(List<T> data,Class<?> header) {
        this.data = data;
        this.header = header;
    }
}
