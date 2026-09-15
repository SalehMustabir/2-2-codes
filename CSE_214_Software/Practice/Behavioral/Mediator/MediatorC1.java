
import java.util.HashMap;
import java.util.Map;

/*
 * ============================================================
 * PATIENT RECORD CLASS
 * ============================================================
 *
 * This class stores the investigation information of ONE patient.
 *
 * The EmergencyCenter needs to remember:
 *
 * 1. Which investigations were requested
 * 2. Which results have arrived
 * 3. The actual pathology result
 * 4. The actual radiology result
 *
 * We use boolean values instead of enum.
 *
 * pathologyRequested:
 *      true  -> pathology test was requested
 *      false -> pathology test was not requested
 *
 * pathologyCompleted:
 *      true  -> pathology result has arrived
 *      false -> result is still pending
 *
 * The same idea is used for radiology.
 */
class PatientRecord {

        private String patientId;

        // Was pathology requested?
        private boolean pathologyRequested;

        // Has pathology result arrived?
        private boolean pathologyCompleted;

        // Was radiology requested?
        private boolean radiologyRequested;

        // Has radiology result arrived?
        private boolean radiologyCompleted;

        // Store the actual results
        private String pathologyResult;
        private String radiologyResult;

        /*
         * Constructor
         */
        public PatientRecord(String patientId) {
                this.patientId = patientId;

                pathologyRequested = false;
                pathologyCompleted = false;

                radiologyRequested = false;
                radiologyCompleted = false;

                pathologyResult = null;
                radiologyResult = null;
        }

        // --------------------------------------------------------
        // Getter for patient ID
        // --------------------------------------------------------

        public String getPatientId() {
                return patientId;
        }

        // --------------------------------------------------------
        // Pathology methods
        // --------------------------------------------------------

        public void requestPathology() {
                pathologyRequested = true;
        }

        public void completePathology(String result) {
                pathologyCompleted = true;
                pathologyResult = result;
        }

        public boolean isPathologyRequested() {
                return pathologyRequested;
        }

        public boolean isPathologyCompleted() {
                return pathologyCompleted;
        }

        public String getPathologyResult() {
                return pathologyResult;
        }

        // --------------------------------------------------------
        // Radiology methods
        // --------------------------------------------------------

        public void requestRadiology() {
                radiologyRequested = true;
        }

        public void completeRadiology(String result) {
                radiologyCompleted = true;
                radiologyResult = result;
        }

        public boolean isRadiologyRequested() {
                return radiologyRequested;
        }

        public boolean isRadiologyCompleted() {
                return radiologyCompleted;
        }

        public String getRadiologyResult() {
                return radiologyResult;
        }

        /*
         * ========================================================
         * isInvestigationComplete()
         * ========================================================
         *
         * The entire investigation is complete only when EVERY
         * requested investigation has completed.
         *
         * Examples:
         *
         * Only pathology requested:
         * pathology completed -> COMPLETE
         *
         * Only radiology requested:
         * radiology completed -> COMPLETE
         *
         * Both requested:
         * pathology + radiology completed -> COMPLETE
         */
        public boolean isInvestigationComplete() {

                // If pathology was requested but is not completed,
                // the overall investigation is still incomplete.
                if (pathologyRequested && !pathologyCompleted) {
                        return false;
                }

                // If radiology was requested but is not completed,
                // the overall investigation is still incomplete.
                if (radiologyRequested && !radiologyCompleted) {
                        return false;
                }

                // Every requested investigation has completed.
                return true;
        }
}

/*
 * ============================================================
 * MEDIATOR INTERFACE
 * ============================================================
 *
 * This is the Mediator Design Pattern.
 *
 * The medical units communicate through this interface.
 *
 * Doctor does NOT directly communicate with PathologyLab.
 *
 * Doctor does NOT directly communicate with RadiologyUnit.
 *
 * PathologyLab does NOT directly communicate with RadiologyUnit.
 *
 * Instead:
 *
 * Doctor
 * |
 * v
 * EmergencyCenter
 * / \
 * v v
 * PathologyLab RadiologyUnit
 *
 * The EmergencyCenter coordinates everything.
 */
