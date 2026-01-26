package com.project.final_project.common.init;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.project.final_project.item.dto.ItemRegisterDTO;
import com.project.final_project.item.service.ItemService;
import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.domain.QuestItemRewardInfo;
import com.project.final_project.quest.service.QuestService;
import com.project.final_project.school.dto.SchoolRegisterDTO;
import com.project.final_project.school.service.SchoolService;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class Init {

  private final SchoolService schoolService;
  private final ItemService itemService;
  private final QuestService questService;


  @PostConstruct
  public void init() {
    if (!schoolService.existSchoolDatas()) {
      File excelFile = new File("src/main/resources/schools.xlsx");

      if (!excelFile.exists()) {
        System.out.println("엑셀 파일이 존재하지 않습니다: " + excelFile.getPath());
        return;
      }

      try (FileInputStream inputStream = new FileInputStream(excelFile);
          Workbook workbook = new XSSFWorkbook(inputStream)) {

        // 엑셀 데이터를 읽어서 DTO 리스트로 변환
        List<SchoolRegisterDTO> schoolData = extractSchoolData(workbook);
        for (SchoolRegisterDTO school : schoolData) {
          schoolService.registerSchool(school); // 학교 데이터 등록
        }

        System.out.println("학교 데이터가 성공적으로 저장되었습니다.");

      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("학교 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
    }


    if (!itemService.existItemDatas()) {
      File itemFile = new File("src/main/resources/itemList.txt");

      if (!itemFile.exists()) {
        System.out.println("아이템 파일이 존재하지 않습니다: " + itemFile.getPath());
        return;
      }

      try (BufferedReader reader = new BufferedReader(new FileReader(itemFile))) {

        // 텍스트 파일 데이터를 읽어서 DTO 리스트로 변환
        List<ItemRegisterDTO> items = extractItemData(reader);
        for (ItemRegisterDTO item : items) {
          itemService.registerItem(item); // 아이템 데이터 등록
        }

        System.out.println("아이템 데이터가 성공적으로 저장되었습니다.");

      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("아이템 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
    }



    if (!questService.existsQuests()) {
      // Initialize Quests
      List<Quest> quests = createQuests();

      for (Quest quest : quests) {
        questService.saveQuest(quest);
      }

      System.out.println("퀘스트 데이터가 성공적으로 저장되었습니다.");
    } else {
      System.out.println("퀘스트 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
    }



  }

  private List<ItemRegisterDTO> extractItemData(BufferedReader reader) throws IOException {
    // JSON 데이터를 읽기 위한 StringBuilder
    StringBuilder jsonBuilder = new StringBuilder();
    String line;

    while ((line = reader.readLine()) != null) {
      jsonBuilder.append(line);
    }

    // JSON 데이터를 파싱
    ObjectMapper objectMapper = new ObjectMapper();
    List<ItemRegisterDTO> items;

    try {
      items = objectMapper.readValue(jsonBuilder.toString(), new TypeReference<List<ItemRegisterDTO>>() {});
    } catch (Exception e) {
      System.out.println("JSON 데이터 파싱 오류: " + e.getMessage());
      items = new ArrayList<>(); // 오류 발생 시 빈 리스트 반환
    }

    return items;
  }


  private List<Quest> createQuests() {
    // Define quests
    List<Quest> quests = new ArrayList<>();

    quests.add(new Quest("나만의 프로필을 완성하세요!", "프로필 설정하기", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("나의 첫 친구는...?", "첫 친구 추가하기", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("친구와 비밀 이야기를 해봅시다!", "친구에게 쪽지 보내기", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("나만의 교실을 만들자!", "교실 처음 꾸미기", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("나의 지식 뽐내기!1", "첫 OX 퀴즈 참여", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("나의 지식 뽐내기!2", "첫 골든벨 참여", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("만남의 광장에는 무엇이 있을까?1", "만남의 광장 오브제 상호작용 첫 시도", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("만남의 광장에는 무엇이 있을까?2", "처음 고민상담 게시글 남기기", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("나의 베스트 친구는..?", "AI 추천 친구 받아보기", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("고민 해결!", "처음으로 고민상담 댓글 남기기", 1, "TUTORIAL", 50, 20));
    quests.add(new Quest("우리 학교 기록하기!", "아카이빙 갤러리에 사진 업로드하기", 1, "TUTORIAL", 50, 20));

    // Define quest rewards
    List<List<QuestItemRewardInfo>> questRewards = Arrays.asList(
        Arrays.asList(new QuestItemRewardInfo(1, 1, 2), new QuestItemRewardInfo(1, 4, 2)),
        Arrays.asList(new QuestItemRewardInfo(2, 5, 1)),
        Arrays.asList(new QuestItemRewardInfo(3, 15, 1), new QuestItemRewardInfo(3, 16, 1)),
        Arrays.asList(new QuestItemRewardInfo(4, 1, 1), new QuestItemRewardInfo(4, 3, 1)),
        Arrays.asList(new QuestItemRewardInfo(5, 12, 3), new QuestItemRewardInfo(5, 13, 3)),
        Arrays.asList(new QuestItemRewardInfo(6, 4, 2), new QuestItemRewardInfo(6, 1, 2)),
        Arrays.asList(new QuestItemRewardInfo(7, 11, 3)),
        Arrays.asList(new QuestItemRewardInfo(8, 7, 1)),
        Arrays.asList(new QuestItemRewardInfo(9, 2, 1)),
        Arrays.asList(new QuestItemRewardInfo(10, 7, 1)),
        Arrays.asList(new QuestItemRewardInfo(11, 9, 3), new QuestItemRewardInfo(11, 10, 3))
    );

    // Associate rewards with quests
    for (int i = 0; i < quests.size(); i++) {
      quests.get(i).setItemRewards(questRewards.get(i));
    }

    return quests;
  }


  // 엑셀 데이터를 읽어서 SchoolRegisterDTO 리스트로 변환
  private List<SchoolRegisterDTO> extractSchoolData(Workbook workbook) {
    List<SchoolRegisterDTO> schools = new ArrayList<>();
    Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트

    for (Row row : sheet) {
      if (row.getRowNum() == 0) continue; // 헤더 행 건너뛰기

      // 학교명, 소재지지번주소, 위도, 경도 컬럼 읽기
      String schoolName = row.getCell(1).getStringCellValue();
      String address = row.getCell(7).getStringCellValue();
      double latitude = row.getCell(15).getNumericCellValue(); // 위도
      double longitude = row.getCell(16).getNumericCellValue(); // 경도

      // SchoolRegisterDTO 생성 및 리스트에 추가
      SchoolRegisterDTO school = new SchoolRegisterDTO(schoolName, address, latitude, longitude);
      schools.add(school);
    }

    return schools;
  }

  // Hibernate SQL 로그 비활성화 메서드
  private void disableHibernateQueryLogging() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    // Hibernate 및 SQL 관련 모든 로거 비활성화
    loggerContext.getLogger("org.hibernate").setLevel(Level.OFF);
    loggerContext.getLogger("org.hibernate.SQL").setLevel(Level.OFF);
    loggerContext.getLogger("org.hibernate.type.descriptor.sql.BasicBinder").setLevel(Level.OFF);
    loggerContext.getLogger("org.hibernate.stat").setLevel(Level.OFF);
    loggerContext.getLogger("org.hibernate.tool.hbm2ddl").setLevel(Level.OFF);
    loggerContext.getLogger("javax.sql").setLevel(Level.OFF); // JDBC 관련 로그 비활성화
  }

  // Hibernate SQL 로그 다시 활성화 메서드
  private void enableHibernateQueryLogging() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    loggerContext.getLogger("org.hibernate.SQL").setLevel(Level.DEBUG);
    loggerContext.getLogger("org.hibernate.type.descriptor.sql.BasicBinder").setLevel(Level.DEBUG);
    loggerContext.getLogger("org.hibernate.stat").setLevel(Level.DEBUG);
    loggerContext.getLogger("org.hibernate.tool.hbm2ddl").setLevel(Level.DEBUG);
    loggerContext.getLogger("javax.sql").setLevel(Level.DEBUG); // JDBC 로그 활성화
  }

}
