package com.project.final_project.chatlog.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatLog is a Querydsl query type for ChatLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatLog extends EntityPathBase<ChatLog> {

    private static final long serialVersionUID = -755980764L;

    public static final QChatLog chatLog = new QChatLog("chatLog");

    public final StringPath channel = createString("channel");

    public final EnumPath<ChatType> chatType = createEnum("chatType", ChatType.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath message = createString("message");

    public final NumberPath<Integer> receiverId = createNumber("receiverId", Integer.class);

    public final NumberPath<Integer> senderId = createNumber("senderId", Integer.class);

    public final StringPath timestamp = createString("timestamp");

    public QChatLog(String variable) {
        super(ChatLog.class, forVariable(variable));
    }

    public QChatLog(Path<? extends ChatLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatLog(PathMetadata metadata) {
        super(ChatLog.class, metadata);
    }

}

