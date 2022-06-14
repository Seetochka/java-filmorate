CREATE TABLE mpa_ref (
    name varchar
);

CREATE TABLE genre_ref (
    name varchar
);

INSERT INTO mpa_ref (name) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

merge into mpa
    using (mpa_ref) on (mpa.name = mpa_ref.name)
when not matched then
    insert(name) values(mpa_ref.name);

INSERT INTO genre_ref (name) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

merge into genre
    using (genre_ref) on (genre.name = genre_ref.name)
when not matched then
    insert(name) values(genre_ref.name);

DROP TABLE mpa_ref;
DROP TABLE genre_ref;
