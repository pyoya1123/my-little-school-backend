package com.project.final_project.mapcontest.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMapContest is a Querydsl query type for MapContest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMapContest extends EntityPathBase<MapContest> {

    private static final long serialVersionUID = -1442907444L;

    public static final QMapContest mapContest = new QMapContest("mapContest");

    public final StringPath description = createString("description");

    public final ListPath<com.project.final_project.mapcontest.dto.MapContestFurnitureVoDTO, com.project.final_project.mapcontest.dto.QMapContestFurnitureVoDTO> furnitureList = this.<com.project.final_project.mapcontest.dto.MapContestFurnitureVoDTO, com.project.final_project.mapcontest.dto.QMapContestFurnitureVoDTO>createList("furnitureList", com.project.final_project.mapcontest.dto.MapContestFurnitureVoDTO.class, com.project.final_project.mapcontest.dto.QMapContestFurnitureVoDTO.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final StringPath previewImageUrl = createString("previewImageUrl");

    public final StringPath publicId = createString("publicId");

    public final StringPath title = createString("title");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QMapContest(String variable) {
        super(MapContest.class, forVariable(variable));
    }

    public QMapContest(Path<? extends MapContest> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMapContest(PathMetadata metadata) {
        super(MapContest.class, metadata);
    }

}

