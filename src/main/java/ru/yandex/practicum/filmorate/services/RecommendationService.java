package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Like;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис рекомендаций
 */
@Service
@Slf4j
public class RecommendationService {
    private static final int COUNT_RECOMMENDATION_FILMS = 10;

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    public RecommendationService(@Qualifier("filmDbStorage") FilmStorage filmStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
    }

    /**
     * Получает рекомендованные фильмы для пользователя
     */
    public Collection<Film> findRecommendations(int userId) {
        if (likeStorage.findLikeFilmsByUserId(userId).isEmpty()) {
            return filmStorage.findPopularFilms(COUNT_RECOMMENDATION_FILMS);
        }

        Collection<Like> likes = likeStorage.findLikes();

        Map<Integer, Collection<Integer>> aggregatedUserLikes = aggregateUserLikes(likes);

        Map<Integer, Set<Integer>> recommendation = getRecommendationMap(aggregatedUserLikes, userId);

        Set<Integer> filmIds = prepareRecommendation(recommendation);

        return filmIds
                .stream()
                .map(filmStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .limit(COUNT_RECOMMENDATION_FILMS)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Метод преобразовывает данные из БД в структуру
     * пользователь -> коллекция id фильмов, которые он лайкнул
     */
    private Map<Integer, Collection<Integer>> aggregateUserLikes(Collection<Like> likesOtherUsers) {
        Map<Integer, Collection<Integer>> aggregatedUserLikes = new HashMap<>();

        for (Like like : likesOtherUsers) {
            if (!aggregatedUserLikes.containsKey(like.getUserId())) {
                aggregatedUserLikes.put(like.getUserId(), new ArrayList<>());
            }

            aggregatedUserLikes.get(like.getUserId()).add(like.getFilmId());
        }

        return aggregatedUserLikes;
    }

    /**
     * Расставляет фильмы по количеству пересечений с искомым пользователем
     * количество пересечений -> коллекция id фильмов
     */
    private Map<Integer, Set<Integer>> getRecommendationMap(Map<Integer, Collection<Integer>> aggregateUserLikes, int userId) {
        Map<Integer, Set<Integer>> result = new TreeMap<>(Collections.reverseOrder());
        Collection<Integer> userLikes = aggregateUserLikes.remove(userId);

        for (Collection<Integer> otherLikes : aggregateUserLikes.values()) {
            Collection<Integer> common = new ArrayList<>(userLikes);
            Collection<Integer> recommend = new ArrayList<>(otherLikes);

            common.retainAll(otherLikes);
            recommend.removeAll(userLikes);

            if (!result.containsKey(common.size())) {
                result.put(common.size(), new HashSet<>());
            }

            result.get(common.size()).addAll(recommend);
        }

        return result;
    }

    /**
     * Собирает связное множество рекомендованных id фильмов
     */
    private Set<Integer> prepareRecommendation(Map<Integer, Set<Integer>> recommendLikes) {
        Set<Integer> filmIds = new LinkedHashSet<>();

        for (Set<Integer> likes: recommendLikes.values()) {
            filmIds.addAll(likes);
        }

        return filmIds;
    }
}
