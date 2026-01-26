package com.project.final_project.emotionanalysis.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmotionAnalysis is a Querydsl query type for EmotionAnalysis
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmotionAnalysis extends EntityPathBase<EmotionAnalysis> {

    private static final long serialVersionUID = -1434040828L;

    public static final QEmotionAnalysis emotionAnalysis = new QEmotionAnalysis("emotionAnalysis");

    public final StringPath emotion = createString("emotion");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath message = createString("message");

    public final NumberPath<Double> score = createNumber("score", Double.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QEmotionAnalysis(String variable) {
        super(EmotionAnalysis.class, forVariable(variable));
    }

    public QEmotionAnalysis(Path<? extends EmotionAnalysis> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmotionAnalysis(PathMetadata metadata) {
        super(EmotionAnalysis.class, metadata);
    }

}

