package com.education.takeit.recommend.repository;

import com.education.takeit.admin.dto.AdminContentResDto;
import com.education.takeit.recommend.entity.TotalContent;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TotalContentRepository extends JpaRepository<TotalContent, Long> {

  @Query("""
  SELECT new com.education.takeit.admin.dto.AdminContentResDto(
      tc.totalContentId,
      tc.contentTitle,
      tc.contentUrl,
      tc.contentType,
      tc.contentPlatform,
      s.subNm,
      COUNT(uc)
  )
  FROM TotalContent tc
  LEFT JOIN tc.subject s
  LEFT JOIN UserContent uc ON uc.totalContent = tc
  WHERE (:title IS NULL OR LOWER(tc.contentTitle) LIKE LOWER(CONCAT('%', :title, '%')))
    AND (:subName IS NULL OR LOWER(s.subNm) LIKE LOWER(CONCAT('%', :subName, '%')))
  GROUP BY tc.totalContentId, tc.contentTitle, tc.contentUrl, tc.contentType, tc.contentPlatform, s.subNm
""")
  List<AdminContentResDto> findAllWithoutSort(
          @Param("title") String title,
          @Param("subName") String subName);

}
