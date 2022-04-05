package cn.honorsgc.honorv2.hduhelper;

import cn.honorsgc.honorv2.hduhelper.dto.HduHelperPersonInfo;
import cn.honorsgc.honorv2.hduhelper.dto.HduHelperToken;
import cn.honorsgc.honorv2.hduhelper.exception.HduHelperException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HduHelperServiceTest {
    @Autowired
    HduHelperService service;

    @Test
    void getToken() throws HduHelperException  {
        HduHelperToken token=service.getToken("91f9be98-634c-4040-a233-8dc3afc368df");
        assertEquals(token.getStaffId(),"41248");
    }

    @Test
    void getPersonInfo() throws HduHelperException {
        HduHelperPersonInfo personInfo = service.getPersonInfo("e9340599-4585-4221-9de5-31f4672e884e");
        assertEquals(personInfo.getStaffId(),"41248");
    }
}