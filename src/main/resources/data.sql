CREATE TABLE mpa_ref (
    name varchar
);

CREATE TABLE genre_ref (
    name varchar
);

INSERT INTO mpa_ref (name) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

MERGE INTO mpa
    USING (mpa_ref) ON (mpa.name = mpa_ref.name)
WHEN NOT MATCHED THEN
    INSERT (name) VALUES (mpa_ref.name);

INSERT INTO genre_ref (name) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

MERGE INTO genre
    USING (genre_ref) ON (genre.name = genre_ref.name)
WHEN NOT MATCHED THEN
    INSERT (name) VALUES (genre_ref.name);

DROP TABLE mpa_ref;
DROP TABLE genre_ref;
