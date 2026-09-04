package Offline_3;

import java.util.HashMap;
import java.util.Map;

//MEDIATOR INTERFACE

interface ResultProcessingMediator {

        void registerDepartment(DepartmentOffice department);

        void registerController(ControllerOfExaminations controller);

        void registerDSW(DSW dsw);

        void registerStudent(Student student);

        void submitDepartmentConfirmation(String studentId);

        void publishFinalResult(String studentId);

        void issueTestimonial(String studentId);

        void issueCertificateAndTranscript(String studentId);

        void displayStatus(String studentId);
}

//STUDENT STATUS

class StudentStatus {

        boolean departmentConfirmed;
        boolean officeOrderIssued;
        boolean testimonialIssued;
        boolean certificateIssued;
        boolean transcriptIssued;

        public StudentStatus() {
                departmentConfirmed = false;
                officeOrderIssued = false;
                testimonialIssued = false;
                certificateIssued = false;
                transcriptIssued = false;
        }
}

//CONCRETE MEDIATOR
class ResultProcessingCoordinator implements ResultProcessingMediator {

        private DepartmentOffice department;
        private ControllerOfExaminations controller;
        private DSW dsw;

        private Map<String, Student> students = new HashMap<>();
        private Map<String, StudentStatus> statuses = new HashMap<>();

        // -------- Registration --------

        @Override
        public void registerDepartment(DepartmentOffice department) {
                this.department = department;
        }

        @Override
        public void registerController(
                        ControllerOfExaminations controller) {

                this.controller = controller;
        }

        @Override
        public void registerDSW(DSW dsw) {
                this.dsw = dsw;
        }

        @Override
        public void registerStudent(Student student) {

                students.put(student.getId(), student);

                statuses.put(
                                student.getId(),
                                new StudentStatus());

                System.out.println(
                                "\nStudent registered: " + student.getName());
        }

        //Step 1

        @Override
        public void submitDepartmentConfirmation(String studentId) {

                StudentStatus status = statuses.get(studentId);

                if (status == null) {
                        System.out.println("Invalid student.");
                        return;
                }

                if (status.departmentConfirmed) {
                        System.out.println(
                                        "Department confirmation already submitted.");
                        return;
                }

                status.departmentConfirmed = true;

                System.out.println(
                                "\nDepartment confirmation accepted for student ID: "
                                                + studentId);

                students.get(studentId).notifyStudent(
                                "Department has confirmed that you completed all academic requirements.");
        }

        //Step 2

        @Override
        public void publishFinalResult(String studentId) {

                StudentStatus status = statuses.get(studentId);

                if (status == null) {
                        System.out.println("Invalid student.");
                        return;
                }

                if (!status.departmentConfirmed) {

                        System.out.println(
                                        "\nREJECTED: Final result cannot be published.");

                        System.out.println(
                                        "Reason: Departmental confirmation is missing.");

                        return;
                }

                if (status.officeOrderIssued) {

                        System.out.println(
                                        "Office order has already been issued.");

                        return;
                }

                status.officeOrderIssued = true;

                System.out.println(
                                "\nFinal-result office order issued for student ID: "
                                                + studentId);

                students.get(studentId).notifyStudent(
                                "Your final-result publication office order has been issued.");
        }

        //Step 3

        @Override
        public void issueTestimonial(String studentId) {

                StudentStatus status = statuses.get(studentId);

                if (status == null) {
                        System.out.println("Invalid student.");
                        return;
                }

                if (!status.officeOrderIssued) {

                        System.out.println(
                                        "\nREJECTED: Testimonial cannot be issued.");

                        System.out.println(
                                        "Reason: Final-result office order has not been issued.");

                        return;
                }

                if (status.testimonialIssued) {

                        System.out.println(
                                        "Testimonial has already been issued.");

                        return;
                }

                status.testimonialIssued = true;

                System.out.println(
                                "\nDSW has issued the testimonial for student ID: "
                                                + studentId);

                students.get(studentId).notifyStudent(
                                "Your testimonial has been issued by DSW.");
        }

        //Step 4

        @Override
        public void issueCertificateAndTranscript(String studentId) {

                StudentStatus status = statuses.get(studentId);

                if (status == null) {
                        System.out.println("Invalid student.");
                        return;
                }

                if (!status.departmentConfirmed ||
                                !status.officeOrderIssued ||
                                !status.testimonialIssued) {

                        System.out.println(
                                        "\nREJECTED: Certificate and transcript cannot be issued.");

                        System.out.println(
                                        "Reason: All required previous steps are not complete.");

                        return;
                }

                if (status.certificateIssued &&
                                status.transcriptIssued) {

                        System.out.println(
                                        "Certificate and transcript have already been issued.");

                        return;
                }

                status.certificateIssued = true;
                status.transcriptIssued = true;

                System.out.println(
                                "\nCertificate and academic transcript issued for student ID: "
                                                + studentId);

                students.get(studentId).notifyStudent(
                                "Congratulations! Your certificate and academic transcript have been issued.");
        }

