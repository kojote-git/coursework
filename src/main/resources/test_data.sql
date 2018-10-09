INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (1, 'Richard', 'Dawkins', '');
INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (3, 'Arthur', 'Doyle', 'Conan');

INSERT INTO Work (id, title, appearedBegins, appearedEnds, rangePrecision)
  VALUES (1, 'The God Delusion', '2006-10-2', '2006-10-2', 3);
INSERT INTO Work (id, title, appearedBegins, appearedEnds, rangePrecision)
  VALUES (2, 'The Selfish Gene', '1976-01-01', '1976-12-31', 1);
INSERT INTO Work (id, title, appearedBegins, appearedEnds, rangePrecision)
  VALUES (3, 'The Hound of the Baskervilles', '1902-04-01', '1902-04-30', 2);
INSERT INTO Work (id, title, appearedBegins, appearedEnds, rangePrecision)
  VALUES (4, 'A Study in Scarlet', '1887-01-01', '1887-12-31', 1);

INSERT INTO WorkAuthor (workId, authorId)
  VALUES (1, 1);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (2, 1);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (3, 3);
INSERT INTO WorkAuthor(workId, authorId)
  VALUES (4, 3);

INSERT INTO Subject (id, subject)
  VALUES (1, 'Biology');
INSERT INTO Subject (id, subject)
  VALUES (2, 'Science');
INSERT INTO Subject (id, subject)
  VALUES (3, 'Religion');
INSERT INTO Subject (id, subject)
  VALUES (4, 'Detective');

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