package com.project.final_project.item.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = 2034493218L;

    public static final QItem item = new QItem("item");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> itemIdx = createNumber("itemIdx", Integer.class);

    public final StringPath itemName = createString("itemName");

    public final StringPath itemType = createString("itemType");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public QItem(String variable) {
        super(Item.class, forVariable(variable));
    }

    public QItem(Path<? extends Item> path) {
        super(path.getType(), path.getMetadata());
    }

    public QItem(PathMetadata metadata) {
        super(Item.class, metadata);
    }

}

