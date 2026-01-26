package com.project.final_project.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1523807378L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final StringPath birthday = createString("birthday");

    public final StringPath email = createString("email");

    public final StringPath enteredDate = createString("enteredDate");

    public final NumberPath<Integer> exp = createNumber("exp", Integer.class);

    public final BooleanPath gender = createBoolean("gender");

    public final NumberPath<Integer> gold = createNumber("gold", Integer.class);

    public final NumberPath<Integer> grade = createNumber("grade", Integer.class);

    public final ListPath<com.project.final_project.guestbook.domain.GuestBook, com.project.final_project.guestbook.domain.QGuestBook> guestBooks = this.<com.project.final_project.guestbook.domain.GuestBook, com.project.final_project.guestbook.domain.QGuestBook>createList("guestBooks", com.project.final_project.guestbook.domain.GuestBook.class, com.project.final_project.guestbook.domain.QGuestBook.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<String, StringPath> interest = this.<String, StringPath>createList("interest", String.class, StringPath.class, PathInits.DIRECT2);

    public final BooleanPath isOnline = createBoolean("isOnline");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final NumberPath<Integer> mapId = createNumber("mapId", Integer.class);

    public final StringPath mapType = createString("mapType");

    public final NumberPath<Integer> maxExp = createNumber("maxExp", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final com.project.final_project.school.domain.QSchool school;

    public final StringPath statusMessage = createString("statusMessage");

    public final ListPath<com.project.final_project.quest.domain.UserQuest, com.project.final_project.quest.domain.QUserQuest> userQuests = this.<com.project.final_project.quest.domain.UserQuest, com.project.final_project.quest.domain.QUserQuest>createList("userQuests", com.project.final_project.quest.domain.UserQuest.class, com.project.final_project.quest.domain.QUserQuest.class, PathInits.DIRECT2);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.school = inits.isInitialized("school") ? new com.project.final_project.school.domain.QSchool(forProperty("school")) : null;
    }

}

