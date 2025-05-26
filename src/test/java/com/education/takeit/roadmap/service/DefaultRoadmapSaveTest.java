package com.education.takeit.roadmap.service;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.dto.RoadmapSaveResDto;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.entity.Track;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultRoadmapSaveTest {
    @InjectMocks
    private RoadmapService roadmapService;

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapManagementRepository roadmapManagementRepository;

    @Mock
    private RoadmapTransactionalService roadmapTransactionalService;

    @Test
    void saveDefaultRoadmap_FE(){
        //given
        long userId = 1L;
        String roadmapType = "FE";

        Track track = Track.builder()
                .trackId(1L)
                .trackNm("기초지식")
                .build();

        Subject subject1 = Subject.builder()
                .subId(1L)
                .subNm("HTML")
                .subType("FE")
                .subEssential("Y")
                .baseSubOrder(1)
                .subOverview("HTML이란? 웹페이지의 뼈대!웹사이트에서 내용을 담당해요.예를 들면, 제목, 글, 버튼, 이미지, 표 같은 걸 HTML로 만들어줍니다.마치 건물의 설계도 같다고 생각하면 돼요. 벽이 어디 있는지, 문은 어디 있는지 알려주는 거죠.")
                .track(track)
                .build();

        Subject subject2 = Subject.builder()
                .subId(2L)
                .subNm("CSS")
                .subType("FE")
                .subEssential("Y")
                .baseSubOrder(2)
                .subOverview("CSS란? 웹페이지에 스타일을 입히는 도구!색깔, 크기, 글꼴, 위치 같은 디자인 요소를 조절해요.예를 들면, 글자의 색을 바꾸거나 버튼의 모양을 다듬고,이미지 간 간격을 조정하고 배경 색을 설정할 수 있어요.마치 집 안을 꾸미는 인테리어처럼,보는 사람이 더 편하고 예쁘게 느낄 수 있도록 만드는 역할을 해요.")
                .track(track)
                .build();

        Subject subject3 = Subject.builder()
                .subId(3L)
                .subNm("JavaScript")
                .subType("FE")
                .subEssential("Y")
                .baseSubOrder(3)
                .subOverview("JavaScript란? 웹페이지를 움직이게 하는 도구!클릭, 입력, 스크롤 같은 사용자 행동에 반응하게 만들어요.예를 들면, 버튼을 누르면 팝업이 뜨거나,입력한 정보를 검사하고, 이미지가 자동으로 바뀌는 기능을 만들 수 있어요.마치 리모컨을 눌렀을 때 TV가 반응하듯,웹페이지에 동작과 반응을 더해주는 역할을 해요.")
                .track(track)
                .build();

        Roadmap roadmap1 = Roadmap.builder()
                .roadmapId(1L)
                .orderSub(1)
                .subject(subject1)
                .roadmapManagement(null)
                .isComplete(false)
                .preSubmitCount(0)
                .postSubmitCount(0)
                .level(0)
                .build();

        Roadmap roadmap2 = Roadmap.builder()
                .roadmapId(2L)
                .orderSub(2)
                .subject(subject2)
                .roadmapManagement(null)
                .isComplete(false)
                .preSubmitCount(0)
                .postSubmitCount(0)
                .level(0)
                .build();

        Roadmap roadmap3 = Roadmap.builder()
                .roadmapId(3L)
                .orderSub(3)
                .subject(subject3)
                .roadmapManagement(null)
                .isComplete(false)
                .preSubmitCount(0)
                .postSubmitCount(0)
                .level(0)
                .build();

        when(roadmapManagementRepository.findByUserId(userId)).thenReturn(null);
        when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(1L))
                .thenReturn(List.of(roadmap1, roadmap2, roadmap3));

        //when
        RoadmapSaveResDto result = roadmapService.saveDefaultRoadmap(roadmapType, userId);

        //then
        assertEquals("user Default Roadmap", result.uuid());
        assertEquals(1L, result.userLocationSubjectId());
        assertEquals(3, result.subjects().size());
        assertEquals("HTML", result.subjects().get(0).subjectName());
        assertEquals("CSS", result.subjects().get(1).subjectName());
    }

    @Test
    void saveDefaultRoadmap_shouldThrowException_whenInvalidRoadmapType(){
        //when & then
        CustomException ex = assertThrows(CustomException.class, () ->
                roadmapService.saveDefaultRoadmap("AI", 1L));

        assertEquals(StatusCode.ROADMAP_TYPE_NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void saveDefaultRoadmap_shouldThrowException_whenDefaultRoadmapListIsEmpty() {
        // given
        when(roadmapManagementRepository.findByUserId(1L)).thenReturn(null);
        when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(1L))
                .thenReturn(Collections.emptyList());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
                roadmapService.saveDefaultRoadmap("FE", 1L)
        );

        assertEquals(StatusCode.DEFAULT_ROADMAP_NOT_FOUND, ex.getStatusCode());
    }

}
