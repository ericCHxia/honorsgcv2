package cn.honorsgc.honorv2.core;

import lombok.Data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Data
public class Semester implements Serializable {
    private final Integer id;
    private final String name;
    public static Semester valuesOf(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int mouth = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR)-(mouth<9?1:0);
        int id = (year-2019)*2;
        if (mouth>=2&&mouth<8) id++;
        String name = String.format("%d-%d学年 第%s学期",year,year+1,(mouth>=2&&mouth<8)?"二":"一");
        return new Semester(id,name);
    }
}
