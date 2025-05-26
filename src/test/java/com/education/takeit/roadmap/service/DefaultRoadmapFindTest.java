package com.education.takeit.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.dto.SubjectDto;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.entity.Track;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultRoadmapFindTest {

  @InjectMocks private RoadmapService roadmapService;

  @Mock private RoadmapRepository roadmapRepository;

  @Test
  @DisplayName("getDefaultFERoadmap() -> 프론트 기본 로드맵 반환")
  void getDefaultRoadmap_FE() {
    // given
    Track track = Track.builder().trackId(1L).trackNm("기초지식").build();

    Subject subject1 =
        Subject.builder()
            .subId(1L)
            .subNm("HTML")
            .subType("FE")
            .subEssential("Y")
            .baseSubOrder(1)
            .subOverview(
                "HTML이란? 웹페이지의 뼈대!웹사이트에서 내용을 담당해요.예를 들면, 제목, 글, 버튼, 이미지, 표 같은 걸 HTML로 만들어줍니다.마치 건물의 설계도 같다고 생각하면 돼요. 벽이 어디 있는지, 문은 어디 있는지 알려주는 거죠.")
            .track(track)
            .build();

    Subject subject2 =
        Subject.builder()
            .subId(2L)
            .subNm("CSS")
            .subType("FE")
            .subEssential("Y")
            .baseSubOrder(2)
            .subOverview(
                "CSS란? 웹페이지에 스타일을 입히는 도구!색깔, 크기, 글꼴, 위치 같은 디자인 요소를 조절해요.예를 들면, 글자의 색을 바꾸거나 버튼의 모양을 다듬고,이미지 간 간격을 조정하고 배경 색을 설정할 수 있어요.마치 집 안을 꾸미는 인테리어처럼,보는 사람이 더 편하고 예쁘게 느낄 수 있도록 만드는 역할을 해요.")
            .track(track)
            .build();

    Subject subject3 =
        Subject.builder()
            .subId(3L)
            .subNm("JavaScript")
            .subType("FE")
            .subEssential("Y")
            .baseSubOrder(3)
            .subOverview(
                "JavaScript란? 웹페이지를 움직이게 하는 도구!클릭, 입력, 스크롤 같은 사용자 행동에 반응하게 만들어요.예를 들면, 버튼을 누르면 팝업이 뜨거나,입력한 정보를 검사하고, 이미지가 자동으로 바뀌는 기능을 만들 수 있어요.마치 리모컨을 눌렀을 때 TV가 반응하듯,웹페이지에 동작과 반응을 더해주는 역할을 해요.")
            .track(track)
            .build();

    Roadmap roadmap1 =
        Roadmap.builder()
            .roadmapId(1L)
            .orderSub(1)
            .subject(subject1)
            .roadmapManagement(null)
            .isComplete(false)
            .preSubmitCount(0)
            .postSubmitCount(0)
            .level(0)
            .build();

    Roadmap roadmap2 =
        Roadmap.builder()
            .roadmapId(2L)
            .orderSub(2)
            .subject(subject2)
            .roadmapManagement(null)
            .isComplete(false)
            .preSubmitCount(0)
            .postSubmitCount(0)
            .level(0)
            .build();

    Roadmap roadmap3 =
        Roadmap.builder()
            .roadmapId(3L)
            .orderSub(3)
            .subject(subject3)
            .roadmapManagement(null)
            .isComplete(false)
            .preSubmitCount(0)
            .postSubmitCount(0)
            .level(0)
            .build();

    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(1L))
        .thenReturn(List.of(roadmap1, roadmap2, roadmap3));

    // when
    List<SubjectDto> result = roadmapService.getDefaultRoadmap("FE");

    // then
    assertEquals(3, result.size());
    assertEquals("HTML", result.get(0).subjectName());
    assertEquals("CSS", result.get(1).subjectName());
    assertEquals("JavaScript", result.get(2).subjectName());
  }

  @Test
  @DisplayName("getDefaultFERoadmap() -> 백엔드 기본 로드맵 반환")
  void getDefaultRoadmap_BE() {
    // given
    Track track1 = Track.builder().trackId(11L).trackNm("Linux & Internet").build();

    Track track2 = Track.builder().trackId(2L).trackNm("VCS").build();

    Subject subject1 =
        Subject.builder()
            .subId(35L)
            .subNm("Linux")
            .subType("BE")
            .subEssential("Y")
            .baseSubOrder(1)
            .subOverview(
                "리눅스 명령어란? 터미널에서 직접 컴퓨터를 조작할 수 있게 해주는 텍스트 기반의 도구! 파일 생성, 복사, 이동부터 시스템 관리, 프로세스 확인, 서버 설정까지 명령어 한 줄로 다양한 작업을 빠르고 정확하게 수행할 수 있어요. 예를 들면, ls로 폴더 목록을 확인하고, cd로 위치를 옮기거나, chmod, ps, top 같은 명령어로 권한과 시스템 상태를 관리할 수 있어요. 마치 명령 센터에서 키워드 하나로 기계를 제어하듯, 개발과 운영 환경에서 효율적이고 강력한 제어력을 제공해주는 기본 도구예요.")
            .track(track1)
            .build();

    Subject subject2 =
        Subject.builder()
            .subId(36L)
            .subNm("인터넷 & 네트워크")
            .subType("BE")
            .subEssential("Y")
            .baseSubOrder(2)
            .subOverview(
                "HTTP, HTTPS, DNS, TCP/IP 기본 개념이란? 인터넷이 어떻게 작동하는지를 이해하는 데 꼭 필요한 핵심 네트워크 개념들이에요! HTTP/HTTPS는 웹에서 정보를 주고받는 방식이고, DNS는 사람이 기억하기 쉬운 주소(도메인)를 숫자 IP로 바꿔주는 시스템이며, TCP/IP는 데이터가 안전하고 정확하게 목적지에 도착하도록 돕는 전달 규칙이에요. 예를 들면, 브라우저에 주소를 입력하면 DNS가 서버를 찾아주고, HTTP가 서버에 요청을 보내고, TCP/IP가 그 데이터를 안정적으로 전달해줘요. 마치 우편 주소를 찾아 편지를 보내고, 정확히 배달되는 전체 과정처럼, 웹과 앱이 원활하게 통신할 수 있도록 돕는 기본 원리들이에요.")
            .track(track1)
            .build();

    Subject subject3 =
        Subject.builder()
            .subId(37L)
            .subNm("Git & GitHub")
            .subType("BE")
            .subEssential("Y")
            .baseSubOrder(3)
            .subOverview(
                "Git & GitHub란? 코드를 안전하게 관리하고, 함께 작업할 수 있게 해주는 도구! 코드를 버전별로 저장하고, 이전 상태로 되돌리거나 여러 사람이 동시에 작업해도 충돌 없이 관리할 수 있어요. 예를 들면, 실수로 코드를 잘못 고쳐도 이전 상태로 복원할 수 있고, 팀원이 만든 기능을 병합하거나 비교할 수도 있어요. Git은 버전 관리 시스템, GitHub는 이 Git 저장소를 인터넷에 저장하고 협업할 수 있게 해주는 서비스라고 생각하면 돼요.")
            .track(track2)
            .build();

    Roadmap roadmap1 =
        Roadmap.builder()
            .roadmapId(27L)
            .orderSub(1)
            .subject(subject1)
            .roadmapManagement(null)
            .isComplete(false)
            .preSubmitCount(0)
            .postSubmitCount(0)
            .level(0)
            .build();

    Roadmap roadmap2 =
        Roadmap.builder()
            .roadmapId(28L)
            .orderSub(2)
            .subject(subject2)
            .roadmapManagement(null)
            .isComplete(false)
            .preSubmitCount(0)
            .postSubmitCount(0)
            .level(0)
            .build();

    Roadmap roadmap3 =
        Roadmap.builder()
            .roadmapId(29L)
            .orderSub(3)
            .subject(subject3)
            .roadmapManagement(null)
            .isComplete(false)
            .preSubmitCount(0)
            .postSubmitCount(0)
            .level(0)
            .build();

    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(2L))
        .thenReturn(List.of(roadmap1, roadmap2, roadmap3));

    // when
    List<SubjectDto> result = roadmapService.getDefaultRoadmap("BE");

    // then
    assertEquals(3, result.size());
    assertEquals("Linux", result.get(0).subjectName());
    assertEquals("인터넷 & 네트워크", result.get(1).subjectName());
    assertEquals("Git & GitHub", result.get(2).subjectName());
  }

  @Test
  @DisplayName("getDefaultRoadmapException() -> 기본 로드맵 반환 예외 처리")
  void getDefaultRoadmapException() {
    // when & then
    CustomException exception =
        assertThrows(CustomException.class, () -> roadmapService.getDefaultRoadmap("AI"));

    assertEquals(StatusCode.ROADMAP_TYPE_NOT_FOUND, exception.getStatusCode());
  }
}
