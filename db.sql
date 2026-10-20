CREATE DATABASE IF NOT EXISTS faculty_technology_management;
USE faculty_technology_management;

CREATE TABLE User (
 User_ID INT AUTO_INCREMENT PRIMARY KEY,
 Username VARCHAR(50) NOT NULL UNIQUE,
 Password VARCHAR(255) NOT NULL,
 Role ENUM('ADMIN','LECTURER','TECHNICAL_OFFICER','STUDENT') NOT NULL,
 Email VARCHAR(100) NOT NULL UNIQUE,
 Profile_pic VARCHAR(255),
 Phone VARCHAR(20),
 Fname VARCHAR(50),
 Lname VARCHAR(50)
);

CREATE TABLE Admin (
 Admin_ID INT PRIMARY KEY,
 FOREIGN KEY (Admin_ID) REFERENCES User(User_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Technical_Officer (
 TO_ID INT PRIMARY KEY,
 Department VARCHAR(100),
 FOREIGN KEY (TO_ID) REFERENCES User(User_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Lecturer (
 Lecturer_ID INT PRIMARY KEY,
 Department VARCHAR(100),
 FOREIGN KEY (Lecturer_ID) REFERENCES User(User_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Student (
 Student_ID INT PRIMARY KEY,
 TG_num VARCHAR(30) NOT NULL UNIQUE,
 Batch VARCHAR(30),
 Department VARCHAR(100),
 NIC VARCHAR(20) UNIQUE,
 Status VARCHAR(30),
 Year INT,
 Semester INT,
 FOREIGN KEY (Student_ID) REFERENCES User(User_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Course (
 Course_code VARCHAR(20) PRIMARY KEY,
 Course_name VARCHAR(150) NOT NULL,
 Academic_year INT,
 Semester INT,
 Theory_credits INT DEFAULT 0,
 Practical_credits INT DEFAULT 0,
 Theory_hours INT DEFAULT 0,
 Practical_hours INT DEFAULT 0
);

CREATE TABLE Course_Material (
 Material_ID INT AUTO_INCREMENT PRIMARY KEY,
 Course_code VARCHAR(20) NOT NULL,
 Lecturer_ID INT,
 Title VARCHAR(150) NOT NULL,
 Material_type VARCHAR(50),
 File_path VARCHAR(255),
 Uploaded_date DATE,
 FOREIGN KEY (Course_code) REFERENCES Course(Course_code) ON DELETE CASCADE ON UPDATE CASCADE,
 FOREIGN KEY (Lecturer_ID) REFERENCES Lecturer(Lecturer_ID) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE Notice (
 Notice_ID INT AUTO_INCREMENT PRIMARY KEY,
 Admin_ID INT,
 Content TEXT,
 Title VARCHAR(150) NOT NULL,
 Published_date DATE,
 FOREIGN KEY (Admin_ID) REFERENCES Admin(Admin_ID) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE Attendance (
 Attendance_ID INT AUTO_INCREMENT PRIMARY KEY,
 Student_ID INT NOT NULL,
 Course_code VARCHAR(20) NOT NULL,
 Session_date DATE NOT NULL,
 Type ENUM('THEORY','PRACTICAL') NOT NULL,
 FOREIGN KEY (Student_ID) REFERENCES Student(Student_ID) ON DELETE CASCADE ON UPDATE CASCADE,
 FOREIGN KEY (Course_code) REFERENCES Course(Course_code) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Medical (
 Medical_ID INT AUTO_INCREMENT PRIMARY KEY,
 Student_ID INT NOT NULL,
 Attendance_ID INT,
 Submission_date DATE,
 Start_date DATE,
 End_date DATE,
 Reason TEXT,
 Approval_status ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
 Status VARCHAR(30),
 FOREIGN KEY (Student_ID) REFERENCES Student(Student_ID) ON DELETE CASCADE ON UPDATE CASCADE,
 FOREIGN KEY (Attendance_ID) REFERENCES Attendance(Attendance_ID) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE Marks (
 Mark_ID INT AUTO_INCREMENT PRIMARY KEY,
 Student_ID INT NOT NULL,
 Course_code VARCHAR(20) NOT NULL,
 CA_marks DECIMAL(5,2),
 Final_exam_marks DECIMAL(5,2),
 Total_marks DECIMAL(5,2),
 Grade VARCHAR(5),
 FOREIGN KEY (Student_ID) REFERENCES Student(Student_ID) ON DELETE CASCADE ON UPDATE CASCADE,
 FOREIGN KEY (Course_code) REFERENCES Course(Course_code) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE GPA (
 GPA_ID INT AUTO_INCREMENT PRIMARY KEY,
 Student_ID INT NOT NULL,
 SGPA DECIMAL(4,2),
 CGPA DECIMAL(4,2),
 Semester INT,
 FOREIGN KEY (Student_ID) REFERENCES Student(Student_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Timetable (
 Timetable_ID INT AUTO_INCREMENT PRIMARY KEY,
 Course_code VARCHAR(20) NOT NULL,
 Department VARCHAR(100),
 Day VARCHAR(20),
 Time_slot VARCHAR(50),
 FOREIGN KEY (Course_code) REFERENCES Course(Course_code) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_attendance_student_course ON Attendance(Student_ID, Course_code);
CREATE INDEX idx_marks_student_course ON Marks(Student_ID, Course_code);
CREATE INDEX idx_course_material_course ON Course_Material(Course_code);
CREATE INDEX idx_gpa_student ON GPA(Student_ID);
CREATE INDEX idx_timetable_course ON Timetable(Course_code);




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

