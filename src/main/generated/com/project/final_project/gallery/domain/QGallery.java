package com.project.final_project.gallery.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGallery is a Querydsl query type for Gallery
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGallery extends EntityPathBase<Gallery> {

    private static final long serialVersionUID = 1375389060L;

    public static final QGallery gallery = new QGallery("gallery");

    public final StringPath enteredDate = createString("enteredDate");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath imgUrl = createString("imgUrl");

    public final StringPath publicId = createString("publicId");

    public final NumberPath<Integer> schoolId = createNumber("schoolId", Integer.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QGallery(String variable) {
        super(Gallery.class, forVariable(variable));
    }

    public QGallery(Path<? extends Gallery> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGallery(PathMetadata metadata) {
        super(Gallery.class, metadata);
    }

}

