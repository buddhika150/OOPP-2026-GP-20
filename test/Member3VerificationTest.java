package test;

import ftms.gui.TechnicalOfficerDashboard;
import ftms.model.Attendance;
import ftms.model.Medical;
import ftms.model.TechnicalOfficer;
import ftms.model.User;

import java.time.LocalDate;

/**
 * Verification test suite for Member 3 deliverables.
 */
public class Member3VerificationTest {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("STARTING MEMBER 3 VERIFICATION TESTS");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // 1. Test Inheritance & Polymorphism
        try {
            TechnicalOfficer to = new TechnicalOfficer(7, "to01", "pass123", "to01@ftms.com",
                    "pic.png", "0720000001", "Chamod", "Perera", "ICT");

            if (!(to instanceof User)) {
                throw new AssertionError("TechnicalOfficer must be an instance of User (Inheritance)");
            }
            if (!"Technical Officer".equals(to.getRoleName())) {
                throw new AssertionError("TechnicalOfficer polymorphism failed on getRoleName()");
            }
            if (!"Chamod Perera".equals(to.getFullName())) {
                throw new AssertionError("getFullName() failed");
            }
            System.out.println("[PASS] Test 1: Inheritance, Abstraction, Polymorphism verified.");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 1: " + t.getMessage());
            failed++;
        }

        // 2. Test Attendance Percentage Calculation (SRS Section 4)
        try {
            // Exactly 80% for 15 sessions = 12 sessions
            double pctTheory80 = Attendance.calculateAttendancePercentage(12, 15);
            if (Math.abs(pctTheory80 - 80.0) > 0.001) {
                throw new AssertionError("Expected 80.0% for 12/15, got: " + pctTheory80);
            }

            // Exactly 80% for 30 sessions = 24 sessions
            double pctCombined80 = Attendance.calculateAttendancePercentage(24, 30);
            if (Math.abs(pctCombined80 - 80.0) > 0.001) {
                throw new AssertionError("Expected 80.0% for 24/30, got: " + pctCombined80);
            }

            // More than 80%: 13/15 = 86.67%
            double pctTheoryAbove = Attendance.calculateAttendancePercentage(13, 15);
            if (pctTheoryAbove <= 80.0) {
                throw new AssertionError("Expected > 80%, got: " + pctTheoryAbove);
            }

            // Less than 80%: 11/15 = 73.33%
            double pctTheoryBelow = Attendance.calculateAttendancePercentage(11, 15);
            if (pctTheoryBelow >= 80.0) {
                throw new AssertionError("Expected < 80%, got: " + pctTheoryBelow);
            }

            System.out.println("[PASS] Test 2: Attendance percentage calculations (SRS Section 4) verified.");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 2: " + t.getMessage());
            failed++;
        }

        // 3. Test Attendance Validation & Encapsulation
        try {
            Attendance att = new Attendance();
            att.setType("theory");
            if (!"THEORY".equals(att.getType())) {
                throw new AssertionError("Expected type normalized to THEORY");
            }

            att.setType("PRACTICAL");
            if (!"PRACTICAL".equals(att.getType())) {
                throw new AssertionError("Expected type normalized to PRACTICAL");
            }

            boolean caughtInvalidType = false;
            try {
                att.setType("INVALID_SESSION");
            } catch (IllegalArgumentException e) {
                caughtInvalidType = true;
            }
            if (!caughtInvalidType) {
                throw new AssertionError("Expected IllegalArgumentException for invalid attendance type");
            }

            System.out.println("[PASS] Test 3: Attendance Encapsulation and validation verified.");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 3: " + t.getMessage());
            failed++;
        }

        // 4. Test Medical Encapsulation & Approval Status Validation
        try {
            Medical med = new Medical();
            med.setStudentId(1);
            med.setStartDate(LocalDate.of(2026, 10, 1));
            med.setEndDate(LocalDate.of(2026, 10, 5));
            med.setApprovalStatus("APPROVED");

            if (!"APPROVED".equals(med.getApprovalStatus())) {
                throw new AssertionError("Expected APPROVED status");
            }

            boolean caughtInvalidStatus = false;
            try {
                med.setApprovalStatus("UNKNOWN_STATUS");
            } catch (IllegalArgumentException e) {
                caughtInvalidStatus = true;
            }
            if (!caughtInvalidStatus) {
                throw new AssertionError("Expected IllegalArgumentException for invalid medical approval status");
            }

            System.out.println("[PASS] Test 4: Medical Encapsulation and approval status validation verified.");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 4: " + t.getMessage());
            failed++;
        }

        // 5. Test GUI Initialization
        try {
            TechnicalOfficer to = new TechnicalOfficer(7, "to01", "pass123", "to01@ftms.com",
                    null, "0720000001", "Chamod", "Perera", "ICT");

            TechnicalOfficerDashboard dashboard = new TechnicalOfficerDashboard(to);
            if (dashboard.getTitle() == null || !dashboard.getTitle().contains("Technical Officer")) {
                throw new AssertionError("Dashboard title mismatch");
            }
            dashboard.dispose(); // clean up resources

            System.out.println("[PASS] Test 5: Java Swing Technical Officer Dashboard UI initialization verified.");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 5: " + t.getMessage());
            failed++;
        }

        System.out.println("==================================================");
        System.out.println(String.format("TEST RESULTS: %d Passed, %d Failed", passed, failed));
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
