package com.project.final_project.quest.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserQuest is a Querydsl query type for UserQuest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserQuest extends EntityPathBase<UserQuest> {

    private static final long serialVersionUID = 497120761L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserQuest userQuest = new QUserQuest("userQuest");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final BooleanPath isComplete = createBoolean("isComplete");

    public final QQuest quest;

    public final com.project.final_project.user.domain.QUser user;

    public QUserQuest(String variable) {
        this(UserQuest.class, forVariable(variable), INITS);
    }

    public QUserQuest(Path<? extends UserQuest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserQuest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserQuest(PathMetadata metadata, PathInits inits) {
        this(UserQuest.class, metadata, inits);
    }

    public QUserQuest(Class<? extends UserQuest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.quest = inits.isInitialized("quest") ? new QQuest(forProperty("quest")) : null;
        this.user = inits.isInitialized("user") ? new com.project.final_project.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