interface EmergencyMediator {

        /*
         * Doctor requests a pathology test.
         */
        void requestPathology(String patientId);

        /*
         * Doctor requests a radiology investigation.
         */
        void requestRadiology(String patientId);

        /*
         * Pathology Lab sends its result to the Emergency Center.
         */
        void pathologyResultReceived(
                        String patientId,
                        String result);

        /*
         * Radiology Unit sends its result to the Emergency Center.
         */
        void radiologyResultReceived(
                        String patientId,
                        String result);
}

/*
 * ============================================================
 * DOCTOR CLASS
 * ============================================================
 *
 * The Doctor knows ONLY about the EmergencyMediator.
 *
 * The Doctor does NOT have:
 *
 * PathologyLab pathologyLab;
 *
 * or
 *
 * RadiologyUnit radiologyUnit;
 *
 * This satisfies the requirement that medical units should
 * not communicate directly with each other.
 */
class Doctor {

        private EmergencyMediator emergencyCenter;

        /*
         * Constructor
         *
         * The Doctor receives the mediator.
         */
        public Doctor(EmergencyMediator emergencyCenter) {
                this.emergencyCenter = emergencyCenter;
        }

        /*
         * Request only a pathology test.
         */
        public void requestPathology(String patientId) {

                System.out.println(
                                "Doctor requested pathology test for Patient "
                                                + patientId);

                emergencyCenter.requestPathology(patientId);
        }

        /*
         * Request only a radiology investigation.
         */
        public void requestRadiology(String patientId) {

                System.out.println(
                                "Doctor requested radiology investigation for Patient "
                                                + patientId);

                emergencyCenter.requestRadiology(patientId);
        }

        /*
         * Request BOTH investigations.
         */
        public void requestBoth(String patientId) {

                System.out.println(
                                "Doctor requested pathology + radiology for Patient "
                                                + patientId);

                /*
                 * The Doctor still does not communicate directly
                 * with either medical unit.
                 *
                 * Both requests go through the Emergency Center.
                 */
                emergencyCenter.requestPathology(patientId);
                emergencyCenter.requestRadiology(patientId);
        }

        /*
         * Receive an urgent notification.
         */
        public void receiveUrgentNotification(
                        String patientId,
                        String result) {

                System.out.println(
                                "URGENT notification sent to Doctor: "
                                                + patientId
                                                + " -> "
                                                + result);
        }

        /*
         * Receive the final complete result.
         */
        public void receiveCompleteResults(
                        String patientId,
                        String pathologyResult,
                        String radiologyResult) {

                System.out.println(
                                "Complete results sent to Doctor for Patient "
                                                + patientId);

                if (pathologyResult != null) {
                        System.out.println(
                                        "  Pathology: " + pathologyResult);
                }

                if (radiologyResult != null) {
                        System.out.println(
                                        "  Radiology: " + radiologyResult);
                }
        }

        /*
         * Receive a single investigation result.
         *
         * This is used when only ONE investigation was requested.
         */
        public void receiveSingleResult(
                        String patientId,
                        String investigation,
                        String result) {

                System.out.println(
                                "Result sent to Doctor for Patient "
                                                + patientId
                                                + " -> "
                                                + investigation
                                                + ": "
                                                + result);
        }
}

/*
 * ============================================================
 * PATIENT CLASS
 * ============================================================
 *
 * The Patient also communicates through the Emergency Center.
 *
 * The Patient does NOT directly communicate with:
 *
 * - Doctor
 * - Pathology Lab
 * - Radiology Unit
 */
class Patient {

        private String patientId;

        private EmergencyMediator emergencyCenter;

        /*
         * Constructor
         */
        public Patient(
                        String patientId,
                        EmergencyMediator emergencyCenter) {

                this.patientId = patientId;
                this.emergencyCenter = emergencyCenter;
        }

        /*
         * Get patient ID.
         */
        public String getPatientId() {
                return patientId;
        }

