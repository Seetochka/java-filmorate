# Filmorate
Сервис для работы с фильмами и оценками пользователей

## /films
#### GET Возвращает список всех фильмов
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

## /users
#### GET Возвращает список всех пользователей
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
