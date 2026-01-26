package com.project.final_project.userposvisitcount.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserPosVisitCount is a Querydsl query type for UserPosVisitCount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPosVisitCount extends EntityPathBase<UserPosVisitCount> {

    private static final long serialVersionUID = -1036285116L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserPosVisitCount userPosVisitCount = new QUserPosVisitCount("userPosVisitCount");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath mapType = createString("mapType");

    public final com.project.final_project.user.domain.QUser user;

    public QUserPosVisitCount(String variable) {
        this(UserPosVisitCount.class, forVariable(variable), INITS);
    }

    public QUserPosVisitCount(Path<? extends UserPosVisitCount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserPosVisitCount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserPosVisitCount(PathMetadata metadata, PathInits inits) {
        this(UserPosVisitCount.class, metadata, inits);
    }

    public QUserPosVisitCount(Class<? extends UserPosVisitCount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.project.final_project.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

