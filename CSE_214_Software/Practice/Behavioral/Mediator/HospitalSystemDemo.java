import java.util.*;

// Enums for Results and Status
enum PathologyResult { NORMAL, CRITICAL }
enum RadiologyResult { OK, NOT_OK }

// Patient class to track state per patient
class Patient {
    private String patientId;
    private boolean requiresPathology;
    private boolean requiresRadiology;
    private boolean pathologyReceived;
    private boolean radiologyReceived;
    private PathologyResult pathResult;
    private RadiologyResult radResult;

    public Patient(String patientId, boolean requiresPathology, boolean requiresRadiology) {
        this.patientId = patientId;
        this.requiresPathology = requiresPathology;
        this.requiresRadiology = requiresRadiology;
        this.pathologyReceived = false;
        this.radiologyReceived = false;
    }

    public String getPatientId() { return patientId; }
    public boolean requiresPathology() { return requiresPathology; }
    public boolean requiresRadiology() { return requiresRadiology; }
    
    public boolean isPathologyReceived() { return pathologyReceived; }
    public void setPathologyResult(PathologyResult result) {
        this.pathResult = result;
        this.pathologyReceived = true;
    }

    public boolean isRadiologyReceived() { return radiologyReceived; }
    public void setRadiologyResult(RadiologyResult result) {
        this.radResult = result;
        this.radiologyReceived = true;
    }

    public boolean isComplete() {
        boolean pathDone = !requiresPathology || pathologyReceived;
        boolean radDone = !requiresRadiology || radiologyReceived;
        return pathDone && radDone;
    }

    public boolean isSingleInvestigation() {
        return (requiresPathology && !requiresRadiology) || (!requiresPathology && requiresRadiology);
    }
}

// Mediator Interface
interface EmergencyMediator {
    void requestPathology(String patientId);
    void requestRadiology(String patientId);
    void submitPathologyResult(String patientId, PathologyResult result);
    void submitRadiologyResult(String patientId, RadiologyResult result);
    void registerPatient(Patient patient);
}

// Abstract Colleague
abstract class MedicalStaff {
    protected EmergencyMediator mediator;

    public MedicalStaff(EmergencyMediator mediator) {
        this.mediator = mediator;
    }
}

// Concrete Colleague: Doctor
class Doctor extends MedicalStaff {
    public Doctor(EmergencyMediator mediator) {
        super(mediator);
    }

    public void orderInvestigations(Patient patient, boolean needPathology, boolean needRadiology) {
        mediator.registerPatient(patient);
        if (needPathology) {
            System.out.println("Pathology test requested for Patient " + patient.getPatientId() + ".");
            mediator.requestPathology(patient.getPatientId());
        }
        if (needRadiology) {
            System.out.println("Radiology investigation requested for Patient " + patient.getPatientId() + ".");
            mediator.requestRadiology(patient.getPatientId());
        }
    }

    public void notifyUrgent(String patientId, String message) {
        System.out.println("URGENT notification sent to Doctor for Patient " + patientId + ": " + message);
    }

    public void receiveCompleteResults(String patientId) {
        System.out.println("Complete results sent to Doctor for Patient " + patientId + ".");
    }
}

// Concrete Colleague: Pathology Lab
class PathologyLab extends MedicalStaff {
    public PathologyLab(EmergencyMediator mediator) {
        super(mediator);
    }

    public void performTest(String patientId, PathologyResult result) {
        // Simulating test performance and submitting back to central Emergency Center
        if (result == PathologyResult.CRITICAL) {
            System.out.println("Critical pathology result received for Patient " + patientId + ".");
        } else {
            System.out.println("Normal pathology result received for Patient " + patientId + ".");
        }
        mediator.submitPathologyResult(patientId, result);
    }
}

// Concrete Colleague: Radiology Unit
class RadiologyUnit extends MedicalStaff {
    public RadiologyUnit(EmergencyMediator mediator) {
        super(mediator);
    }

    public void performInvestigation(String patientId, RadiologyResult result) {
        // Simulating investigation and submitting back to central Emergency Center
        if (result == RadiologyResult.NOT_OK) {
            System.out.println("Not OK radiology result received for Patient " + patientId + ".");
        } else {
            System.out.println("Radiology result received for Patient " + patientId + ".");
        }
        mediator.submitRadiologyResult(patientId, result);
    }
}

