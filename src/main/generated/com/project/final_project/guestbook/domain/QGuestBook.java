package com.project.final_project.guestbook.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGuestBook is a Querydsl query type for GuestBook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGuestBook extends EntityPathBase<GuestBook> {

    private static final long serialVersionUID = -1343764220L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGuestBook guestBook = new QGuestBook("guestBook");

    public final NumberPath<Integer> backgroundColor = createNumber("backgroundColor", Integer.class);

    public final StringPath content = createString("content");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> mapId = createNumber("mapId", Integer.class);

    public final StringPath mapType = createString("mapType");

    public final com.project.final_project.user.domain.QUser user;

    public QGuestBook(String variable) {
        this(GuestBook.class, forVariable(variable), INITS);
    }

    public QGuestBook(Path<? extends GuestBook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGuestBook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGuestBook(PathMetadata metadata, PathInits inits) {
        this(GuestBook.class, metadata, inits);
    }

    public QGuestBook(Class<? extends GuestBook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.project.final_project.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