        /*
         * Receive urgent notification.
         */
        public void receiveUrgentNotification(
                        String result) {

                System.out.println(
                                "URGENT notification sent to Patient "
                                                + patientId
                                                + ": "
                                                + result);
        }

        /*
         * Receive a single investigation result.
         */
        public void receiveSingleResult(
                        String investigation,
                        String result) {

                System.out.println(
                                "Result sent to Patient "
                                                + patientId
                                                + " -> "
                                                + investigation
                                                + ": "
                                                + result);
        }

        /*
         * Receive complete investigation results.
         */
        public void receiveCompleteResults(
                        String pathologyResult,
                        String radiologyResult) {

                System.out.println(
                                "Complete results sent to Patient "
                                                + patientId);

                if (pathologyResult != null) {

                        System.out.println(
                                        "  Pathology: " + pathologyResult);
                }

                if (radiologyResult != null) {

                        System.out.println(
                                        "  Radiology: " + radiologyResult);
                }
        }
}

/*
 * ============================================================
 * PATHOLOGY LAB CLASS
 * ============================================================
 *
 * The Pathology Lab communicates ONLY with the mediator.
 *
 * It does NOT know about:
 *
 * - Doctor
 * - Patient
 * - Radiology Unit
 *
 * When the lab finishes a test, it sends the result to
 * EmergencyCenter.
 */
class PathologyLab {

        private EmergencyMediator emergencyCenter;

        /*
         * Constructor
         */
        public PathologyLab(
                        EmergencyMediator emergencyCenter) {

                this.emergencyCenter = emergencyCenter;
        }

        /*
         * Perform pathology test.
         *
         * The result can be:
         *
         * NORMAL
         * CRITICAL
         */
        public void performTest(String patientId) {

                System.out.println(
                                "Pathology test performed for Patient "
                                                + patientId);

                /*
                 * For demonstration, we generate a CRITICAL result.
                 *
                 * In a real hospital system, this would come from
                 * the actual laboratory test.
                 */
                String result = "CRITICAL";

                /*
                 * Send result to EmergencyCenter.
                 *
                 * The lab does NOT send it directly to Doctor
                 * or Patient.
                 */
                emergencyCenter.pathologyResultReceived(
                                patientId,
                                result);
        }
}

/*
 * ============================================================
 * RADIOLOGY UNIT CLASS
 * ============================================================
 *
 * Similar to PathologyLab.
 *
 * It communicates only with the mediator.
 */
class RadiologyUnit {

        private EmergencyMediator emergencyCenter;

        /*
         * Constructor
         */
        public RadiologyUnit(
                        EmergencyMediator emergencyCenter) {

                this.emergencyCenter = emergencyCenter;
        }

        /*
         * Perform radiology investigation.
         *
         * Possible results:
         *
         * OK
         * NOT OK
         */
        public void performInvestigation(String patientId) {

                System.out.println(
                                "Radiology investigation performed for Patient "
                                                + patientId);

                /*
                 * For demonstration, we return OK.
                 *
                 * In a real system this would come from the
                 * radiologist/investigation.
                 */
                String result = "OK";

                /*
                 * Send the result to EmergencyCenter.
                 */
                emergencyCenter.radiologyResultReceived(
                                patientId,
                                result);
        }
}

/*
 * ============================================================
 * EMERGENCY CENTER
 * ============================================================
 *
 * THIS IS THE MEDIATOR.
 *
 * It coordinates all communication between:
 *
 * Doctor
 * Patient
 * PathologyLab
 * RadiologyUnit
 *
 * The medical units do not communicate directly.
 *
 * This class also maintains the investigation status for
 * every patient.
 */
class EmergencyCenter implements EmergencyMediator {

        /*
         * Store all patient records.
         *
         * Key = Patient ID
         * Value = PatientRecord
         *
         * Example:
         *
         * P101 -> PatientRecord
         * P102 -> PatientRecord
         */
        private Map<String, PatientRecord> patientRecords;

        /*
         * References to the medical participants.
         *
         * Notice that these references are held by the MEDIATOR,
         * not by the medical units.
         */
        private Doctor doctor;
        private PathologyLab pathologyLab;
        private RadiologyUnit radiologyUnit;

