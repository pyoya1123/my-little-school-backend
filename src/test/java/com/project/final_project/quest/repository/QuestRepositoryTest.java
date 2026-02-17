package com.project.final_project.quest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.final_project.item.domain.Item;
import com.project.final_project.item.domain.ItemType;
import com.project.final_project.item.repository.ItemRepository;
import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.domain.QuestReward;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

/**
 * QuestRepository 통합 테스트
 * - 실제 DB 대신 인메모리 H2 DB를 사용합니다.
 * - Hibernate Statistics를 비활성화하여 Observation Handler가 로드되지 않도록 합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY) // 인메모리 H2 DB 사용
@TestPropertySource(properties = {
    "spring.jpa.properties.hibernate.generate_statistics=false",
    "management.metrics.enable.hibernate=false",
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class QuestRepositoryTest {

  @Autowired
  private QuestRepository questRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Test
  @DisplayName("퀘스트와 보상 아이템을 함께 저장하고 조회한다")
  void saveAndFindQuestWithRewards() {
    // Given
    // 1. 아이템 미리 저장
    Item item1 = new Item(null, 101, "책상", 1000, ItemType.CLASSROOM);
    Item item2 = new Item(null, 102, "의자", 500, ItemType.COMMON);
    itemRepository.saveAll(List.of(item1, item2));

    // 2. 퀘스트 생성
    Quest quest = new Quest("교실 꾸미기", "책상과 의자 배치하기", 10, "DAILY", 100, 50);

    // 3. 보상 설정
    QuestReward reward1 = new QuestReward(quest, item1, 1);
    QuestReward reward2 = new QuestReward(quest, item2, 2);
    quest.getRewards().add(reward1);
    quest.getRewards().add(reward2);

    // When
    Quest savedQuest = questRepository.save(quest);
    
    // Then
    Quest foundQuest = questRepository.findById(savedQuest.getId()).orElseThrow();
    
    assertThat(foundQuest.getTitle()).isEqualTo("교실 꾸미기");
    assertThat(foundQuest.getRewards()).hasSize(2); // 보상 2개가 잘 들어갔는지
    
    // 보상 내용 검증
    QuestReward foundReward1 = foundQuest.getRewards().get(0);
    assertThat(foundReward1.getItem().getName()).isIn("책상", "의자"); // 아이템 정보까지 잘 가져오는지
    assertThat(foundReward1.getQuest()).isEqualTo(foundQuest); // 양방향 관계 확인
  }
}

