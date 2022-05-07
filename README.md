# Filmorate
Сервис для работы с фильмами и оценками пользователей

## /films
#### GET Возвращает список всех фильмов
#### GET /{id} Возвращает фильм по id
#### POST Создает фильм
`{
"name": "nisi eiusmod",
"description": "adipisicing",
"releaseDate": "1967-03-25",
"duration": 100
}`
#### PUT Обновляет данные у существующего фильма
`{
"name": "nisi eiusmod",
"id": 1,
"description": "adipisicing",
"releaseDate": "1967-03-25",
"duration": 120
}`
#### PUT /{id}/like/{userId} Создает лайк
#### DELETE /{id}/like/{userId} Удаляет лайк
#### GET /popular?count= Возвращает число популярных фильмов

## /users
#### GET Возвращает список всех пользователей
#### GET /{id} Возвращает пользователя по id
#### POST Создает пользователя
`{
"login": "dolore",
"name": "est adipisicing",
"email": "mail@mail.ru",
"birthday": "1946-08-20"
}`
#### PUT Обновляет данные у существующего пользователя
`{
"login": "dolore",
"name": "est adipisicing",
"id": 1,
"email": "mail@yandex.ru",
"birthday": "1946-08-20"
}`
#### PUT /{id}/friends/{friendId} Добавляет в друзья
#### DELETE /{id}/like/{userId} Удаляет из друзей
#### GET /{id}/friends Возвращает список друзей
#### GET /{id}/friends/common/{otherId} Возвращает список друзей, общих с другим пользователем