// Concrete Mediator: Emergency Center
class EmergencyCenter implements EmergencyMediator {
    private Map<String, Patient> patientRegistry = new HashMap<>();
    private Doctor doctor;
    private PathologyLab pathologyLab;
    private RadiologyUnit radiologyUnit;

    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public void setPathologyLab(PathologyLab pathologyLab) { this.pathologyLab = pathologyLab; }
    public void setRadiologyUnit(RadiologyUnit radiologyUnit) { this.radiologyUnit = radiologyUnit; }

    @Override
    public void registerPatient(Patient patient) {
        patientRegistry.put(patient.getPatientId(), patient);
    }

    @Override
    public void requestPathology(String patientId) {
        pathologyLab.performTest(patientId, PathologyResult.NORMAL);

        // Forwarded to Pathology Lab through mediator
        // In asynchronous setup, this triggers the lab queue.
    }

    @Override
    public void requestRadiology(String patientId) {
        radiologyUnit.performInvestigation(patientId, RadiologyResult.OK);
        // Forwarded to Radiology Unit through mediator
    }

    @Override
    public void submitPathologyResult(String patientId, PathologyResult result) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null) return;

        patient.setPathologyResult(result);

        // Check for urgent result handling
        if (result == PathologyResult.CRITICAL) {
            doctor.notifyUrgent(patientId, "Critical pathology result found.");
            notifyPatientUrgent(patientId, "Critical pathology result found.");
        }

        checkAndDispatchResults(patient);
    }

    @Override
    public void submitRadiologyResult(String patientId, RadiologyResult result) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null) return;

        patient.setRadiologyResult(result);

        // Check for urgent result handling
        if (result == RadiologyResult.NOT_OK) {
            doctor.notifyUrgent(patientId, "Urgent: Radiology result is NOT OK.");
            notifyPatientUrgent(patientId, "Urgent: Radiology result is NOT OK.");
        }

        checkAndDispatchResults(patient);
    }

    private void checkAndDispatchResults(Patient patient) {
        if (patient.isSingleInvestigation()) {
            // Case 1: Only One Investigation Requested
            if (patient.isComplete()) {
                dispatchCompleteResults(patient);
            }
        } else {
            // Case 2: Both Investigations Requested
            if (patient.isComplete()) {
                System.out.println("All requested investigations completed for Patient " + patient.getPatientId() + ".");
                dispatchCompleteResults(patient);
            }
        }
    }

    private void dispatchCompleteResults(Patient patient) {
        doctor.receiveCompleteResults(patient.getPatientId());
        notifyPatientComplete(patient.getPatientId());
    }

    private void notifyPatientUrgent(String patientId, String message) {
        System.out.println("URGENT notification sent to Patient " + patientId + ".");
    }

    private void notifyPatientComplete(String patientId) {
        System.out.println("Complete results sent to Patient " + patientId + ".");
    }
}

// Main Driver to test the scenario
public class HospitalSystemDemo {
    public static void main(String[] args) {
        // Initialize Mediator
        EmergencyCenter emergencyCenter = new EmergencyCenter();

        // Initialize Colleagues connected only to the Mediator
        Doctor doctor = new Doctor(emergencyCenter);
        PathologyLab pathologyLab = new PathologyLab(emergencyCenter);
        RadiologyUnit radiologyUnit = new RadiologyUnit(emergencyCenter);

        emergencyCenter.setDoctor(doctor);
        emergencyCenter.setPathologyLab(pathologyLab);
        emergencyCenter.setRadiologyUnit(radiologyUnit);

        // Scenario Execution for Patient P101 (Both Pathology and Radiology requested)
        Patient p101 = new Patient("P101", true, true);
        
        System.out.println("--- Starting Workflow for Patient P101 ---");
        doctor.orderInvestigations(p101, true, true);

        // Suppose pathology result arrives first and is CRITICAL
        System.out.println("\n[Result Event 1]");
        pathologyLab.performTest("P101", PathologyResult.CRITICAL);

        // Later, the Radiology Unit reports OK
        System.out.println("\n[Result Event 2]");
        radiologyUnit.performInvestigation("P101", RadiologyResult.OK);
    }
}