INSERT INTO users (first_name, last_name, email, password)
VALUES
    ('user', 'user', 'user@gmail.com', 'user123'),
    ('admin', 'admin', 'admin@gmail.com', 'user123'),
    ('manager', 'manager', 'manager@gmail.com', 'user123');
insert into roles values
                      (1, 'ROLE_USER'),
                      (2, 'ROLE_ADMIN'),
                      (3, 'ROLE_MANAGER');
INSERT INTO users_roles VALUES(1, 1),
                              (2, 2),
                              (3, 3);