        //Display Status

        @Override
        public void displayStatus(String studentId) {

                StudentStatus status = statuses.get(studentId);

                if (status == null) {
                        System.out.println("Invalid student.");
                        return;
                }

                Student student = students.get(studentId);

                System.out.println("\n=================================");
                System.out.println(
                                "PROCESSING STATUS FOR: " + student.getName());
                System.out.println("Student ID: " + studentId);
                System.out.println("=================================");

                System.out.println(
                                "Department Confirmed : " +
                                                (status.departmentConfirmed ? "YES" : "NO"));

                System.out.println(
                                "Office Order Issued   : " +
                                                (status.officeOrderIssued ? "YES" : "NO"));

                System.out.println(
                                "Testimonial Issued    : " +
                                                (status.testimonialIssued ? "YES" : "NO"));

                System.out.println(
                                "Certificate Issued    : " +
                                                (status.certificateIssued ? "YES" : "NO"));

                System.out.println(
                                "Transcript Issued     : " +
                                                (status.transcriptIssued ? "YES" : "NO"));

                System.out.println("=================================");
        }
}

//DEPARTMENT

class DepartmentOffice {

        private ResultProcessingMediator mediator;

        public DepartmentOffice(ResultProcessingMediator mediator) {
                this.mediator = mediator;
        }

        public void confirmStudent(String studentId) {

                System.out.println(
                                "\nDepartment Office requests academic confirmation...");

                mediator.submitDepartmentConfirmation(studentId);
        }
}

// CONTROLLER

class ControllerOfExaminations {

        private ResultProcessingMediator mediator;

        public ControllerOfExaminations(
                        ResultProcessingMediator mediator) {

                this.mediator = mediator;
        }

        public void requestFinalResultPublication(String studentId) {

                System.out.println(
                                "\nController requests final-result publication...");

                mediator.publishFinalResult(studentId);
        }

        public void requestCertificateAndTranscript(
                        String studentId) {

                System.out.println(
                                "\nController requests certificate and transcript...");

                mediator.issueCertificateAndTranscript(studentId);
        }
}

//DSW 

class DSW {

        private ResultProcessingMediator mediator;

        public DSW(ResultProcessingMediator mediator) {
                this.mediator = mediator;
        }

        public void requestTestimonial(String studentId) {

                System.out.println(
                                "\nDSW requests testimonial issuance...");

                mediator.issueTestimonial(studentId);
        }
}

//STUDENT

class Student {

        private String id;
        private String name;

        public Student(String id, String name) {
                this.id = id;
                this.name = name;
        }

        public String getId() {
                return id;
        }

        public String getName() {
                return name;
        }

        public void notifyStudent(String message) {

                System.out.println(
                                "NOTIFICATION to " + name + ": " + message);
        }
}

//MAIN

public class task2_2305153 {

        public static void main(String[] args) {

                // Mediator
                ResultProcessingCoordinator coordinator = new ResultProcessingCoordinator();

                // Colleagues
                DepartmentOffice department = new DepartmentOffice(coordinator);

                ControllerOfExaminations controller = new ControllerOfExaminations(coordinator);

                DSW dsw = new DSW(coordinator);

                // Student
                Student student = new Student("2005001", "Rahim");

                // Register all participants
                coordinator.registerDepartment(department);

                coordinator.registerController(controller);

                coordinator.registerDSW(dsw);

                coordinator.registerStudent(student);

                System.out.println(
                                "\n========== PROCESS START ==========");

                // 1. Attempt to publish result BEFORE department confirmation
                System.out.println(
                                "\n1. Attempting to publish result before confirmation:");

                controller.requestFinalResultPublication(
                                student.getId());

                // 2. Department submits confirmation
                System.out.println(
                                "\n2. Department submits confirmation:");

                department.confirmStudent(
                                student.getId());

                // 3. Early attempt to issue certificate and transcript
                System.out.println(
                                "\n3. Attempting certificate/transcript early:");

                controller.requestCertificateAndTranscript(
                                student.getId());

                // 4. Final-result office order
                System.out.println(
                                "\n4. Issuing final-result office order:");

                controller.requestFinalResultPublication(
                                student.getId());

                // 5. Testimonial
                System.out.println(
                                "\n5. Issuing testimonial:");

                dsw.requestTestimonial(
                                student.getId());

                // 6. Certificate and transcript
                System.out.println(
                                "\n6. Issuing certificate and transcript:");

                controller.requestCertificateAndTranscript(
                                student.getId());

                // 7. Final Status
                System.out.println(
                                "\n7. Displaying final status:");

                coordinator.displayStatus(
                                student.getId());

                System.out.println(
                                "\n========== PROCESS COMPLETE ==========");
        }
}