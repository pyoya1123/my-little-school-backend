package com.project.final_project.note.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNote is a Querydsl query type for Note
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNote extends EntityPathBase<Note> {

    private static final long serialVersionUID = -407045536L;

    public static final QNote note = new QNote("note");

    public final NumberPath<Integer> color = createNumber("color", Integer.class);

    public final StringPath content = createString("content");

    public final NumberPath<Integer> fromUserId = createNumber("fromUserId", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> toUserId = createNumber("toUserId", Integer.class);

    public QNote(String variable) {
        super(Note.class, forVariable(variable));
    }

    public QNote(Path<? extends Note> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNote(PathMetadata metadata) {
        super(Note.class, metadata);
    }

}

