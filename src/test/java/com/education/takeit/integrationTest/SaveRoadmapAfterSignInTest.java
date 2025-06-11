package com.education.takeit.integrationTest;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testec2")
@Transactional
public class SaveRoadmapAfterSignInTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoadmapManagementRepository roadmapManagementRepository;

    @Test
    void 회원가입_및_로그인_후_사전진단과_로드맵_생성_및_저장() throws Exception {
        // 1. 회원가입
       String email = "test@test.com";
       String nickname = "test";
       String password = "test1234!";
       String loginType = "LOCAL";

        String signUpJson = """
        {
          "email" : "%s",
          "nickname" : "%s",
          "password" : "%s",
          "loginType" : "%s"
        }
        """.formatted(email, nickname, password, loginType);

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpJson))
                .andExpect(status().isOk());

        assertThat(userRepository.findByEmailAndLoginType(email, LoginType.LOCAL)).isPresent();

        // 2. 로그인
        String signInJson = """
        {
            "email" : "%s",
            "password" : "%s"
        }
        """.formatted(email, password);

        String signinResponse = mockMvc.perform(post("/api/user/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = JsonPath.read(signinResponse, "$.data.accessToken");

        // 3. 사전진단 문제 요청
        mockMvc.perform(get("/api/diagnosis")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // 4. 사전진단 응답 제출
        String diagnosisSubmitJson = """
         [
            {
                "questionId" : 1,
                "answer" : "BE"
            },
            {
                "questionId" : 2,
                "answer" : "0"
            },
            {
                "questionId" : 3,
                "answer" : "0"
            },
            {
                "questionId" : 4,
                "answer" : "N"
            },
            {
                "questionId" : 11,
                "answer" : "Java/Spring"
            },
            {
                "questionId" : 12,
                "answer" : "N"
            },
            {
                "questionId" : 13,
                "answer" : "N"
            },
            {
                "questionId" : 14,
                "answer" : "N"
            },
            {
                "questionId" : 15,
                "answer" : "N"
            }
         ]
         """;

        mockMvc.perform(post("/api/diagnosis")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(diagnosisSubmitJson))
                .andExpect(status().isOk());

        // 5. 로드맵이 저장됐는지 확인
        User user = userRepository.findByEmailAndLoginType(email, LoginType.LOCAL)
                .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
        Long userId = user.getUserId();

        assertThat(roadmapManagementRepository.findByUserId(userId)).isNotNull();
    }
}
