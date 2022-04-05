package cn.honorsgc.honorv2.hduhelper;

import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import cn.honorsgc.honorv2.jwt.ConstantKey;
import cn.honorsgc.honorv2.jwt.JWTTokenResponse;
import cn.honorsgc.honorv2.user.User;
import cn.honorsgc.honorv2.user.UserRepository;
import cn.honorsgc.honorv2.user.UserService;
import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class HduHelperControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository repository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    String oauth2login(String code) throws Exception {
        String result = mockMvc.perform(get("/hduhelper")
                        .param("code",code)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(HduHelperController.class))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.token",notNullValue()))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(result,"$.data.token");
    }

    @Test
    void oauth2loginTeacher() throws Exception {
        String  token  = oauth2login("91f9be98-634c-4040-a233-8dc3afc368df");
        Claims claims = Jwts.parser().setSigningKey(ConstantKey.SIGNING_KEY).parseClaimsJws(token.replace("Bearer ", "")).getBody();
        Optional<User> optionalUser = repository.findById(Long.parseLong(claims.getSubject()));
        assertTrue(optionalUser.isPresent());
        assertEquals(optionalUser.get().getUserId(),"41248");
    }

    @Test
    void oauth2loginStudent() throws Exception {
        String  token  = oauth2login("5557c684-263c-40a5-a8eb-267839d409c4");
        Claims claims = Jwts.parser().setSigningKey(ConstantKey.SIGNING_KEY).parseClaimsJws(token.replace("Bearer ", "")).getBody();
        Optional<User> optionalUser = repository.findById(Long.parseLong(claims.getSubject()));
        assertTrue(optionalUser.isPresent());
        assertEquals(optionalUser.get().getUserId(),"19051131");
        assertEquals(optionalUser.get().getPhone(),"18342896685");
        assertEquals(optionalUser.get().getClassId(),"19185311");
    }
}