package com.project.final_project.quest.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQuest is a Querydsl query type for Quest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuest extends EntityPathBase<Quest> {

    private static final long serialVersionUID = -471135228L;

    public static final QQuest quest = new QQuest("quest");

    public final StringPath content = createString("content");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final NumberPath<Integer> exp = createNumber("exp", Integer.class);

    public final NumberPath<Integer> gold = createNumber("gold", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<QuestItemRewardInfo, QQuestItemRewardInfo> itemRewards = this.<QuestItemRewardInfo, QQuestItemRewardInfo>createList("itemRewards", QuestItemRewardInfo.class, QQuestItemRewardInfo.class, PathInits.DIRECT2);

    public final StringPath questType = createString("questType");

    public final StringPath title = createString("title");

    public QQuest(String variable) {
        super(Quest.class, forVariable(variable));
    }

    public QQuest(Path<? extends Quest> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQuest(PathMetadata metadata) {
        super(Quest.class, metadata);
    }

}

