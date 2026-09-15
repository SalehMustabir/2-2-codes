
import java.util.*;


// ============================================================
// MEDIATOR INTERFACE
// ============================================================

// All communication between medical units goes through
// this mediator.
interface EmergencyMediator {

    void requestPathology(String patientId);

    void requestRadiology(String patientId);

    void pathologyResult(String patientId, PathologyResult result);

    void radiologyResult(String patientId, RadiologyResult result);
}


// ============================================================
// RESULT ENUMS
// ============================================================

enum PathologyResult {
    NORMAL,
    CRITICAL
}

enum RadiologyResult {
    OK,
    NOT_OK
}


// ============================================================
// PATIENT INVESTIGATION STATUS
// ============================================================

// Keeps track of investigations for each patient.
class InvestigationStatus {

    boolean pathologyRequested;
    boolean radiologyRequested;

    boolean pathologyCompleted;
    boolean radiologyCompleted;

    PathologyResult pathologyResult;
    RadiologyResult radiologyResult;

    public InvestigationStatus() {

        pathologyRequested = false;
        radiologyRequested = false;

        pathologyCompleted = false;
        radiologyCompleted = false;
    }
}


// ============================================================
// DOCTOR - COLLEAGUE
// ============================================================

class Doctor {

    // Doctor knows only the mediator.
    private EmergencyMediator mediator;

    public Doctor(EmergencyMediator mediator) {
        this.mediator = mediator;
    }


    // Doctor requests pathology test.
    public void requestPathology(String patientId) {

        System.out.println(
            "Doctor requested pathology test for " + patientId
        );

        mediator.requestPathology(patientId);
    }


    // Doctor requests radiology test.
    public void requestRadiology(String patientId) {

        System.out.println(
            "Doctor requested radiology investigation for " + patientId
        );

        mediator.requestRadiology(patientId);
    }


    // Doctor receives notification.
    public void notifyDoctor(String message) {

        System.out.println(
            "Doctor: " + message
        );
    }
}


// ============================================================
// PATIENT - PARTICIPANT
// ============================================================

class Patient {

    private String patientId;

    public Patient(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }


    // Patient receives notification.
    public void notifyPatient(String message) {

        System.out.println(
            "Patient " + patientId + ": " + message
        );
    }
}


// ============================================================
// PATHOLOGY LAB - COLLEAGUE
// ============================================================

class PathologyLab {

    // Pathology Lab knows only the mediator.
    private EmergencyMediator mediator;

    public PathologyLab(EmergencyMediator mediator) {
        this.mediator = mediator;
    }


    // Emergency Center forwards a pathology request here.
    public void performTest(String patientId) {

        System.out.println(
            "Pathology Lab performing test for " + patientId
        );

        // In a real system, result would come after testing.
        // Here we simulate the result.
    }


    // Lab submits result to Emergency Center.
    public void submitResult(
            String patientId,
            PathologyResult result) {

        mediator.pathologyResult(patientId, result);
    }
}


// ============================================================
// RADIOLOGY UNIT - COLLEAGUE
// ============================================================

class RadiologyUnit {

    // Radiology Unit knows only the mediator.
    private EmergencyMediator mediator;

    public RadiologyUnit(EmergencyMediator mediator) {
        this.mediator = mediator;
    }


    // Emergency Center forwards a radiology request here.
    public void performInvestigation(String patientId) {

        System.out.println(
            "Radiology Unit performing investigation for " + patientId
        );
    }


    // Radiology Unit submits result to Emergency Center.
    public void submitResult(
            String patientId,
            RadiologyResult result) {

        mediator.radiologyResult(patientId, result);
    }
}


// ============================================================
// CONCRETE MEDIATOR
// ============================================================

class EmergencyCenter implements EmergencyMediator {

    private Doctor doctor;
    private PathologyLab pathologyLab;
    private RadiologyUnit radiologyUnit;


    // Store patients.
    private Map<String, Patient> patients =
            new HashMap<>();


    // Store investigation status separately for each patient.
    private Map<String, InvestigationStatus> statuses =
            new HashMap<>();


    // --------------------------------------------------------
    // Register medical units
    // --------------------------------------------------------

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setPathologyLab(PathologyLab pathologyLab) {
        this.pathologyLab = pathologyLab;
    }

    public void setRadiologyUnit(RadiologyUnit radiologyUnit) {
        this.radiologyUnit = radiologyUnit;
    }


    // --------------------------------------------------------
    // Register patient
    // --------------------------------------------------------

    public void registerPatient(Patient patient) {

        patients.put(
            patient.getPatientId(),
            patient
        );

        statuses.put(
            patient.getPatientId(),
            new InvestigationStatus()
        );
    }


    // ========================================================
    // PATHOLOGY REQUEST
    // ========================================================

    @Override
    public void requestPathology(String patientId) {

        InvestigationStatus status =
                statuses.get(patientId);

        if (status == null) {
            System.out.println("Patient not found.");
            return;
        }


        // Mark pathology as requested.
        status.pathologyRequested = true;

        System.out.println(
            "Emergency Center: Pathology test requested for "
            + patientId
        );


        // Forward request to Pathology Lab.
        pathologyLab.performTest(patientId);
    }


    // ========================================================
    // RADIOLOGY REQUEST
    // ========================================================

    @Override
    public void requestRadiology(String patientId) {

        InvestigationStatus status =
                statuses.get(patientId);

        if (status == null) {
            System.out.println("Patient not found.");
            return;
        }


        // Mark radiology as requested.
        status.radiologyRequested = true;

        System.out.println(
            "Emergency Center: Radiology investigation requested for "
            + patientId
        );


        // Forward request to Radiology Unit.
        radiologyUnit.performInvestigation(patientId);
    }


