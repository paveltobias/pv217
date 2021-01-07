set role u;
\c d;
CREATE TABLE course (id INT PRIMARY KEY, name varchar(255));
CREATE TABLE person (id INT PRIMARY KEY, name varchar(255), role INT,  email varchar(255), password varchar(255));
CREATE TABLE assignment (id INT PRIMARY KEY, teacherId INT, courseId INT , description varchar(255));
CREATE TABLE solution (id INT PRIMARY KEY, content varchar(255),assignmentId INT,studentId INT,mark char);
