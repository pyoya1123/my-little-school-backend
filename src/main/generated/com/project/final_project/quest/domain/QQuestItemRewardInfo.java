package com.project.final_project.quest.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QQuestItemRewardInfo is a Querydsl query type for QuestItemRewardInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuestItemRewardInfo extends EntityPathBase<QuestItemRewardInfo> {

    private static final long serialVersionUID = -1564322540L;

    public static final QQuestItemRewardInfo questItemRewardInfo = new QQuestItemRewardInfo("questItemRewardInfo");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> itemCount = createNumber("itemCount", Integer.class);

    public final NumberPath<Integer> itemIdx = createNumber("itemIdx", Integer.class);

    public final NumberPath<Integer> questId = createNumber("questId", Integer.class);

    public QQuestItemRewardInfo(String variable) {
        super(QuestItemRewardInfo.class, forVariable(variable));
    }

    public QQuestItemRewardInfo(Path<? extends QuestItemRewardInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQuestItemRewardInfo(PathMetadata metadata) {
        super(QuestItemRewardInfo.class, metadata);
    }

}

