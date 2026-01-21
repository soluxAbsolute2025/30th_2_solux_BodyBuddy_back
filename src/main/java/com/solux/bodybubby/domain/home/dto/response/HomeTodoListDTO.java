package com.solux.bodybubby.domain.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeTodoListDTO { // 대문자 DTO 확인!
    private List<TodoItemDTO> medicine;
    private List<TodoItemDTO> water;
    private List<TodoItemDTO> exercise;
    private List<TodoItemDTO> meal;
}