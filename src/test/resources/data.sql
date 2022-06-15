INSERT INTO user (email, login, name, birthday) VALUES ('mail@mail.ru', 'dolore', 'Nick Name', '1946-08-20');
INSERT INTO user (email, login, name, birthday) VALUES ('email@email.ru', 'ipsum', 'Name Nick', '1976-12-24');
INSERT INTO user (email, login, name, birthday) VALUES ('testemail@email.ru', 'regut', 'Tom Smith', '1996-12-24');

INSERT INTO friend (user_id, friend_id) VALUES (1, 2);
INSERT INTO friend (user_id, friend_id) VALUES (1, 3);

INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES ('film', 'cool film', '1995-05-12', 300, 1);
INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES ('road', 'brick', '2015-09-24', 147, 4);

INSERT INTO `like` (user_id, film_id) VALUES (2, 2);
INSERT INTO `like` (user_id, film_id) VALUES (1, 2);

INSERT INTO review (content, is_positive, user_id, film_id)
VALUES ('So good', true, 1, 1), ('So bad',false, 2, 1), ('So bad',false, 1, 2);
