CREATE TABLE Download (
  readerId       BIGINT,
  bookInstanceId BIGINT,
  dateDownloaded TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
  readerRating   INT
);

INSERT INTO Download (readerId, bookInstanceId, readerRating)
    VALUES (1, 1, 10);