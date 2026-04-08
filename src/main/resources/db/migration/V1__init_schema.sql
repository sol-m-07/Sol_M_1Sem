CREATE TABLE tasks (
    id            BIGSERIAL    PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    description   VARCHAR(500),
    completed     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    due_date      DATE,
    priority      VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM'
);

CREATE TABLE task_tags (
    task_id BIGINT      NOT NULL,
    tag     VARCHAR(50) NOT NULL,
    PRIMARY KEY (task_id, tag),
    CONSTRAINT fk_task_tags_task FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE
);

CREATE TABLE task_attachments (
    id               BIGSERIAL    PRIMARY KEY,
    task_id          BIGINT       NOT NULL,
    file_name        VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    content_type     VARCHAR(100),
    size             BIGINT       NOT NULL DEFAULT 0,
    uploaded_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attachments_task FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE
);

CREATE INDEX idx_task_attachments_task_id ON task_attachments (task_id);
CREATE INDEX idx_tasks_priority ON tasks (priority);
CREATE INDEX idx_tasks_due_date ON tasks (due_date);
