package com.example.todolist.validation;

import com.example.todolist.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DueDateNotBeforeCreationValidator
        implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

    @Override
    public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getDueDate() == null || dto.getCreatedAt() == null) {
            return true;
        }
        LocalDate creationDate = dto.getCreatedAt().toLocalDate();
        return !dto.getDueDate().isBefore(creationDate);
    }
}
