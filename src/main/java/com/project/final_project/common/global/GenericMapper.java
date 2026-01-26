package com.project.final_project.common.global;

import java.util.List;

public interface GenericMapper<D, E> {

  D toDTO(E e);

  E toEntity(D d);

  List<D> toDTOList(List<E> entityList);

  List<E> toEntityList(List<D> dtoList);

}