        /*
         * Patient objects are also stored here so that the
         * Emergency Center can notify the correct patient.
         */
        private Map<String, Patient> patients;

        /*
         * Constructor
         */
        public EmergencyCenter() {

                patientRecords = new HashMap<>();
                patients = new HashMap<>();
        }

        /*
         * ========================================================
         * REGISTER PARTICIPANTS
         * ========================================================
         *
         * These methods connect the participants to the
         * Emergency Center.
         */

        public void setDoctor(Doctor doctor) {
                this.doctor = doctor;
        }

        public void setPathologyLab(PathologyLab pathologyLab) {
                this.pathologyLab = pathologyLab;
        }

        public void setRadiologyUnit(
                        RadiologyUnit radiologyUnit) {

                this.radiologyUnit = radiologyUnit;
        }

        public void registerPatient(Patient patient) {

                patients.put(
                                patient.getPatientId(),
                                patient);
        }

        /*
         * ========================================================
         * GET OR CREATE PATIENT RECORD
         * ========================================================
         *
         * If a patient does not have a record yet, create one.
         */
        private PatientRecord getOrCreateRecord(
                        String patientId) {

                if (!patientRecords.containsKey(patientId)) {

                        patientRecords.put(
                                        patientId,
                                        new PatientRecord(patientId));
                }

                return patientRecords.get(patientId);
        }

        /*
         * ========================================================
         * REQUEST PATHOLOGY
         * ========================================================
         *
         * Called by Doctor.
         *
         * The Emergency Center:
         *
         * 1. Records that pathology was requested.
         * 2. Forwards the request to PathologyLab.
         */
        @Override
        public void requestPathology(String patientId) {

                PatientRecord record = getOrCreateRecord(patientId);

                /*
                 * Store the requested investigation.
                 */
                record.requestPathology();

                System.out.println(
                                "Emergency Center -> Pathology Lab: "
                                                + "Pathology test requested for Patient "
                                                + patientId);

                /*
                 * Forward the request to the lab.
                 */
                pathologyLab.performTest(patientId);
        }

        /*
         * ========================================================
         * REQUEST RADIOLOGY
         * ========================================================
         *
         * Called by Doctor.
         *
         * Again, the Doctor does not contact RadiologyUnit
         * directly.
         *
         * Everything goes through EmergencyCenter.
         */
        @Override
        public void requestRadiology(String patientId) {

                PatientRecord record = getOrCreateRecord(patientId);

                /*
                 * Store the requested investigation.
                 */
                record.requestRadiology();

                System.out.println(
                                "Emergency Center -> Radiology Unit: "
                                                + "Radiology investigation requested "
                                                + "for Patient "
                                                + patientId);

                /*
                 * Forward request to Radiology Unit.
                 */
                radiologyUnit.performInvestigation(patientId);
        }

        /*
         * ========================================================
         * PATHOLOGY RESULT RECEIVED
         * ========================================================
         *
         * Called by PathologyLab when its result is ready.
         */
        @Override
        public void pathologyResultReceived(
                        String patientId,
                        String result) {

                System.out.println(
                                "Critical pathology result received for Patient "
                                                + patientId);

                /*
                 * Get the patient's record.
                 */
                PatientRecord record = getOrCreateRecord(patientId);

                /*
                 * Save the pathology result.
                 */
                record.completePathology(result);

                /*
                 * ----------------------------------------------------
                 * URGENT RESULT CHECK
                 * ----------------------------------------------------
                 *
                 * According to the problem:
                 *
                 * Pathology CRITICAL = urgent.
                 *
                 * The Doctor and Patient must be notified
                 * IMMEDIATELY, even if radiology is still pending.
                 */
                if (result.equals("CRITICAL")) {

                        System.out.println(
                                        "URGENT: Critical pathology result "
                                                        + "found for Patient "
                                                        + patientId);

                        /*
                         * Notify Doctor.
                         */
                        doctor.receiveUrgentNotification(
                                        patientId,
                                        "CRITICAL pathology result");

                        /*
                         * Notify Patient.
                         */
                        Patient patient = patients.get(patientId);

                        if (patient != null) {

                                patient.receiveUrgentNotification(
                                                "CRITICAL pathology result");
                        }
                }

                /*
                 * Now check whether the entire investigation is
                 * complete.
                 */
                checkInvestigationCompletion(record);
        }

