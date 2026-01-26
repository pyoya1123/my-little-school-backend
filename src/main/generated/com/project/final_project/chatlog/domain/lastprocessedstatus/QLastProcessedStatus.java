package com.project.final_project.chatlog.domain.lastprocessedstatus;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLastProcessedStatus is a Querydsl query type for LastProcessedStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLastProcessedStatus extends EntityPathBase<LastProcessedStatus> {

    private static final long serialVersionUID = -820593602L;

    public static final QLastProcessedStatus lastProcessedStatus = new QLastProcessedStatus("lastProcessedStatus");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> lastProcessedId = createNumber("lastProcessedId", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QLastProcessedStatus(String variable) {
        super(LastProcessedStatus.class, forVariable(variable));
    }

    public QLastProcessedStatus(Path<? extends LastProcessedStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLastProcessedStatus(PathMetadata metadata) {
        super(LastProcessedStatus.class, metadata);
    }

}

