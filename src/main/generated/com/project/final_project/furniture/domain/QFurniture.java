package com.project.final_project.furniture.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFurniture is a Querydsl query type for Furniture
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFurniture extends EntityPathBase<Furniture> {

    private static final long serialVersionUID = 1877736708L;

    public static final QFurniture furniture = new QFurniture("furniture");

    public final BooleanPath flip = createBoolean("flip");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> mapId = createNumber("mapId", Integer.class);

    public final StringPath mapType = createString("mapType");

    public final NumberPath<Integer> objId = createNumber("objId", Integer.class);

    public final NumberPath<Integer> rot = createNumber("rot", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final NumberPath<Integer> x = createNumber("x", Integer.class);

    public final NumberPath<Integer> y = createNumber("y", Integer.class);

    public QFurniture(String variable) {
        super(Furniture.class, forVariable(variable));
    }

    public QFurniture(Path<? extends Furniture> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFurniture(PathMetadata metadata) {
        super(Furniture.class, metadata);
    }

}

