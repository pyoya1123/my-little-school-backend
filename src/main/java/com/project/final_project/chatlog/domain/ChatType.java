package com.project.final_project.chatlog.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatType {
  PRIVATE,   // 1:1 채팅
  GROUP,     // 그룹 채팅
  PUBLIC;    // 전체 채팅
}
