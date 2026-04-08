package com.example.todolist.service;

import com.example.todolist.dto.TaskPriorityCountDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class TaskStatisticsJdbcService {

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TaskPriorityCountDto> getTasksCountByPriority() {
        String sql = "SELECT priority, COUNT(*) AS cnt FROM tasks GROUP BY priority ORDER BY priority";
        return jdbcTemplate.query(sql, new TaskPriorityCountRowMapper());
    }

    private static class TaskPriorityCountRowMapper implements RowMapper<TaskPriorityCountDto> {
        @Override
        public TaskPriorityCountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TaskPriorityCountDto(
                    rs.getString("priority"),
                    rs.getLong("cnt")
            );
        }
    }
}
