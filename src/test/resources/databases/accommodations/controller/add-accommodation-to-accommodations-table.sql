WITH ins AS (
    INSERT INTO accommodations (daily_rate, availability, size, accommodation_type, country, city, street, build_number)
        VALUES (450.00, 15, 'guesthouse', 'GUESTHOUSE', 'Ukraine', 'Kharkiv', 'Freedom Square', 5)
        RETURNING id
)
INSERT INTO accommodation_amenities (accommodation_id, amenity)
SELECT id, unnest(ARRAY['wifi', 'hot tub', 'barbecue'])
FROM ins;