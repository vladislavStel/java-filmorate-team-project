## Backend приложения "Online кинотеатр "Filmorate"

### < Стек технологий >
_Java SE 11, Spring Boot, JDBC, Maven, Lombok, Slf4j_

### < Функционал >
_Онлайн кинотеатр_

### < Запуск >
* _требуется среда разработки(IntelliJ IDEA)_
* _через меню IntelliJ IDEA запустить FilmorateApplication (src/main/java/ru/yandex/practicum/filmorate)
  с помощью команды 'run'_

### < Основные операции >
  <details>
    <summary>
      добавление фильмов и пользователей   
    </summary>
    Пример запросов:

    ```
      "/films"
      "/users"
    ```
  </details>

  <details>
    <summary>
      добавление в друзья, удаление из друзей, вывод списка общих друзей  
    </summary>
    Пример запросов:

    ```
      "/users/{id}/friends/{friendId}"
      "/users/{id}/friends/common/{otherId}"
    ```
  </details>

  <details>
    <summary>
      добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков  
    </summary>
    Пример запросов:

    ```
      "/films/{id}/like/{userId}"
      "/films/popular"
    ```
  </details>

  <details>
    <summary>
      получение списка всех жанров фильмов и по идентификатору  
    </summary>
    Пример запросов:

    ```
      "/films/genres"
      "/films/{id}/genres"
    ```
  </details>

### < ER диаграмма >

![ER_diagram](ER-diagram%20App%20Filmorate.png)

### < Примечание к ER диаграмме >
* _таблица 'genre' содержит список жанров фильма, у фильма может быть несколько жанров_
* _таблица 'rating' содержит перечень возрастных ограничений фильма в соответствии с рейтингом Ассоциации
  кинокомпаний (МРА)_
* _таблица 'friends' определяет статус 'дружба' между двумя пользователями_