        /*
         * ========================================================
         * RADIOLOGY RESULT RECEIVED
         * ========================================================
         *
         * Called by RadiologyUnit.
         */
        @Override
        public void radiologyResultReceived(
                        String patientId,
                        String result) {

                System.out.println(
                                "Radiology result received for Patient "
                                                + patientId);

                /*
                 * Get patient's record.
                 */
                PatientRecord record = getOrCreateRecord(patientId);

                /*
                 * Save radiology result.
                 */
                record.completeRadiology(result);

                /*
                 * ----------------------------------------------------
                 * URGENT RESULT CHECK
                 * ----------------------------------------------------
                 *
                 * According to the problem:
                 *
                 * NOT OK = urgent.
                 */
                if (result.equals("NOT OK")) {

                        System.out.println(
                                        "URGENT: Radiology result NOT OK "
                                                        + "for Patient "
                                                        + patientId);

                        /*
                         * Notify Doctor immediately.
                         */
                        doctor.receiveUrgentNotification(
                                        patientId,
                                        "NOT OK radiology result");

                        /*
                         * Notify Patient immediately.
                         */
                        Patient patient = patients.get(patientId);

                        if (patient != null) {

                                patient.receiveUrgentNotification(
                                                "NOT OK radiology result");
                        }
                }

                /*
                 * Check whether all requested investigations
                 * have completed.
                 */
                checkInvestigationCompletion(record);
        }

        /*
         * ========================================================
         * CHECK INVESTIGATION COMPLETION
         * ========================================================
         *
         * This method determines whether we should:
         *
         * 1. Send a single result immediately, OR
         * 2. Wait for another investigation, OR
         * 3. Send the complete result set.
         */
        private void checkInvestigationCompletion(
                        PatientRecord record) {

                String patientId = record.getPatientId();

                /*
                 * ----------------------------------------------------
                 * CASE 1: ONLY PATHOLOGY REQUESTED
                 * ----------------------------------------------------
                 */
                if (record.isPathologyRequested()
                                && !record.isRadiologyRequested()) {

                        if (record.isPathologyCompleted()) {

                                System.out.println(
                                                "All requested investigations "
                                                                + "completed for Patient "
                                                                + patientId);

                                /*
                                 * Send pathology result to Doctor.
                                 */
                                doctor.receiveSingleResult(
                                                patientId,
                                                "Pathology",
                                                record.getPathologyResult());

                                /*
                                 * Send pathology result to Patient.
                                 */
                                Patient patient = patients.get(patientId);

                                if (patient != null) {

                                        patient.receiveSingleResult(
                                                        "Pathology",
                                                        record.getPathologyResult());
                                }
                        }

                        return;
                }

                /*
                 * ----------------------------------------------------
                 * CASE 2: ONLY RADIOLOGY REQUESTED
                 * ----------------------------------------------------
                 */
                if (record.isRadiologyRequested()
                                && !record.isPathologyRequested()) {

                        if (record.isRadiologyCompleted()) {

                                System.out.println(
                                                "All requested investigations "
                                                                + "completed for Patient "
                                                                + patientId);

                                /*
                                 * Send result to Doctor.
                                 */
                                doctor.receiveSingleResult(
                                                patientId,
                                                "Radiology",
                                                record.getRadiologyResult());

                                /*
                                 * Send result to Patient.
                                 */
                                Patient patient = patients.get(patientId);

                                if (patient != null) {

                                        patient.receiveSingleResult(
                                                        "Radiology",
                                                        record.getRadiologyResult());
                                }
                        }

                        return;
                }

                /*
                 * ----------------------------------------------------
                 * CASE 3: BOTH REQUESTED
                 * ----------------------------------------------------
                 *
                 * If both were requested, we must WAIT until
                 * BOTH results have arrived.
                 */
                if (record.isPathologyRequested()
                                && record.isRadiologyRequested()) {

                        /*
                         * If pathology is completed but radiology is
                         * still pending, do nothing.
                         */
                        if (record.isPathologyCompleted()
                                        && !record.isRadiologyCompleted()) {

                                System.out.println(
                                                "Pathology result received. "
                                                                + "Radiology still pending for Patient "
                                                                + patientId);

                                return;
                        }

                        /*
                         * If radiology is completed but pathology is
                         * still pending, do nothing.
                         */
                        if (!record.isPathologyCompleted()
                                        && record.isRadiologyCompleted()) {

                                System.out.println(
                                                "Radiology result received. "
                                                                + "Pathology still pending for Patient "
                                                                + patientId);

                                return;
                        }

                        /*
                         * Both results are now available.
                         */
                        if (record.isInvestigationComplete()) {

                                System.out.println(
                                                "All requested investigations "
                                                                + "completed for Patient "
                                                                + patientId);

                                /*
                                 * Send complete results to Doctor.
                                 */
                                doctor.receiveCompleteResults(
                                                patientId,
                                                record.getPathologyResult(),
                                                record.getRadiologyResult());

                                /*
                                 * Send complete results to Patient.
                                 */
                                Patient patient = patients.get(patientId);

                                if (patient != null) {

                                        patient.receiveCompleteResults(
                                                        record.getPathologyResult(),
                                                        record.getRadiologyResult());
                                }
                        }
                }
        }
}

