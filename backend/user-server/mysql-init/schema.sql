CREATE DATABASE IF NOT EXISTS user_db;
USE user_db;

CREATE TABLE `Users` (
    `user_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_name` VARCHAR(50)    NOT NULL,
    `gender`    BOOLEAN        NOT NULL,
    `age`       INT             NULL,
    `join_at`   DATETIME        NOT NULL,
    `user_img`  BLOB            NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Friends` (
    `user_id`   BIGINT NOT NULL,
    `friend_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `friend_id`),
    FOREIGN KEY (`user_id`)   REFERENCES `User`(`user_id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
    FOREIGN KEY (`friend_id`) REFERENCES `User`(`user_id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4;