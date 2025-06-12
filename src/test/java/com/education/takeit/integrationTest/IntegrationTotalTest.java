package com.education.takeit.integrationTest;

//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("testec2")
//@Transactional
//public class IntegrationTotalTest {
//
//  @Autowired private MockMvc mockMvc;
//
//  @Autowired private UserRepository userRepository;
//
//  @Autowired private RoadmapManagementRepository roadmapManagementRepository;
//
//  @Test
//  void 전체_사용자_플로우_성공() throws Exception {
//    // 1. 회원가입
//    String email = "test@test.com";
//    String nickname = "test";
//    String password = "test1234!";
//    String loginType = "LOCAL";
//
//    String signUpJson =
//        """
//        {
//          "email" : "%s",
//          "nickname" : "%s",
//          "password" : "%s",
//          "loginType" : "%s"
//        }
//        """
//            .formatted(email, nickname, password, loginType);
//
//    mockMvc
//        .perform(
//            post("/api/user/signup").contentType(MediaType.APPLICATION_JSON).content(signUpJson))
//        .andExpect(status().isOk());
//
//    assertThat(userRepository.findByEmailAndLoginType(email, LoginType.LOCAL)).isPresent();
//
//    // 2. 로그인
//    String signInJson =
//        """
//        {
//            "email" : "%s",
//            "password" : "%s"
//        }
//        """
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
//    String accessToken = JsonPath.read(signinResponse, "$.data.accessToken");
//
//    // 3. 사전진단 문제 요청
//    mockMvc
//        .perform(get("/api/diagnosis").header("Authorization", "Bearer " + accessToken))
//        .andExpect(status().isOk());
//
//    // 4. 사전진단 응답 제출
//    String diagnosisSubmitJson =
//        """
//         [
//            {
//                "questionId" : 1,
//                "answer" : "BE"
//            },
//            {
//                "questionId" : 2,
//                "answer" : "0"
//            },
//            {
//                "questionId" : 3,
//                "answer" : "0"
//            },
//            {
//                "questionId" : 4,
//                "answer" : "N"
//            },
//            {
//                "questionId" : 11,
//                "answer" : "Java/Spring"
//            },
//            {
//                "questionId" : 12,
//                "answer" : "N"
//            },
//            {
//                "questionId" : 13,
//                "answer" : "N"
//            },
//            {
//                "questionId" : 14,
//                "answer" : "N"
//            },
//            {
//                "questionId" : 15,
//                "answer" : "N"
//            }
//         ]
//         """;
//
//    mockMvc
//        .perform(
//            post("/api/diagnosis")
//                .header("Authorization", "Bearer " + accessToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(diagnosisSubmitJson))
//        .andExpect(status().isOk());
//
//    // 5. 로드맵이 저장됐는지 확인
//    User user =
//        userRepository
//            .findByEmailAndLoginType(email, LoginType.LOCAL)
//            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
//    Long userId = user.getUserId();
//
//    assertThat(roadmapManagementRepository.findByUserId(userId)).isNotNull();
//
//    // 6. 로드맵 조회
//    String getRoadmapResponse = mockMvc
//            .perform(
//                    get("/api/roadmap")
//                            .header("Authorization", "Bearer " + accessToken)
//                            .param("uuid", "takeit"))
//            .andExpect(status().isOk())
//            .andReturn()
//            .getResponse()
//            .getContentAsString();
//
//    // 7. 과목 정보 조회
//    Integer subjectId = JsonPath.read(getRoadmapResponse, "$.data.subjects[0].subjectId");
//
//    mockMvc.perform(get("/api/roadmap/subject")
//                    .header("Authorization", "Bearer " + accessToken)
//                    .param("subjectId", subjectId.toString()))
//            .andExpect(status().isOk());

    // 8. 사전 평가 문제 조회
//    String getPreExamQuestions = mockMvc.perform(get("/api/exam/pre")
//                    .header("Authorization", "Bearer " + accessToken)
//                    .param("subjectId", subjectId.toString()))
//            .andExpect(status().isOk())
//            .andReturn()
//            .getResponse()
//            .getContentAsString();

    // 9. 사전 평가 결과 제출

//  }
//}
