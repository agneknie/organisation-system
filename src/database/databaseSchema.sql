CREATE TABLE User(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    forename TEXT NOT NULL,
    passwordHash TEXT NOT NULL,
    keepLoggedIn BOOLEAN NOT NULL
);

CREATE TABLE Module(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    userId INTEGER NOT NULL,
    code TEXT NOT NULL,
    fullName TEXT NOT NULL,
    credits INTEGER NOT NULL,
    semester TEXT NOT NULL,
    studyYear INTEGER NOT NULL,
    colour TEXT NOT NULL,
    FOREIGN KEY (userId)
        REFERENCES User (id)
);

CREATE TABLE Assignment(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    userId INTEGER NOT NULL,
    moduleId INTEGER NOT NULL,
    fullName TEXT NOT NULL,
    percentWorth INTEGER NOT NULL,
    maxScore INTEGER,
    score INTEGER,
    FOREIGN KEY (userId)
        REFERENCES User (id),
    FOREIGN KEY (moduleId)
        REFERENCES Module (id)
);

PRAGMA foreign_keys=on;