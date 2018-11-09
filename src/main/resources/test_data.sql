INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (1, 'Richard', 'Dawkins', '');
INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (3, 'Arthur', 'Doyle', 'Conan');
INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (93, 'L''Done', 'None', 'None');
INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (94, 'L"Done', 'None', 'None');
INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (4, 'Imaginary', 'Imaginary', 'Imaginary');
INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (55, 'a', 'a', 'a');

INSERT INTO Work (id, title)
  VALUES (1, 'The God Delusion');
INSERT INTO Work (id, title)
  VALUES (2, 'The Selfish Gene');
INSERT INTO Work (id, title)
  VALUES (3, 'The Hound of the Baskervilles');
INSERT INTO Work (id, title)
  VALUES (4, 'A Study in Scarlet');

INSERT INTO Work (id, title)
  VALUES (66, 'W1');
INSERT INTO Work (id, title)
  VALUES (77, 'W2');
INSERT INTO Work (id, title)
  VALUES (88, 'W3');

INSERT INTO Work (id, title, description)
  VALUES (5, 'Imaginary', 'Assume that this is a very long description');

INSERT INTO WorkAuthor (workId, authorId)
  VALUES (1, 1);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (2, 1);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (3, 3);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (4, 3);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (5, 4);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (66, 55);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (77, 55);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (88, 55);

INSERT INTO Subject (id, subject)
  VALUES (1, 'Biology');
INSERT INTO Subject (id, subject)
  VALUES (2, 'Science');
INSERT INTO Subject (id, subject)
  VALUES (3, 'Religion');
INSERT INTO Subject (id, subject)
  VALUES (4, 'Detective');

INSERT INTO Subject (id, subject)
  VALUES (5, 'Imaginary');

INSERT INTO WorkSubject (workId, subjectId)
  VALUES (1, 1);
INSERT INTO WorkSubject (workId, subjectId)
  VALUES (1, 2);
INSERT INTO WorkSubject (workId, subjectId)
  VALUES (1, 3);
INSERT INTO WorkSubject (workId, subjectId)
  VALUES (2, 1);
INSERT INTO WorkSubject (workId, subjectId)
  VALUES (2, 2);
INSERT INTO WorkSubject (workId, subjectId)
  VALUES (3, 4);
INSERT INTO WorkSubject (workId, subjectId)
  VALUES (4, 4);
INSERT INTO WorkSubject(workId, subjectId)
  VALUES (5, 5);


/*UNKNOWN PUBLISHER*/
INSERT INTO Publisher (id, name)
  VALUES (1, NULL);
INSERT INTO Publisher (id, name)
  VALUES (2, 'Bantam Books');
INSERT INTO Publisher (id, name)
  VALUES (3, 'Oxford University Press');

INSERT INTO Book (id, workId, publisherId, edition)
  VALUES (1, 1, 2, 1);
INSERT INTO Book (id, workId, publisherId, edition)
  VALUES (2, 2, 3, 1);

INSERT INTO Reader (id, email, password)
  VALUES (1, 'reader@mail.com', '$2a$12$hEfJ8syqZTc/KzjRU1ySm.N1Q2h4xxO6fBzLTCLzIlbJfAbCaqXLa')