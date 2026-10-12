-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Oct 12, 2026 at 06:06 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `faculty_technology_management`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `Admin_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`Admin_ID`) VALUES
(1);

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `Attendance_ID` int(11) NOT NULL,
  `Student_ID` int(11) NOT NULL,
  `Course_code` varchar(20) NOT NULL,
  `Session_date` date NOT NULL,
  `Type` enum('THEORY','PRACTICAL') NOT NULL,
  `Status` enum('PRESENT','ABSENT') NOT NULL DEFAULT 'ABSENT'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `Course_code` varchar(20) NOT NULL,
  `Course_name` varchar(150) NOT NULL,
  `Academic_year` int(11) DEFAULT NULL,
  `Semester` int(11) DEFAULT NULL,
  `Theory_credits` int(11) DEFAULT 0,
  `Practical_credits` int(11) DEFAULT 0,
  `Theory_hours` int(11) DEFAULT 0,
  `Practical_hours` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`Course_code`, `Course_name`, `Academic_year`, `Semester`, `Theory_credits`, `Practical_credits`, `Theory_hours`, `Practical_hours`) VALUES
('ICT2131', 'Database Management Systems', 2026, 1, 2, 1, 4, 2),
('ICT2132', 'Object Oriented Programming', 2026, 1, 2, 1, 4, 2),
('ICT2133', 'Data Structures', 2026, 1, 2, 1, 4, 2),
('ICT2134', 'Software Engineering', 2026, 1, 2, 1, 4, 2),
('ICT2135', 'Web Application Development', 2026, 1, 2, 1, 4, 2);

-- --------------------------------------------------------

--
-- Table structure for table `course_material`
--

CREATE TABLE `course_material` (
  `Material_ID` int(11) NOT NULL,
  `Course_code` varchar(20) NOT NULL,
  `Lecturer_ID` int(11) DEFAULT NULL,
  `Title` varchar(150) NOT NULL,
  `Material_type` varchar(50) DEFAULT NULL,
  `File_path` varchar(255) DEFAULT NULL,
  `Uploaded_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `enrollment`
--

