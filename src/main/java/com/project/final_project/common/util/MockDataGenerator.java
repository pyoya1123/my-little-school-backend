package com.project.final_project.common.util;

import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 테스트용 목업 데이터 생성 유틸리티
 * Faker 라이브러리를 사용하여 현실적인 테스트 데이터를 생성합니다.
 */
public class MockDataGenerator {

  private static final Faker faker = new Faker(new Locale("ko", "KR"));
  private static final Random random = new Random();

  // 관심사 목록
  private static final String[] INTERESTS = {
      "게임", "독서", "영화", "음악", "운동", "요리", "여행", "사진",
      "그림", "만화", "애니메이션", "프로그래밍", "수학", "과학", "역사", "문학"
  };

  /**
   * 사용자 등록 DTO 생성
   */
  public static UserRegisterData generateUserRegisterData(Integer schoolId) {
    UserRegisterData data = new UserRegisterData();
    data.name = faker.name().fullName();
    data.grade = random.nextInt(3) + 1; // 1~3학년
    data.birthday = String.format("%04d-%02d-%02d",
        faker.number().numberBetween(2008, 2011),
        faker.number().numberBetween(1, 13),
        faker.number().numberBetween(1, 29));
    data.gender = random.nextBoolean();
    data.email = faker.internet().emailAddress();
    data.password = "test1234";
    data.phone = String.format("010-%04d-%04d",
        faker.number().numberBetween(1000, 10000),
        faker.number().numberBetween(1000, 10000));
    data.statusMessage = faker.lorem().sentence(5);
    data.interest = generateInterests();
    data.schoolId = schoolId;
    return data;
  }

  /**
   * 게시판 등록 DTO 생성
   */
  public static BoardRegisterData generateBoardRegisterData(Integer userId) {
    BoardRegisterData data = new BoardRegisterData();
    data.title = faker.lorem().sentence(3, 8);
    data.content = faker.lorem().paragraph();
    data.userId = userId;
    return data;
  }

  /**
   * 관심사 리스트 생성 (1~3개)
   */
  private static List<String> generateInterests() {
    int count = random.nextInt(3) + 1; // 1~3개
    List<String> interests = new ArrayList<>();
    List<String> availableInterests = new ArrayList<>(List.of(INTERESTS));
    
    for (int i = 0; i < count; i++) {
      int index = random.nextInt(availableInterests.size());
      interests.add(availableInterests.remove(index));
    }
    
    return interests;
  }

  /**
   * 사용자 등록 데이터 클래스
   */
  public static class UserRegisterData {
    public String name;
    public Integer grade;
    public String birthday;
    public Boolean gender;
    public String email;
    public String password;
    public String phone;
    public String statusMessage;
    public List<String> interest;
    public Integer schoolId;
  }

  /**
   * 게시판 등록 데이터 클래스
   */
  public static class BoardRegisterData {
    public String title;
    public String content;
    public Integer userId;
  }
}

