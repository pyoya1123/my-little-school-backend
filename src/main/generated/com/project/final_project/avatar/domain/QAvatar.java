package com.project.final_project.avatar.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAvatar is a Querydsl query type for Avatar
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAvatar extends EntityPathBase<Avatar> {

    private static final long serialVersionUID = 1254678894L;

    public static final QAvatar avatar = new QAvatar("avatar");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<Integer, NumberPath<Integer>> infoList = this.<Integer, NumberPath<Integer>>createList("infoList", Integer.class, NumberPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QAvatar(String variable) {
        super(Avatar.class, forVariable(variable));
    }

    public QAvatar(Path<? extends Avatar> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAvatar(PathMetadata metadata) {
        super(Avatar.class, metadata);
    }

}

