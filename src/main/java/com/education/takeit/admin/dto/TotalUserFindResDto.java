package com.education.takeit.admin.dto;

import com.education.takeit.user.entity.LectureAmount;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.PriceLevel;

public record TotalUserFindResDto(Long userId,
                                  String email,
                                  String nickname,
                                  LoginType loginType,
                                  LectureAmount lectureAmount,
                                  PriceLevel priceLevel,
                                  boolean isActive,
                                  boolean likeBooks,
                                  boolean PrivacyStatus) {
}
