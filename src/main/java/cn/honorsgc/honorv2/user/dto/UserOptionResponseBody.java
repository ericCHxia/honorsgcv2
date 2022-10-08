package cn.honorsgc.honorv2.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserOptionResponseBody {
    private Set<String> classId;
    private Set<String> college;
    private Set<String> subject;
}
