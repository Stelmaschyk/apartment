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