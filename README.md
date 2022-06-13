# Filmorate
Сервис для работы с фильмами и оценками пользователей

## /films
#### GET Возвращает список всех фильмов
#### GET /{id} Возвращает фильм по id
#### POST Создает фильм
```
{
"name": "nisi eiusmod",
"description": "adipisicing",
"releaseDate": "1967-03-25",
"duration": 100
}
```
#### PUT Обновляет данные у существующего фильма
```
{
"name": "nisi eiusmod",
"id": 1,
"description": "adipisicing",
"releaseDate": "1967-03-25",
"duration": 120
}
```
#### PUT /{id}/like/{userId} Создает лайк
#### DELETE /{id}/like/{userId} Удаляет лайк
#### GET /popular?count= Возвращает число популярных фильмов

## /users
#### GET Возвращает список всех пользователей
#### GET /{id} Возвращает пользователя по id
#### POST Создает пользователя
```
{
"login": "dolore",
"name": "est adipisicing",
"email": "mail@mail.ru",
"birthday": "1946-08-20"
}
```
#### PUT Обновляет данные у существующего пользователя
```
{
"login": "dolore",
"name": "est adipisicing",
"id": 1,
"email": "mail@yandex.ru",
"birthday": "1946-08-20"
}
```
#### PUT /{id}/friends/{friendId} Добавляет в друзья
#### DELETE /{id}/like/{userId} Удаляет из друзей
#### GET /{id}/friends Возвращает список друзей
#### GET /{id}/friends/common/{otherId} Возвращает список друзей, общих с другим пользователем

## Модель базы данных
https://dbdiagram.io/d/6280dc607f945876b61e9962

![Диаграмма БД](images/diagram.png)

#### user
Таблица пользователей, все поля являются обязательными для заполнения, поле логин и email должны быть уникальными

#### friend
Таблица друзей, связи пользователей, может быть подтвержденной и не подтвержденной

#### film
Таблица фильмов, все поля являются обязательными для заполнения, для связи с жанрами служит отдельная таблица, рейтинг 
указывается из типа перечислений film_ratings, чтобы можно было реализовать поиск по названию фильма или описанию 
добавлены индексы для них

#### like
Таблица лайков, связь пользователя и фильма

#### genre
Таблица жанров, поле названия является обязательным для заполнения и должно быть уникальным

#### film_genre
Таблица связи фильма и жанра

#### mpa
Таблица рейтингов фильмов, в ней перечислены все рейтинги Ассоциации кинокомпаний Америки (англ. Motion Picture
Association of America)

### Примеры основных запросов (только на чтение)
Получение фильма по id
```
SELECT id,
        name,
        description,
        release_date,
        duration,
        mpa_id
FROM film
WHERE id = {id};
```
Получение всех фильмов
```
SELECT id,
        name,
        description,
        release_date,
        duration,
        mpa_id
FROM film;
```
Получение переданного количества популярных фильмов
```
SELECT f.id,
        f.name,
        f.description,
        f.release_date,
        f.duration,
        f.mpa_id,
        COUNT(l.film_id) AS count_likes
FROM film f
LEFT JOIN `like` l ON f.id = l.film_id
GROUP BY f.id
ORDER BY count_likes DESC
LIMIT {count};
```
Получение друзей пользователя
```
SELECT u.id,
        u.email,
        u.login,
        u.name,
        u.birthday
FROM user u
JOIN friend f ON u.id = f.friend_id
WHERE (user_id = {id})
OR (friend_id = {id} AND status = 1)
```
Получение друзей, общих с другим пользователем
```
SELECT id,
        email,
        login,
        name,
        birthday
FROM user
WHERE id IN (
        (
            SELECT user_id
            FROM friend
            WHERE friend_id = {userId}
            UNION
            SELECT friend_id
            FROM friend
            WHERE user_id = {userId}
        )
        INTERSECT
        (
            SELECT user_id
            FROM friend
            WHERE friend_id = {otherUserId}
            UNION
            SELECT friend_id
            FROM friend
            WHERE user_id = {otherUserId}
        )
)
```

---
## add-most-popular - 2 points
### Описание 
Добавлена возможность выводить топ N популярных фильмов с фильтрацией по году и/или жанру
### Эндпоинты 
#### GET /films/popular?count={limit}&genreId={genreId}&year={year}
count - указывает количество фильмов, которые необходимо вернуть, по умолчанию 10  
genreId - указывает на id жанра, для фильтрации по жанрам  
year - указывает на год, для фильтрации по годам  
Оба фильтра могут быть использованы одновременно. Также можно указать только один из двух. Также можно не указывать 
фильтры тогда будут возвращены самые популярные фильмы  из всех существующих

---
## add-review - 4 points
### Описание
Добавлена возможность оставлять комментарии к фильмам. Также любой пользователь может поставить комментарию лайк или
дизлайк
### Эндпоинты
#### POST /reviews — добавление нового отзыва 
#### PUT /reviews — редактирование уже имеющегося отзыв 
#### DELETE /reviews/{id} — Удаление уже имеющегося отзыва 
#### GET /reviews/{id} — получение отзыва по идентификатору 
#### GET /reviews?filmId={filmId}&count={count} — Получение всех отзывов по идентификатору фильма
count - казывает количество отзывов, которые необходимо вернуть, по умолчанию 10  
filmId - id фильма, к которому нужно вернуть, если не указан то возвращаются отзывы к любому фильму  
Сортировка происходит по рейтингу отзыва
#### PUT /reviews/{id}/like/{userId} — пользователь ставит лайк отзыву
id - id отзыва, которому ставится лайк  
userId - id ппользователя, который ставит лайк  
#### PUT /reviews/{id}/dislike/{userId} — пользователь ставит дизлайк отзыву
id - id отзыва, которому ставится дизлайк  
userId - id ппользователя, который ставит дизлайк  
#### DELETE /reviews/{id}/like/{userId} — пользователь удаляет лайк/дизлайк отзыву
id - id отзыва, у которого пользователь хочет удалить свой лайк  
userId - id ппользователя, который хочет удалить лайк  
#### DELETE /reviews/{id}/dislike/{userId} — пользователь удаляет дизлайк отзыву
id - id отзыва, у которого пользователь хочет удалить свой дизлайк  
userId - id ппользователя, который хочет удалить дизлайк  
---
