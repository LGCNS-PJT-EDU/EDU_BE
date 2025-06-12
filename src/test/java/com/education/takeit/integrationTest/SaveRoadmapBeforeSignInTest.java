package com.education.takeit.integrationTest;

//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("testec2")
//@Transactional
//public class SaveRoadmapBeforeSignInTest {
//  @Autowired private MockMvc mockMvc;
//  @Autowired private UserRepository userRepository;
//  @Autowired private RoadmapManagementRepository roadmapManagementRepository;
//
//  @Test
//  void 사전진단을_진행_후_회원가입_및_로그인() throws Exception {
//    // 1. 사전진단 문제 요청
//    mockMvc.perform(get("/api/diagnosis")).andExpect(status().isOk());
//
//    // 2. 사전 진단 응답 제출
//    String diagnosisSubmitJson =
//        """
//                 [
//                    {
//                        "questionId" : 1,
//                        "answer" : "BE"
//                    },
//                    {
//                        "questionId" : 2,
//                        "answer" : "0"
//                    },
//                    {
//                        "questionId" : 3,
//                        "answer" : "0"
//                    },
//                    {
//                        "questionId" : 4,
//                        "answer" : "N"
//                    },
//                    {
//                        "questionId" : 11,
//                        "answer" : "Java/Spring"
//                    },
//                    {
//                        "questionId" : 12,
//                        "answer" : "N"
//                    },
//                    {
//                        "questionId" : 13,
//                        "answer" : "N"
//                    },
//                    {
//                        "questionId" : 14,
//                        "answer" : "N"
//                    },
//                    {
//                        "questionId" : 15,
//                        "answer" : "N"
//                    }
//                 ]
//                 """;
//
//    String roadmapResponse =
//        mockMvc
//            .perform(
//                post("/api/diagnosis")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(diagnosisSubmitJson))
//            .andExpect(status().isOk())
//            .andReturn()
//            .getResponse()
//            .getContentAsString();
//
//    String uuid = JsonPath.read(roadmapResponse, "$.data.uuid");
//
//    // 3. 회원가입
//    String email = "test@test.com";
//    String nickname = "test";
//    String password = "test1234!";
//    String loginType = "LOCAL";
//
//    String signUpJson =
//        """
//                {
//                  "email" : "%s",
//                  "nickname" : "%s",
//                  "password" : "%s",
//                  "loginType" : "%s"
//                }
//                """
//            .formatted(email, nickname, password, loginType);
//
//    mockMvc
//        .perform(
//            post("/api/user/signup").contentType(MediaType.APPLICATION_JSON).content(signUpJson))
//        .andExpect(status().isOk());
//
//    assertThat(userRepository.findByEmailAndLoginType(email, LoginType.LOCAL)).isPresent();
//
//    // 4. 로그인
//    String signInJson =
//        """
//                {
//                    "email" : "%s",
//                    "password" : "%s"
//                }
//                """
//            .formatted(email, password);
//
//    String signinResponse =
//        mockMvc
//            .perform(
//                post("/api/user/signin")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(signInJson))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
//            .andReturn()
//            .getResponse()
//            .getContentAsString();
//
//    System.out.println("로그인 응답 : " + signinResponse);
//
//    String accessToken = JsonPath.read(signinResponse, "$.data.accessToken");
//
//    // 5. 로드맵 저장 및 조회
//    mockMvc
//        .perform(
//            get("/api/roadmap")
//                .header("Authorization", "Bearer " + accessToken)
//                .param("uuid", uuid))
//        .andExpect(status().isOk());
//
//    User user =
//        userRepository
//            .findByEmailAndLoginType(email, LoginType.LOCAL)
//            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
//    Long userId = user.getUserId();
//
//    assertThat(roadmapManagementRepository.findByUserId(userId)).isNotNull();
//  }
//}
