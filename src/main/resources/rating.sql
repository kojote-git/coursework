CREATE TABLE Rating (
  readerId BIGINT,
  bookId BIGINT,
  rating INT
);

INSERT INTO Rating (readerId, bookId, rating) VALUES (1, 1, 5);
INSERT INTO Rating (readerId, bookId, rating) VALUES (1, 2, 6);