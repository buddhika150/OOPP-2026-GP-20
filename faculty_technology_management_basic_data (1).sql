CREATE DATABASE IF NOT EXISTS faculty_technology_management;
USE faculty_technology_management;

-- BASIC DATA ONLY
-- 1 Admin
INSERT INTO User
(Username, Password, Role, Email, Phone, Fname, Lname)
VALUES
('admin', 'admin123', 'ADMIN', 'admin@ftms.com', '0710000001', 'Admin', 'User');

INSERT INTO Admin (Admin_ID)
SELECT User_ID FROM User WHERE Username = 'admin';

-- 5 Lecturers
INSERT INTO User
(Username, Password, Role, Email, Phone, Fname, Lname)
VALUES
('lecturer01','lec123','LECTURER','lecturer01@ftms.com','0710000011','Nimal','Perera'),
('lecturer02','lec123','LECTURER','lecturer02@ftms.com','0710000012','Kasun','Silva'),
('lecturer03','lec123','LECTURER','lecturer03@ftms.com','0710000013','Tharushi','Fernando'),
('lecturer04','lec123','LECTURER','lecturer04@ftms.com','0710000014','Dilshan','Bandara'),
('lecturer05','lec123','LECTURER','lecturer05@ftms.com','0710000015','Sanduni','Jayasinghe');

INSERT INTO Lecturer (Lecturer_ID, Department)
SELECT User_ID, 'ICT'
FROM User
WHERE Role = 'LECTURER';

-- 4 Technical Officers
INSERT INTO User
(Username, Password, Role, Email, Phone, Fname, Lname)
VALUES
('to01','to123','TECHNICAL_OFFICER','to01@ftms.com','0720000001','Chamod','Perera'),
('to02','to123','TECHNICAL_OFFICER','to02@ftms.com','0720000002','Hashini','Rathnayake'),
('to03','to123','TECHNICAL_OFFICER','to03@ftms.com','0720000003','Isuru','Wijesinghe'),
('to04','to123','TECHNICAL_OFFICER','to04@ftms.com','0720000004','Sachini','Kumari');

INSERT INTO Technical_Officer (TO_ID, Department)
SELECT User_ID, 'ICT'
FROM User
WHERE Role = 'TECHNICAL_OFFICER';

-- 20 Students
INSERT INTO User
(Username, Password, Role, Email, Phone, Fname, Lname)
VALUES
('student01','stu123','STUDENT','student01@ftms.com','0711000001','Nethmina','Perera'),
('student02','stu123','STUDENT','student02@ftms.com','0711000002','Kasun','Silva'),
('student03','stu123','STUDENT','student03@ftms.com','0711000003','Tharushi','Fernando'),
('student04','stu123','STUDENT','student04@ftms.com','0711000004','Dilshan','Bandara'),
('student05','stu123','STUDENT','student05@ftms.com','0711000005','Sanduni','Jayasinghe'),
('student06','stu123','STUDENT','student06@ftms.com','0711000006','Chamod','Perera'),
('student07','stu123','STUDENT','student07@ftms.com','0711000007','Hashini','Rathnayake'),
('student08','stu123','STUDENT','student08@ftms.com','0711000008','Isuru','Wijesinghe'),
('student09','stu123','STUDENT','student09@ftms.com','0711000009','Sachini','Kumari'),
('student10','stu123','STUDENT','student10@ftms.com','0711000010','Dhanushka','Silva'),
('student11','stu123','STUDENT','student11@ftms.com','0711000011','Tharindu','Perera'),
('student12','stu123','STUDENT','student12@ftms.com','0711000012','Oshadi','Fernando'),
('student13','stu123','STUDENT','student13@ftms.com','0711000013','Supun','Bandara'),
('student14','stu123','STUDENT','student14@ftms.com','0711000014','Piumi','Jayawardena'),
('student15','stu123','STUDENT','student15@ftms.com','0711000015','Chamika','Rathnayake'),
('student16','stu123','STUDENT','student16@ftms.com','0711000016','Shashini','Perera'),
('student17','stu123','STUDENT','student17@ftms.com','0711000017','Ravindu','Silva'),
('student18','stu123','STUDENT','student18@ftms.com','0711000018','Methuli','Wijesinghe'),
('student19','stu123','STUDENT','student19@ftms.com','0711000019','Sahan','Fernando'),
('student20','stu123','STUDENT','student20@ftms.com','0711000020','Imashi','Bandara');

INSERT INTO Student
(Student_ID, TG_num, Batch, Department, NIC, Status, Year, Semester)
SELECT
    User_ID,
    CONCAT('TG/2024/', 2100 + User_ID),
    CASE
        WHEN Username IN ('student16','student17','student18') THEN '2023'
        WHEN Username IN ('student19','student20') THEN '2022'
        ELSE '2024'
    END,
    'ICT',
    CONCAT('TESTNIC', LPAD(User_ID, 5, '0')),
    CASE
        WHEN Username IN ('student16','student17','student18') THEN 'REPEAT'
        WHEN Username IN ('student19','student20') THEN 'BATCH_MISSED'
        ELSE 'ACTIVE'
    END,
    2,
    1
FROM User
WHERE Role = 'STUDENT';

-- 5 Courses
INSERT INTO Course
(Course_code, Course_name, Academic_year, Semester,
 Theory_credits, Practical_credits, Theory_hours, Practical_hours)
VALUES
('ICT2131','Database Management Systems',2026,1,2,1,4,2),
('ICT2132','Object Oriented Programming',2026,1,2,1,4,2),
('ICT2133','Data Structures',2026,1,2,1,4,2),
('ICT2134','Software Engineering',2026,1,2,1,4,2),
('ICT2135','Web Application Development',2026,1,2,1,4,2);

-- Check inserted basic data
SELECT * FROM User;
SELECT * FROM Admin;
SELECT * FROM Lecturer;
SELECT * FROM Technical_Officer;
SELECT * FROM Student;
SELECT * FROM Course;
