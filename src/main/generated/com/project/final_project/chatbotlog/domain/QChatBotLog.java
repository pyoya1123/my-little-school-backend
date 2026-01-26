package com.project.final_project.chatbotlog.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatBotLog is a Querydsl query type for ChatBotLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatBotLog extends EntityPathBase<ChatBotLog> {

    private static final long serialVersionUID = -2067325914L;

    public static final QChatBotLog chatBotLog = new QChatBotLog("chatBotLog");

    public final StringPath aiMessage = createString("aiMessage");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final StringPath userMessage = createString("userMessage");

    public QChatBotLog(String variable) {
        super(ChatBotLog.class, forVariable(variable));
    }

    public QChatBotLog(Path<? extends ChatBotLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatBotLog(PathMetadata metadata) {
        super(ChatBotLog.class, metadata);
    }

}

