package com.project.final_project.mapcontest.dto;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMapContestFurnitureVoDTO is a Querydsl query type for MapContestFurnitureVoDTO
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QMapContestFurnitureVoDTO extends BeanPath<MapContestFurnitureVoDTO> {

    private static final long serialVersionUID = -1973957199L;

    public static final QMapContestFurnitureVoDTO mapContestFurnitureVoDTO = new QMapContestFurnitureVoDTO("mapContestFurnitureVoDTO");

    public final BooleanPath flip = createBoolean("flip");

    public final StringPath mapType = createString("mapType");

    public final NumberPath<Integer> objId = createNumber("objId", Integer.class);

    public final NumberPath<Integer> rot = createNumber("rot", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final NumberPath<Integer> x = createNumber("x", Integer.class);

    public final NumberPath<Integer> y = createNumber("y", Integer.class);

    public QMapContestFurnitureVoDTO(String variable) {
        super(MapContestFurnitureVoDTO.class, forVariable(variable));
    }

    public QMapContestFurnitureVoDTO(Path<? extends MapContestFurnitureVoDTO> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMapContestFurnitureVoDTO(PathMetadata metadata) {
        super(MapContestFurnitureVoDTO.class, metadata);
    }

}