    // ========================================================
    // PATHOLOGY RESULT
    // ========================================================

    @Override
    public void pathologyResult(
            String patientId,
            PathologyResult result) {

        InvestigationStatus status =
                statuses.get(patientId);

        if (status == null) {
            return;
        }


        // Store the result.
        status.pathologyResult = result;
        status.pathologyCompleted = true;


        System.out.println(
            "Pathology result received for "
            + patientId + ": " + result
        );


        // ----------------------------------------------------
        // URGENT RESULT
        // ----------------------------------------------------
        // CRITICAL pathology result must be reported
        // immediately, even if radiology is still pending.

        if (result == PathologyResult.CRITICAL) {

            doctor.notifyDoctor(
                "URGENT: Critical pathology result for "
                + patientId
            );

            patients.get(patientId).notifyPatient(
                "URGENT: Critical pathology result received."
            );
        }


        // Check whether all requested investigations
        // have completed.
        checkCompletion(patientId);
    }


    // ========================================================
    // RADIOLOGY RESULT
    // ========================================================

    @Override
    public void radiologyResult(
            String patientId,
            RadiologyResult result) {

        InvestigationStatus status =
                statuses.get(patientId);

        if (status == null) {
            return;
        }


        // Store the result.
        status.radiologyResult = result;
        status.radiologyCompleted = true;


        System.out.println(
            "Radiology result received for "
            + patientId + ": " + result
        );


        // ----------------------------------------------------
        // URGENT RESULT
        // ----------------------------------------------------
        // NOT_OK is considered urgent.

        if (result == RadiologyResult.NOT_OK) {

            doctor.notifyDoctor(
                "URGENT: Radiology result NOT OK for "
                + patientId
            );

            patients.get(patientId).notifyPatient(
                "URGENT: Radiology result is NOT OK."
            );
        }


        // Check whether all requested investigations
        // have completed.
        checkCompletion(patientId);
    }


    // ========================================================
    // CHECK INVESTIGATION COMPLETION
    // ========================================================

    private void checkCompletion(String patientId) {

        InvestigationStatus status =
                statuses.get(patientId);


        // ----------------------------------------------------
        // CASE 1: ONLY PATHOLOGY REQUESTED
        // ----------------------------------------------------

        if (status.pathologyRequested &&
            !status.radiologyRequested) {

            if (status.pathologyCompleted) {

                sendCompleteResults(patientId);
            }

            return;
        }


        // ----------------------------------------------------
        // CASE 2: ONLY RADIOLOGY REQUESTED
        // ----------------------------------------------------

        if (!status.pathologyRequested &&
            status.radiologyRequested) {

            if (status.radiologyCompleted) {

                sendCompleteResults(patientId);
            }

            return;
        }


        // ----------------------------------------------------
        // CASE 3: BOTH REQUESTED
        // ----------------------------------------------------

        if (status.pathologyRequested &&
            status.radiologyRequested) {

            // Wait until BOTH results arrive.
            if (status.pathologyCompleted &&
                status.radiologyCompleted) {

                sendCompleteResults(patientId);
            }
            else {

                System.out.println(
                    "Investigation still incomplete for "
                    + patientId
                );
            }
        }
    }


    // ========================================================
    // SEND COMPLETE RESULTS
    // ========================================================

    private void sendCompleteResults(String patientId) {

        InvestigationStatus status =
                statuses.get(patientId);


        System.out.println(
            "All requested investigations completed for "
            + patientId
        );


        // Build complete result message.
        StringBuilder result = new StringBuilder();

        result.append("Complete Results: ");


        if (status.pathologyRequested) {

            result.append(
                "Pathology = " +
                status.pathologyResult
            );
        }


        if (status.radiologyRequested) {

            result.append(
                ", Radiology = " +
                status.radiologyResult
            );
        }


        // Send complete result to Doctor.
        doctor.notifyDoctor(
            result.toString()
        );


        // Send complete result to Patient.
        patients.get(patientId).notifyPatient(
            result.toString()
        );
    }
}


// ============================================================
// MAIN CLASS
// ============================================================

public class MediatorC1 {

    public static void main(String[] args) {


        // ----------------------------------------------------
        // Create the Mediator
        // ----------------------------------------------------

        EmergencyCenter emergencyCenter =
                new EmergencyCenter();


        // ----------------------------------------------------
        // Create Colleagues
        // ----------------------------------------------------

        Doctor doctor =
                new Doctor(emergencyCenter);

        PathologyLab pathologyLab =
                new PathologyLab(emergencyCenter);

        RadiologyUnit radiologyUnit =
                new RadiologyUnit(emergencyCenter);


        // Register units with Emergency Center.
        emergencyCenter.setDoctor(doctor);
        emergencyCenter.setPathologyLab(pathologyLab);
        emergencyCenter.setRadiologyUnit(radiologyUnit);


        // ----------------------------------------------------
        // Create Patient
        // ----------------------------------------------------

        Patient patient =
                new Patient("P101");

        emergencyCenter.registerPatient(patient);


        System.out.println(
            "\n===== INVESTIGATION REQUEST ====="
        );


        // Doctor requests both investigations.
        doctor.requestPathology("P101");

        doctor.requestRadiology("P101");


        System.out.println(
            "\n===== PATHOLOGY RESULT ====="
        );


        // Pathology result arrives first.
        // CRITICAL -> immediate notification.
        // Radiology is still pending.
        pathologyLab.submitResult(
            "P101",
            PathologyResult.CRITICAL
        );


        System.out.println(
            "\n===== RADIOLOGY RESULT ====="
        );


        // Radiology result arrives later.
        // Now both investigations are complete.
        radiologyUnit.submitResult(
            "P101",
            RadiologyResult.OK
        );
    }
}