CREATE TABLE `enrollment` (
  `Student_ID` int(11) NOT NULL,
  `Course_code` varchar(20) NOT NULL,
  `Academic_year` int(11) NOT NULL,
  `Semester` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `enrollment`
--

INSERT INTO `enrollment` (`Student_ID`, `Course_code`, `Academic_year`, `Semester`) VALUES
(11, 'ICT2131', 2026, 1),
(11, 'ICT2132', 2026, 1),
(12, 'ICT2131', 2026, 1),
(12, 'ICT2132', 2026, 1),
(13, 'ICT2133', 2026, 1),
(14, 'ICT2132', 2026, 1);

-- --------------------------------------------------------

--
-- Table structure for table `gpa`
--

CREATE TABLE `gpa` (
  `GPA_ID` int(11) NOT NULL,
  `Student_ID` int(11) NOT NULL,
  `SGPA` decimal(4,2) DEFAULT NULL,
  `CGPA` decimal(4,2) DEFAULT NULL,
  `Semester` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lecturer`
--

CREATE TABLE `lecturer` (
  `Lecturer_ID` int(11) NOT NULL,
  `Department` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `lecturer`
--

INSERT INTO `lecturer` (`Lecturer_ID`, `Department`) VALUES
(2, 'ICT'),
(3, 'ICT'),
(4, 'ICT'),
(5, 'ICT'),
(6, 'ICT');

-- --------------------------------------------------------

--
-- Table structure for table `marks`
--

CREATE TABLE `marks` (
  `Mark_ID` int(11) NOT NULL,
  `Student_ID` int(11) NOT NULL,
  `Course_code` varchar(20) NOT NULL,
  `CA_marks` decimal(5,2) DEFAULT NULL,
  `Final_exam_marks` decimal(5,2) DEFAULT NULL,
  `Total_marks` decimal(5,2) DEFAULT NULL,
  `Grade` varchar(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `medical`
--

CREATE TABLE `medical` (
  `Medical_ID` int(11) NOT NULL,
  `Student_ID` int(11) NOT NULL,
  `Attendance_ID` int(11) DEFAULT NULL,
  `Submission_date` date DEFAULT NULL,
  `Start_date` date DEFAULT NULL,
  `End_date` date DEFAULT NULL,
  `Reason` text DEFAULT NULL,
  `Approval_status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  `Status` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notice`
--

CREATE TABLE `notice` (
  `Notice_ID` int(11) NOT NULL,
  `Admin_ID` int(11) DEFAULT NULL,
  `Content` text DEFAULT NULL,
  `Title` varchar(150) NOT NULL,
  `Published_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `Student_ID` int(11) NOT NULL,
  `TG_num` varchar(30) NOT NULL,
  `Batch` varchar(30) DEFAULT NULL,
  `Department` varchar(100) DEFAULT NULL,
  `NIC` varchar(20) DEFAULT NULL,
  `Status` varchar(30) DEFAULT NULL,
  `Year` int(11) DEFAULT NULL,
  `Semester` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`Student_ID`, `TG_num`, `Batch`, `Department`, `NIC`, `Status`, `Year`, `Semester`) VALUES
(11, 'TG/2024/2111', '2024', 'ICT', 'TESTNIC00011', 'ACTIVE', 2, 1),
(12, 'TG/2024/2112', '2024', 'ICT', 'TESTNIC00012', 'ACTIVE', 2, 1),
(13, 'TG/2024/2113', '2024', 'ICT', 'TESTNIC00013', 'ACTIVE', 2, 1),
(14, 'TG/2024/2114', '2024', 'ICT', 'TESTNIC00014', 'ACTIVE', 2, 1),
(15, 'TG/2024/2115', '2024', 'ICT', 'TESTNIC00015', 'ACTIVE', 2, 1),
(16, 'TG/2024/2116', '2024', 'ICT', 'TESTNIC00016', 'ACTIVE', 2, 1),
(17, 'TG/2024/2117', '2024', 'ICT', 'TESTNIC00017', 'ACTIVE', 2, 1),
(18, 'TG/2024/2118', '2024', 'ICT', 'TESTNIC00018', 'ACTIVE', 2, 1),
(19, 'TG/2024/2119', '2024', 'ICT', 'TESTNIC00019', 'ACTIVE', 2, 1),
(20, 'TG/2024/2120', '2024', 'ICT', 'TESTNIC00020', 'ACTIVE', 2, 1),
(21, 'TG/2024/2121', '2024', 'ICT', 'TESTNIC00021', 'ACTIVE', 2, 1),
(22, 'TG/2024/2122', '2024', 'ICT', 'TESTNIC00022', 'ACTIVE', 2, 1),
(23, 'TG/2024/2123', '2024', 'ICT', 'TESTNIC00023', 'ACTIVE', 2, 1),
(24, 'TG/2024/2124', '2024', 'ICT', 'TESTNIC00024', 'ACTIVE', 2, 1),
(25, 'TG/2024/2125', '2024', 'ICT', 'TESTNIC00025', 'ACTIVE', 2, 1),
(26, 'TG/2024/2126', '2023', 'ICT', 'TESTNIC00026', 'REPEAT', 2, 1),
(27, 'TG/2024/2127', '2023', 'ICT', 'TESTNIC00027', 'REPEAT', 2, 1),
(28, 'TG/2024/2128', '2023', 'ICT', 'TESTNIC00028', 'REPEAT', 2, 1),
(29, 'TG/2024/2129', '2022', 'ICT', 'TESTNIC00029', 'BATCH_MISSED', 2, 1),
(30, 'TG/2024/2130', '2022', 'ICT', 'TESTNIC00030', 'BATCH_MISSED', 2, 1);

-- --------------------------------------------------------

--
-- Table structure for table `technical_officer`
--

CREATE TABLE `technical_officer` (
  `TO_ID` int(11) NOT NULL,
  `Department` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `technical_officer`
--

INSERT INTO `technical_officer` (`TO_ID`, `Department`) VALUES
(7, 'ICT'),
(8, 'ICT'),
(9, 'ICT'),
(10, 'ICT');

-- --------------------------------------------------------

--
-- Table structure for table `timetable`
--

CREATE TABLE `timetable` (
  `Timetable_ID` int(11) NOT NULL,
  `Course_code` varchar(20) NOT NULL,
  `Department` varchar(100) DEFAULT NULL,
  `Day` varchar(20) DEFAULT NULL,
  `Time_slot` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `User_ID` int(11) NOT NULL,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Role` enum('ADMIN','LECTURER','TECHNICAL_OFFICER','STUDENT') NOT NULL,
  `Email` varchar(100) NOT NULL,
  `Profile_pic` varchar(255) DEFAULT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `Fname` varchar(50) DEFAULT NULL,
  `Lname` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`User_ID`, `Username`, `Password`, `Role`, `Email`, `Profile_pic`, `Phone`, `Fname`, `Lname`) VALUES
(1, 'admin', 'admin123', 'ADMIN', 'admin@ftms.com', NULL, '0710000001', 'Admin', 'User'),
(2, 'lecturer01', 'lec123', 'LECTURER', 'lecturer01@ftms.com', NULL, '0710000011', 'Nimal', 'Perera'),
(3, 'lecturer02', 'lec123', 'LECTURER', 'lecturer02@ftms.com', NULL, '0710000012', 'Kasun', 'Silva'),
(4, 'lecturer03', 'lec123', 'LECTURER', 'lecturer03@ftms.com', NULL, '0710000013', 'Tharushi', 'Fernando'),
(5, 'lecturer04', 'lec123', 'LECTURER', 'lecturer04@ftms.com', NULL, '0710000014', 'Dilshan', 'Bandara'),
(6, 'lecturer05', 'lec123', 'LECTURER', 'lecturer05@ftms.com', NULL, '0710000015', 'Sanduni', 'Jayasinghe'),
(7, 'to01', 'to123', 'TECHNICAL_OFFICER', 'to01@ftms.com', NULL, '0720000001', 'Chamod', 'Perera'),
(8, 'to02', 'to123', 'TECHNICAL_OFFICER', 'to02@ftms.com', NULL, '0720000002', 'Hashini', 'Rathnayake'),
(9, 'to03', 'to123', 'TECHNICAL_OFFICER', 'to03@ftms.com', NULL, '0720000003', 'Isuru', 'Wijesinghe'),
(10, 'to04', 'to123', 'TECHNICAL_OFFICER', 'to04@ftms.com', NULL, '0720000004', 'Sachini', 'Kumari'),
(11, 'student01', 'stu123', 'STUDENT', 'student01@ftms.com', NULL, '0711000001', 'Nethmina', 'Perera'),
(12, 'student02', 'stu123', 'STUDENT', 'student02@ftms.com', NULL, '0711000002', 'Kasun', 'Silva'),
(13, 'student03', 'stu123', 'STUDENT', 'student03@ftms.com', NULL, '0711000003', 'Tharushi', 'Fernando'),
(14, 'student04', 'stu123', 'STUDENT', 'student04@ftms.com', NULL, '0711000004', 'Dilshan', 'Bandara'),
(15, 'student05', 'stu123', 'STUDENT', 'student05@ftms.com', NULL, '0711000005', 'Sanduni', 'Jayasinghe'),
(16, 'student06', 'stu123', 'STUDENT', 'student06@ftms.com', NULL, '0711000006', 'Chamod', 'Perera'),
(17, 'student07', 'stu123', 'STUDENT', 'student07@ftms.com', NULL, '0711000007', 'Hashini', 'Rathnayake'),
(18, 'student08', 'stu123', 'STUDENT', 'student08@ftms.com', NULL, '0711000008', 'Isuru', 'Wijesinghe'),
(19, 'student09', 'stu123', 'STUDENT', 'student09@ftms.com', NULL, '0711000009', 'Sachini', 'Kumari'),
(20, 'student10', 'stu123', 'STUDENT', 'student10@ftms.com', NULL, '0711000010', 'Dhanushka', 'Silva'),
(21, 'student11', 'stu123', 'STUDENT', 'student11@ftms.com', NULL, '0711000011', 'Tharindu', 'Perera'),
(22, 'student12', 'stu123', 'STUDENT', 'student12@ftms.com', NULL, '0711000012', 'Oshadi', 'Fernando'),
(23, 'student13', 'stu123', 'STUDENT', 'student13@ftms.com', NULL, '0711000013', 'Supun', 'Bandara'),
(24, 'student14', 'stu123', 'STUDENT', 'student14@ftms.com', NULL, '0711000014', 'Piumi', 'Jayawardena'),
(25, 'student15', 'stu123', 'STUDENT', 'student15@ftms.com', NULL, '0711000015', 'Chamika', 'Rathnayake'),
(26, 'student16', 'stu123', 'STUDENT', 'student16@ftms.com', NULL, '0711000016', 'Shashini', 'Perera'),
(27, 'student17', 'stu123', 'STUDENT', 'student17@ftms.com', NULL, '0711000017', 'Ravindu', 'Silva'),
(28, 'student18', 'stu123', 'STUDENT', 'student18@ftms.com', NULL, '0711000018', 'Methuli', 'Wijesinghe'),
(29, 'student19', 'stu123', 'STUDENT', 'student19@ftms.com', NULL, '0711000019', 'Sahan', 'Fernando'),
(30, 'student20', 'stu123', 'STUDENT', 'student20@ftms.com', NULL, '0711000020', 'Imashi', 'Bandara');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`Admin_ID`);

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`Attendance_ID`),
  ADD KEY `Course_code` (`Course_code`),
  ADD KEY `idx_attendance_student_course` (`Student_ID`,`Course_code`);

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`Course_code`);

--
-- Indexes for table `course_material`
--
ALTER TABLE `course_material`
  ADD PRIMARY KEY (`Material_ID`),
  ADD KEY `Lecturer_ID` (`Lecturer_ID`),
  ADD KEY `idx_course_material_course` (`Course_code`);

--
-- Indexes for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD PRIMARY KEY (`Student_ID`,`Course_code`,`Academic_year`,`Semester`),
  ADD KEY `Course_code` (`Course_code`);

--
-- Indexes for table `gpa`
--
ALTER TABLE `gpa`
  ADD PRIMARY KEY (`GPA_ID`),
  ADD KEY `idx_gpa_student` (`Student_ID`);

--
-- Indexes for table `lecturer`
--
ALTER TABLE `lecturer`
  ADD PRIMARY KEY (`Lecturer_ID`);

--
-- Indexes for table `marks`
--
ALTER TABLE `marks`
  ADD PRIMARY KEY (`Mark_ID`),
  ADD KEY `Course_code` (`Course_code`),
  ADD KEY `idx_marks_student_course` (`Student_ID`,`Course_code`);

--
-- Indexes for table `medical`
--
ALTER TABLE `medical`
  ADD PRIMARY KEY (`Medical_ID`),
  ADD KEY `Student_ID` (`Student_ID`),
  ADD KEY `Attendance_ID` (`Attendance_ID`);

--
-- Indexes for table `notice`
--
ALTER TABLE `notice`
  ADD PRIMARY KEY (`Notice_ID`),
  ADD KEY `Admin_ID` (`Admin_ID`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`Student_ID`),
  ADD UNIQUE KEY `TG_num` (`TG_num`),
  ADD UNIQUE KEY `NIC` (`NIC`);

--
-- Indexes for table `technical_officer`
--
ALTER TABLE `technical_officer`
  ADD PRIMARY KEY (`TO_ID`);

--
-- Indexes for table `timetable`
--
ALTER TABLE `timetable`
  ADD PRIMARY KEY (`Timetable_ID`),
  ADD KEY `idx_timetable_course` (`Course_code`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`User_ID`),
  ADD UNIQUE KEY `Username` (`Username`),
  ADD UNIQUE KEY `Email` (`Email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `Attendance_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `course_material`
--
ALTER TABLE `course_material`
  MODIFY `Material_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `gpa`
--
ALTER TABLE `gpa`
  MODIFY `GPA_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `marks`
--
ALTER TABLE `marks`
  MODIFY `Mark_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `medical`
--
ALTER TABLE `medical`
  MODIFY `Medical_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notice`
--
ALTER TABLE `notice`
  MODIFY `Notice_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `timetable`
--
ALTER TABLE `timetable`
  MODIFY `Timetable_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `User_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`Admin_ID`) REFERENCES `user` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`Student_ID`) REFERENCES `student` (`Student_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `attendance_ibfk_2` FOREIGN KEY (`Course_code`) REFERENCES `course` (`Course_code`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `course_material`
--
ALTER TABLE `course_material`
  ADD CONSTRAINT `course_material_ibfk_1` FOREIGN KEY (`Course_code`) REFERENCES `course` (`Course_code`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `course_material_ibfk_2` FOREIGN KEY (`Lecturer_ID`) REFERENCES `lecturer` (`Lecturer_ID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD CONSTRAINT `enrollment_ibfk_1` FOREIGN KEY (`Student_ID`) REFERENCES `student` (`Student_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `enrollment_ibfk_2` FOREIGN KEY (`Course_code`) REFERENCES `course` (`Course_code`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `gpa`
--
ALTER TABLE `gpa`
  ADD CONSTRAINT `gpa_ibfk_1` FOREIGN KEY (`Student_ID`) REFERENCES `student` (`Student_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `lecturer`
--
ALTER TABLE `lecturer`
  ADD CONSTRAINT `lecturer_ibfk_1` FOREIGN KEY (`Lecturer_ID`) REFERENCES `user` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `marks`
--
ALTER TABLE `marks`
  ADD CONSTRAINT `marks_ibfk_1` FOREIGN KEY (`Student_ID`) REFERENCES `student` (`Student_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `marks_ibfk_2` FOREIGN KEY (`Course_code`) REFERENCES `course` (`Course_code`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `medical`
--
ALTER TABLE `medical`
  ADD CONSTRAINT `medical_ibfk_1` FOREIGN KEY (`Student_ID`) REFERENCES `student` (`Student_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `medical_ibfk_2` FOREIGN KEY (`Attendance_ID`) REFERENCES `attendance` (`Attendance_ID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `notice`
--
ALTER TABLE `notice`
  ADD CONSTRAINT `notice_ibfk_1` FOREIGN KEY (`Admin_ID`) REFERENCES `admin` (`Admin_ID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `student_ibfk_1` FOREIGN KEY (`Student_ID`) REFERENCES `user` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `technical_officer`
--
ALTER TABLE `technical_officer`
  ADD CONSTRAINT `technical_officer_ibfk_1` FOREIGN KEY (`TO_ID`) REFERENCES `user` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `timetable`
--
ALTER TABLE `timetable`
  ADD CONSTRAINT `timetable_ibfk_1` FOREIGN KEY (`Course_code`) REFERENCES `course` (`Course_code`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
