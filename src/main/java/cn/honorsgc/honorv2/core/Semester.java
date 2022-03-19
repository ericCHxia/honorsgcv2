package cn.honorsgc.honorv2.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    public static Semester valuesOf(Integer id){
        String name = String.format("%d-%d学年 第%s学期",id/2+2019,id/2+2019+1,(id%2>0)?"二":"一");
        return new Semester(id,name);
    }
    @JsonIgnore
    public Date getBegin(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,(id+1)/2+2019);
        calendar.set(Calendar.MONTH,id%2>0?2:8);
        calendar.set(Calendar.DATE,0);
        return calendar.getTime();
    }
    @JsonIgnore
    public Date getEnd(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,id/2+2019+1);
        calendar.set(Calendar.MONTH,id%2>0?8:2);
        calendar.set(Calendar.DATE,0);
        return calendar.getTime();
    }
}