/*
 * ============================================================
 * MAIN CLASS
 * ============================================================
 *
 * Demonstrates the complete Mediator Pattern solution.
 */
public class MediatorC1 {

        public static void main(String[] args) {

                /*
                 * ----------------------------------------------------
                 * STEP 1: Create the Emergency Center
                 * ----------------------------------------------------
                 *
                 * This will be our MEDIATOR.
                 */
                EmergencyCenter emergencyCenter = new EmergencyCenter();

                /*
                 * ----------------------------------------------------
                 * STEP 2: Create Doctor
                 * ----------------------------------------------------
                 *
                 * Doctor knows only about EmergencyCenter.
                 */
                Doctor doctor = new Doctor(emergencyCenter);

                /*
                 * ----------------------------------------------------
                 * STEP 3: Create Pathology Lab
                 * ----------------------------------------------------
                 *
                 * Pathology Lab also knows only about the mediator.
                 */
                PathologyLab pathologyLab = new PathologyLab(emergencyCenter);

                /*
                 * ----------------------------------------------------
                 * STEP 4: Create Radiology Unit
                 * ----------------------------------------------------
                 */
                RadiologyUnit radiologyUnit = new RadiologyUnit(emergencyCenter);

                /*
                 * ----------------------------------------------------
                 * STEP 5: Register the units with EmergencyCenter
                 * ----------------------------------------------------
                 *
                 * EmergencyCenter now knows all participants.
                 *
                 * But the participants do NOT know one another.
                 */
                emergencyCenter.setDoctor(doctor);

                emergencyCenter.setPathologyLab(
                                pathologyLab);

                emergencyCenter.setRadiologyUnit(
                                radiologyUnit);

                /*
                 * ----------------------------------------------------
                 * STEP 6: Create Patient P101
                 * ----------------------------------------------------
                 */
                Patient patient = new Patient(
                                "P101",
                                emergencyCenter);

                /*
                 * Register patient with EmergencyCenter.
                 */
                emergencyCenter.registerPatient(patient);

                /*
                 * ----------------------------------------------------
                 * STEP 7: Doctor requests BOTH investigations
                 * ----------------------------------------------------
                 *
                 * According to the problem:
                 *
                 * P101 needs:
                 *
                 * - Pathology
                 * - Radiology
                 */
                System.out.println(
                                "\n========== P101 CASE ==========\n");

                doctor.requestBoth("P101");
        }
}
