package com.project.final_project.friendship.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFriendship is a Querydsl query type for Friendship
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendship extends EntityPathBase<Friendship> {

    private static final long serialVersionUID = -1339840080L;

    public static final QFriendship friendship = new QFriendship("friendship");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final BooleanPath isAccepted = createBoolean("isAccepted");

    public final StringPath message = createString("message");

    public final NumberPath<Integer> receiverId = createNumber("receiverId", Integer.class);

    public final NumberPath<Integer> requesterId = createNumber("requesterId", Integer.class);

    public QFriendship(String variable) {
        super(Friendship.class, forVariable(variable));
    }

    public QFriendship(Path<? extends Friendship> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFriendship(PathMetadata metadata) {
        super(Friendship.class, metadata);
    }

}

