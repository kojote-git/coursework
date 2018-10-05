INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (1, 'Richard', 'Dawkins', '');
INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (2, 'Stephen', 'Hawking', '');
INSERT INTO Author (id, firstName, lastName, middleName)
  VALUES (3, 'Arthur', 'Doyle', 'Conan');

INSERT INTO Work (id, title, firstAppeared)
  VALUES (1, 'The God Delusion', '2006-10-2');
INSERT INTO WorkAuthor (workId, authorId)
  VALUES (1, 1);