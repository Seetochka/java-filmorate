CREATE TABLE IF NOT EXISTS `mpa`
(
    id   int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name varchar(10)
);

CREATE TABLE IF NOT EXISTS `user`
(
    id         int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    email      varchar(50)     NOT NULL,
    login      varchar(50)     NOT NULL,
    name       varchar(50)     NOT NULL,
    birthday   date            NOT NULL,
    created_at timestamp DEFAULT (now()),
    updated_at timestamp DEFAULT (now())
);

CREATE TABLE IF NOT EXISTS `film`
(
    id           int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name         varchar(100),
    description  text            NOT NULL,
    release_date date            NOT NULL,
    duration     int             NOT NULL,
    mpa_id       int             NOT NULL,
    created_at   timestamp DEFAULT (now()),
    updated_at   timestamp DEFAULT (now())
);

CREATE TABLE IF NOT EXISTS `like`
(
    user_id    int NOT NULL,
    film_id    int NOT NULL,
    created_at timestamp DEFAULT (now()),
    updated_at timestamp DEFAULT (now()),
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS `friend`
(
    user_id    int NOT NULL,
    friend_id  int NOT NULL,
    status     boolean   DEFAULT false,
    created_at timestamp DEFAULT (now()),
    updated_at timestamp DEFAULT (now()),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS `genre`
(
    id         int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name       varchar(50)     NOT NULL,
    created_at timestamp DEFAULT (now()),
    updated_at timestamp DEFAULT (now())
);

CREATE TABLE IF NOT EXISTS `film_genre`
(
    film_id    int NOT NULL,
    genre_id   int NOT NULL,
    created_at timestamp DEFAULT (now()),
    updated_at timestamp DEFAULT (now()),
    PRIMARY KEY (film_id, genre_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS user_index_email ON `user` (email);

CREATE UNIQUE INDEX IF NOT EXISTS user_index_login ON `user` (login);

CREATE UNIQUE INDEX IF NOT EXISTS genre_index_name ON `genre` (name);

ALTER TABLE IF EXISTS `film`
    ADD FOREIGN KEY (mpa_id) REFERENCES `mpa` (id);

ALTER TABLE IF EXISTS `like`
    ADD FOREIGN KEY (user_id) REFERENCES `user` (id);

ALTER TABLE IF EXISTS `like`
    ADD FOREIGN KEY (film_id) REFERENCES `film` (id);

ALTER TABLE IF EXISTS friend
    ADD FOREIGN KEY (user_id) REFERENCES `user` (id);

ALTER TABLE IF EXISTS friend
    ADD FOREIGN KEY (friend_id) REFERENCES `user` (id);

ALTER TABLE IF EXISTS film_genre
    ADD FOREIGN KEY (film_id) REFERENCES `film` (id);

ALTER TABLE IF EXISTS film_genre
    ADD FOREIGN KEY (genre_id) REFERENCES `genre` (id);
