TRUNCATE TABLE payments RESTART IDENTITY CASCADE;
TRUNCATE TABLE bookings RESTART IDENTITY CASCADE;
TRUNCATE TABLE accommodation_amenities RESTART IDENTITY CASCADE;
TRUNCATE TABLE accommodations RESTART IDENTITY CASCADE;
TRUNCATE TABLE users_roles RESTART IDENTITY CASCADE;
TRUNCATE TABLE roles RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;
INSERT INTO users (first_name, last_name, email, password)
VALUES ('user', 'user', 'user@gmail.com', 'user123'),
       ('admin', 'admin', 'admin@gmail.com', 'user123'),
       ('manager', 'manager', 'manager@gmail.com', 'user123');
insert into roles
values (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN'),
       (3, 'ROLE_MANAGER');
INSERT INTO users_roles
VALUES (1, 1),
       (2, 2),
       (3, 3);
WITH ins AS (
    INSERT INTO accommodations (daily_rate, availability, size, accommodation_type, country, city, street, build_number)
        VALUES (200.00, 30, 'small', 'GUESTHOUSE', 'Ukraine', 'Kyiv', 'Khreshchatyk', 25),
               (300.00, 50, 'house', 'HOUSE', 'Ukraine', 'Odessa', 'Deribasivska', 10),
               (450.00, 15, 'vacation_home', 'GUESTHOUSE', 'Ukraine', 'Kharkiv', 'Freedom Square', 5)
        RETURNING id, city)
INSERT
INTO accommodation_amenities (accommodation_id, amenity)
SELECT id, unnest(ARRAY ['wifi', 'air conditioning', 'kitchen'])
FROM ins
WHERE city = 'Kyiv'
UNION ALL
SELECT id, unnest(ARRAY ['wifi', 'kitchen', 'swimming pool'])
FROM ins
WHERE city = 'Odessa'
UNION ALL
SELECT id, unnest(ARRAY ['wifi', 'hot tub', 'barbecue'])
FROM ins
WHERE city = 'Kharkiv';

INSERT INTO bookings (id, user_id, check_in_date, check_out_date, accommodation_id, status, is_deleted)
VALUES (1, 1, '2025-01-01', '2025-01-05', 1, 'PENDING', FALSE),
       (2, 1, '2025-02-02', '2025-02-06', 2, 'PENDING', FALSE),
       (3, 1, '2025-03-03', '2025-03-07', 3, 'PENDING', FALSE),
       (4, 1, '2025-04-04', '2025-04-09', 1, 'PENDING', FALSE),
       (5, 1, '2025-05-05', '2025-05-10', 2, 'PENDING', FALSE),
       (6, 1, '2025-06-06', '2025-06-11', 3, 'PENDING', FALSE);
INSERT INTO payments (id, status, booking_id, url, session_id, amount)
VALUES (1, 'PENDING', 1, 'https://checkout.stripe.com/c/pay/cs_test_a1QpOwJuVdsnxVDllwnHk82',
        'cs_test_a1ADMDWWeKAzWJ6RwzeyjmDSjdy0rHsvOJTmUZfZmISHIh6PsawrGLKe0i', 1000.00),
       (2, 'PENDING', 2, 'https://checkout.stripe.com/c/pay/cs_test_a1QpOwJuVdsnxVDllwnHk83',
        'cs_test_a1ADMDWWeKAzWJ6RwzeyjmDSjdy0rHsvOJTmUZfZmISHIh6PsawrGLKe0y', 1000.00),
       (3, 'PENDING', 3, 'https://checkout.stripe.com/c/pay/cs_test_a1QpOwJuVdsnxVDllwnHk84',
        'cs_test_a1ADMDWWeKAzWJ6RwzeyjmDSjdy0rHsvOJTmUZfZmISHIh6PsawrGLKe0o', 1000.00);