package com.project.final_project.inventory.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInventory is a Querydsl query type for Inventory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInventory extends EntityPathBase<Inventory> {

    private static final long serialVersionUID = 1691205188L;

    public static final QInventory inventory = new QInventory("inventory");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final MapPath<Integer, Integer, NumberPath<Integer>> itemCountList = this.<Integer, Integer, NumberPath<Integer>>createMap("itemCountList", Integer.class, Integer.class, NumberPath.class);

    public final StringPath itemType = createString("itemType");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QInventory(String variable) {
        super(Inventory.class, forVariable(variable));
    }

    public QInventory(Path<? extends Inventory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInventory(PathMetadata metadata) {
        super(Inventory.class, metadata);
    }

}

