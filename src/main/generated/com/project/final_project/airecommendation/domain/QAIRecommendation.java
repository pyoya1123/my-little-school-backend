package com.project.final_project.airecommendation.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAIRecommendation is a Querydsl query type for AIRecommendation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAIRecommendation extends EntityPathBase<AIRecommendation> {

    private static final long serialVersionUID = -2066773954L;

    public static final QAIRecommendation aIRecommendation = new QAIRecommendation("aIRecommendation");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<String, StringPath> recommendedInterestList = this.<String, StringPath>createList("recommendedInterestList", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> recommendedUserId = createNumber("recommendedUserId", Integer.class);

    public final NumberPath<Double> similarity = createNumber("similarity", Double.class);

    public final StringPath similarityMessage = createString("similarityMessage");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QAIRecommendation(String variable) {
        super(AIRecommendation.class, forVariable(variable));
    }

    public QAIRecommendation(Path<? extends AIRecommendation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAIRecommendation(PathMetadata metadata) {
        super(AIRecommendation.class, metadata);
    }

}

