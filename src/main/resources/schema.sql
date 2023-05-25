DROP TABLE IF EXISTS USERS, FRIEND_LIST, FILM, LIKE_LIST, GENRE_LIST, GENRE, MPA, REVIEW, REVIEW_LIKE;

CREATE TABLE IF NOT EXISTS USERS
(
    user_id  bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login    varchar(50) NOT NULL,
    name     varchar(50) NOT NULL,
    birthday date,
    email    varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS FRIEND_LIST (
    user_id bigint REFERENCES USERS (user_id),
    friend_id bigint REFERENCES USERS (user_id)
);

CREATE TABLE IF NOT EXISTS MPA (
    mpa_id int PRIMARY KEY,
    name varchar(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILM (
    film_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL,
    description varchar(200) NOT NULL,
    releaseDate date,
    duration int CHECK (duration > 0),
    mpa_id int REFERENCES MPA (mpa_id)
);

CREATE TABLE IF NOT EXISTS LIKE_LIST (
    film_id bigint REFERENCES FILM (film_id),
    user_id bigint REFERENCES USERS (user_id)
);

CREATE TABLE IF NOT EXISTS GENRE (
    genre_id int PRIMARY KEY,
    name varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS GENRE_LIST (
    film_id bigint REFERENCES FILM (film_id),
    genre_id int REFERENCES GENRE (genre_id)
);

CREATE TABLE IF NOT EXISTS REVIEW (
    review_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    content varchar not null ,
    is_positive boolean,
    user_id bigint not null REFERENCES USERS(USER_ID) ON DELETE CASCADE,
    film_Id bigint not null REFERENCES FILM(FILM_ID) ON DELETE CASCADE,
    useful int
);
CREATE TABLE IF NOT EXISTS REVIEW_LIKE (
    review_id bigint REFERENCES REVIEW(review_id) ON DELETE CASCADE ,
    user_id   bigint REFERENCES USERS(USER_ID) ON DELETE CASCADE,
    is_like   boolean

);