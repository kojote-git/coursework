CREATE TABLE Author (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  firstName VARCHAR(32) NOT NULL DEFAULT '',
  middleName VARCHAR(32) NOT NULL DEFAULT '',
  lastName VARCHAR(32) NOT NULL DEFAULT ''
);

CREATE TABLE Work(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL DEFAULT '',
  appearedBegins DATE,
  appearedEnds DATE,
  rangePrecision INT DEFAULT 0
);

CREATE TABLE WorkAuthor (
  authorId BIGINT,
  workId BIGINT,
  isFinished BOOLEAN,
  UNIQUE (authorId, workId),
  CONSTRAINT author_fk FOREIGN KEY (authorId) REFERENCES Author(id),
  CONSTRAINT work_fk FOREIGN KEY (workId) REFERENCES Work(id)
);

CREATE TABLE Subject (
  id INT PRIMARY KEY AUTO_INCREMENT,
  subject VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE WorkSubject (
  workId BIGINT,
  subjectId INT,
  UNIQUE (workId, subjectId),
  CONSTRAINT work_s_fk FOREIGN KEY (workId) REFERENCES Work(id),
  CONSTRAINT subject_w_fk FOREIGN KEY (subjectId) REFERENCES Subject(id)
);

CREATE TABLE Publisher(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64)
);

CREATE TABLE Book(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  workId BIGINT,
  publisherId BIGINT,
  CONSTRAINT work_b_fk FOREIGN KEY (workId) REFERENCES Work(id),
  CONSTRAINT publisher_b_fk FOREIGN KEY (publisherId) REFERENCES  Publisher(id)
);

CREATE TABLE Format (
  id INT PRIMARY KEY AUTO_INCREMENT,
  format VARCHAR(12) UNIQUE
);

CREATE TABLE BookInstance(
  bookId BIGINT,
  format INT,
  isbn VARCHAR(32),
  PRIMARY KEY(bookId, format),
  CONSTRAINT book_fk FOREIGN KEY (bookId) REFERENCES Book(id),
  CONSTRAINT format_fk FOREIGN KEY (format) REFERENCES Format(id)
);