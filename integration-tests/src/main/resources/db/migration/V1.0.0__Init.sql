CREATE TABLE t_query_todo
(
    todoid      VARCHAR(255)                   NOT NULL,
    description VARCHAR(255)                   NOT NULL,
    createdat   timestamp(6) without time zone NOT NULL,
    status      VARCHAR(255)                   NOT NULL,
    version     integer
);

ALTER TABLE ONLY t_query_todo
    ADD CONSTRAINT t_query_todo_pkey PRIMARY KEY (todoid);