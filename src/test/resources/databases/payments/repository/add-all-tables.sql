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
WITH ins AS (
    INSERT INTO accommodations (daily_rate, availability, size, accommodation_type, country, city, street, build_number)
        VALUES
            (200.00, 30, 'small', 'GUESTHOUSE', 'Ukraine', 'Kyiv', 'Khreshchatyk', 25),
            (300.00, 50, 'house', 'HOUSE', 'Ukraine', 'Odessa', 'Deribasivska', 10),
            (450.00, 15, 'vacation_home', 'GUESTHOUSE', 'Ukraine', 'Kharkiv', 'Freedom Square', 5)
        RETURNING id, city
)
INSERT INTO accommodation_amenities (accommodation_id, amenity)
SELECT id, unnest(ARRAY['wifi', 'air conditioning', 'kitchen'])
FROM ins
WHERE city = 'Kyiv'
UNION ALL
SELECT id, unnest(ARRAY['wifi', 'kitchen', 'swimming pool'])
FROM ins
WHERE city = 'Odessa'
UNION ALL
SELECT id, unnest(ARRAY['wifi', 'hot tub', 'barbecue'])
FROM ins
WHERE city = 'Kharkiv';

INSERT INTO bookings (id, user_id, check_in_date, check_out_date, accommodation_id, status, is_deleted)
VALUES
    (1, 1, '2025-01-20', '2025-01-25', 1, 'PENDING', FALSE),
    (2, 1, '2025-02-01', '2025-02-10', 2, 'PENDING', FALSE),
    (3, 1, '2025-03-15', '2025-03-20', 3, 'PENDING', FALSE),
    (4, 1, '2025-01-01', '2025-02-01', 3, 'CANCELED', FALSE);