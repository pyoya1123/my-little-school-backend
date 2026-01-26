package com.project.final_project.common.constants;

/**
 * User 관련 상수
 */
public class UserConstants {

  private UserConstants() {
    // 인스턴스화 방지
  }

  // 초기값
  public static final Integer INITIAL_GOLD = 100000;
  public static final Integer INITIAL_LEVEL = 1;
  public static final Integer INITIAL_EXP = 0;
  public static final Integer INITIAL_MAX_EXP = 100;

  // 경험치 계산
  public static final Integer BASE_EXP = 100;
  public static final Integer EXP_INCREMENT_PER_LEVEL = 50;

  // 맵 타입
  public static final String DEFAULT_MAP_TYPE = "MyClassroom";
}

