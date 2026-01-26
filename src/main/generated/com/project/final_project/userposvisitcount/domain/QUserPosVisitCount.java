package com.project.final_project.userposvisitcount.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserPosVisitCount is a Querydsl query type for UserPosVisitCount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPosVisitCount extends EntityPathBase<UserPosVisitCount> {

    private static final long serialVersionUID = -1036285116L;

    public static final QUserPosVisitCount userPosVisitCount = new QUserPosVisitCount("userPosVisitCount");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath mapType = createString("mapType");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QUserPosVisitCount(String variable) {
        super(UserPosVisitCount.class, forVariable(variable));
    }

    public QUserPosVisitCount(Path<? extends UserPosVisitCount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPosVisitCount(PathMetadata metadata) {
        super(UserPosVisitCount.class, metadata);
    }

}

