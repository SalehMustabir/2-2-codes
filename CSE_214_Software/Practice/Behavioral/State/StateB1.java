// =========================================================
// STATE INTERFACE
// =========================================================
// This interface defines all operations that can be
// performed on a return request.
//
// Every state will implement these operations differently.
// For example, approve() behaves differently in Requested
// and Approved states.

interface ReturnState {

    void updateReason(String reason);

    void approve();

    void reject();

    void cancel();

    void itemDelivered();

    void inspect(boolean eligible);

    void refundSuccessful();

    void refundFailed();
}


// =========================================================
// CONTEXT CLASS: ReturnRequest
// =========================================================
// This class represents the actual return request.
//
// It stores:
// 1. The current state
// 2. The return reason
//
// It delegates all operations to the current state.
//
// The Context does not need to know the detailed behaviour
// of every state.

class ReturnRequest {

    // Current state of the return request.
    private ReturnState currentState;

    // Reason for returning the product.
    private String reason;


    // Constructor
    public ReturnRequest(String reason) {

        this.reason = reason;

        // Every new return request starts in Requested state.
        currentState = new RequestedState(this);
    }


    // -----------------------------------------------------
    // Getters and Setters
    // -----------------------------------------------------

    public String getReason() {
        return reason;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }


    public ReturnState getCurrentState() {
        return currentState;
    }


    // This method allows a state to change the current state.
    public void setState(ReturnState newState) {

        currentState = newState;

        System.out.println(
                "Current State: "
                + newState.getClass().getSimpleName()
        );
    }


    // -----------------------------------------------------
    // Public Operations
    // -----------------------------------------------------
    // Each operation is forwarded to the current state.

    public void updateReason(String reason) {
        currentState.updateReason(reason);
    }


    public void approve() {
        currentState.approve();
    }


    public void reject() {
        currentState.reject();
    }


    public void cancel() {
        currentState.cancel();
    }


    public void itemDelivered() {
        currentState.itemDelivered();
    }


    public void inspect(boolean eligible) {
        currentState.inspect(eligible);
    }


    public void refundSuccessful() {
        currentState.refundSuccessful();
    }


    public void refundFailed() {
        currentState.refundFailed();
    }
}


// =========================================================
// REQUESTED STATE
// =========================================================
// Initial state of the return request.
//
// Valid operations:
// - updateReason()
// - approve()
// - reject()
// - cancel()
//
// Invalid operations:
// - itemDelivered()
// - inspect()
// - refundSuccessful()
// - refundFailed()

class RequestedState implements ReturnState {

    private ReturnRequest request;


    public RequestedState(ReturnRequest request) {
        this.request = request;
    }


    @Override
    public void updateReason(String reason) {

        // The reason can be changed multiple times.
        request.setReason(reason);

        System.out.println(
                "Return reason updated to: " + reason
        );
    }


    @Override
    public void approve() {

        // Move from Requested to Approved.
        request.setState(new ApprovedState(request));

        System.out.println("Return request approved.");
    }


    @Override
    public void reject() {

        // Move from Requested to Rejected.
        request.setState(new RejectedState(request));

        System.out.println("Return request rejected.");
    }


    @Override
    public void cancel() {

        // Move from Requested to Cancelled.
        request.setState(new CancelledState(request));

        System.out.println("Return request cancelled.");
    }


    @Override
    public void itemDelivered() {

        System.out.println(
                "Invalid operation: Request must be approved first."
        );
    }


    @Override
    public void inspect(boolean eligible) {

        System.out.println(
                "Invalid operation: Item has not been delivered."
        );
    }


    @Override
    public void refundSuccessful() {

        System.out.println(
                "Invalid operation: Refund is not being processed."
        );
    }


    @Override
    public void refundFailed() {

        System.out.println(
                "Invalid operation: Refund is not being processed."
        );
    }
}


// =========================================================
// APPROVED STATE
// =========================================================
// In this state:
//
// - Return reason cannot be modified.
// - Request can be cancelled.
// - Item can be marked as delivered.
//
// Valid operations:
// - cancel()
// - itemDelivered()
//
// Invalid operations:
// - updateReason()
// - approve()
// - reject()
// - inspect()
// - refundSuccessful()
// - refundFailed()

class ApprovedState implements ReturnState {

    private ReturnRequest request;


    public ApprovedState(ReturnRequest request) {
        this.request = request;
    }


    @Override
    public void updateReason(String reason) {

        System.out.println(
                "Invalid operation: Return reason cannot be changed after approval."
        );
    }


    @Override
    public void approve() {

        System.out.println(
                "Invalid operation: Request is already approved."
        );
    }


    @Override
    public void reject() {

        System.out.println(
                "Invalid operation: Cannot reject an approved request directly."
        );
    }


    @Override
    public void cancel() {

        // The item has not been delivered yet.
        // Therefore, cancellation is allowed.
        request.setState(new CancelledState(request));

        System.out.println("Return request cancelled.");
    }


    @Override
    public void itemDelivered() {

        // Move from Approved to Delivered.
        request.setState(new DeliveredState(request));

        System.out.println("Returned item has been delivered.");
    }


    @Override
    public void inspect(boolean eligible) {

        System.out.println(
                "Invalid operation: Item must be delivered before inspection."
        );
    }


    @Override
    public void refundSuccessful() {

        System.out.println(
                "Invalid operation: Refund processing has not started."
        );
    }


    @Override
    public void refundFailed() {

        System.out.println(
                "Invalid operation: Refund processing has not started."
        );
    }
}


// =========================================================
// DELIVERED STATE
// =========================================================
// In this state:
//
// - Cancellation is not allowed.
// - Return reason cannot be modified.
// - Item can be inspected.
//
// If eligible:
//      Delivered -> ProcessingRefund
//
// If not eligible:
//      Delivered -> Rejected

class DeliveredState implements ReturnState {

    private ReturnRequest request;


    public DeliveredState(ReturnRequest request) {
        this.request = request;
    }


    @Override
    public void updateReason(String reason) {

        System.out.println(
                "Invalid operation: Return reason cannot be changed."
        );
    }


    @Override
    public void approve() {

        System.out.println(
                "Invalid operation: Request is already approved."
        );
    }


    @Override
    public void reject() {

        // The item can be rejected after delivery.
        request.setState(new RejectedState(request));

        System.out.println(
                "Return request rejected."
        );
    }


    @Override
    public void cancel() {

        System.out.println(
                "Invalid operation: Delivered item cannot be cancelled."
        );
    }


    @Override
    public void itemDelivered() {

        System.out.println(
                "Invalid operation: Item has already been delivered."
        );
    }


    @Override
    public void inspect(boolean eligible) {

        System.out.println("Inspecting returned item...");

        if (eligible) {

            // Item satisfies the return policy.
            // Start refund processing.
            request.setState(
                    new ProcessingRefundState(request)
            );

            System.out.println(
                    "Item is eligible. Refund processing started."
            );

        } else {

            // Item does not satisfy the return policy.
            // Move to the final Rejected state.
            request.setState(new RejectedState(request));

            System.out.println(
                    "Item is not eligible. Return rejected."
            );
        }
    }


    @Override
    public void refundSuccessful() {

        System.out.println(
                "Invalid operation: Item has not been inspected."
        );
    }


    @Override
    public void refundFailed() {

        System.out.println(
                "Invalid operation: Refund processing has not started."
        );
    }
}


// =========================================================
// PROCESSING REFUND STATE
// =========================================================
// In this state:
//
// - Return reason cannot be modified.
// - Cancellation is not allowed.
// - Refund can succeed or fail.
//
// If refund succeeds:
//      ProcessingRefund -> Refunded
//
// If refund fails:
//      Remain in ProcessingRefund
//
// The refund can be attempted again later.

class ProcessingRefundState implements ReturnState {

    private ReturnRequest request;


    public ProcessingRefundState(ReturnRequest request) {
        this.request = request;
    }


    @Override
    public void updateReason(String reason) {

        System.out.println(
                "Invalid operation: Return reason cannot be changed."
        );
    }


    @Override
    public void approve() {

        System.out.println(
                "Invalid operation: Request is already approved."
        );
    }


    @Override
    public void reject() {

        System.out.println(
                "Invalid operation: Refund processing is already in progress."
        );
    }


    @Override
    public void cancel() {

        System.out.println(
                "Invalid operation: Request cannot be cancelled during refund processing."
        );
    }


    @Override
    public void itemDelivered() {

        System.out.println(
                "Invalid operation: Item has already been delivered."
        );
    }


    @Override
    public void inspect(boolean eligible) {

        System.out.println(
                "Invalid operation: Item has already been inspected."
        );
    }


    @Override
    public void refundSuccessful() {

        // Refund succeeded.
        // Move to the final Refunded state.
        request.setState(new RefundedState(request));

        System.out.println(
                "Refund successful. Request completed."
        );
    }


    @Override
    public void refundFailed() {

        // The refund failed.
        // Remain in the current state.
        // The refund can be attempted again later.
        System.out.println(
                "Refund failed. You may try again later."
        );
    }
}


// =========================================================
// FINAL STATE: REFUNDED
// =========================================================
// No operation can change a refunded request.

class RefundedState implements ReturnState {

    private ReturnRequest request;


    public RefundedState(ReturnRequest request) {
        this.request = request;
    }


    private void invalidOperation() {

        System.out.println(
                "Invalid operation: Request is already refunded."
        );
    }


    @Override
    public void updateReason(String reason) {
        invalidOperation();
    }


    @Override
    public void approve() {
        invalidOperation();
    }


    @Override
    public void reject() {
        invalidOperation();
    }


    @Override
    public void cancel() {
        invalidOperation();
    }


    @Override
    public void itemDelivered() {
        invalidOperation();
    }


    @Override
    public void inspect(boolean eligible) {
        invalidOperation();
    }


    @Override
    public void refundSuccessful() {
        invalidOperation();
    }


    @Override
    public void refundFailed() {
        invalidOperation();
    }
}


// =========================================================
// FINAL STATE: REJECTED
// =========================================================
// No operation can change a rejected request.

class RejectedState implements ReturnState {

    private ReturnRequest request;


    public RejectedState(ReturnRequest request) {
        this.request = request;
    }


    private void invalidOperation() {

        System.out.println(
                "Invalid operation: Request is already rejected."
        );
    }


    @Override
    public void updateReason(String reason) {
        invalidOperation();
    }


    @Override
    public void approve() {
        invalidOperation();
    }


    @Override
    public void reject() {
        invalidOperation();
    }


    @Override
    public void cancel() {
        invalidOperation();
    }


    @Override
    public void itemDelivered() {
        invalidOperation();
    }


    @Override
    public void inspect(boolean eligible) {
        invalidOperation();
    }


    @Override
    public void refundSuccessful() {
        invalidOperation();
    }


    @Override
    public void refundFailed() {
        invalidOperation();
    }
}


// =========================================================
// FINAL STATE: CANCELLED
// =========================================================
// No operation can change a cancelled request.

class CancelledState implements ReturnState {

    private ReturnRequest request;


    public CancelledState(ReturnRequest request) {
        this.request = request;
    }


    private void invalidOperation() {

        System.out.println(
                "Invalid operation: Request is already cancelled."
        );
    }


    @Override
    public void updateReason(String reason) {
        invalidOperation();
    }


    @Override
    public void approve() {
        invalidOperation();
    }


    @Override
    public void reject() {
        invalidOperation();
    }


    @Override
    public void cancel() {
        invalidOperation();
    }


    @Override
    public void itemDelivered() {
        invalidOperation();
    }


    @Override
    public void inspect(boolean eligible) {
        invalidOperation();
    }


    @Override
    public void refundSuccessful() {
        invalidOperation();
    }


    @Override
    public void refundFailed() {
        invalidOperation();
    }
}


// =========================================================
// MAIN CLASS
// =========================================================

public class StateB1 {

    public static void main(String[] args) {

        // -------------------------------------------------
        // Create a new return request.
        // Initial state = Requested
        // -------------------------------------------------

        ReturnRequest request =
                new ReturnRequest("Product is defective");


        // -------------------------------------------------
        // Requested State Operations
        // -------------------------------------------------

        request.updateReason("Product is damaged");

        request.approve();


        // -------------------------------------------------
        // Approved State Operations
        // -------------------------------------------------

        // This operation is invalid because the reason
        // cannot be changed after approval.
        request.updateReason("Changed my mind");

        // Deliver the item.
        request.itemDelivered();


        // -------------------------------------------------
        // Delivered State Operations
        // -------------------------------------------------

        // Cancellation is invalid after delivery.
        request.cancel();

        // Inspect the item.
        // true means the item satisfies the return policy.
        request.inspect(true);


        // -------------------------------------------------
        // Processing Refund State Operations
        // -------------------------------------------------

        // First refund attempt fails.
        request.refundFailed();

        // Try the refund again.
        request.refundSuccessful();


        // -------------------------------------------------
        // Refunded State Operations
        // -------------------------------------------------

        // This operation is invalid because the request
        // has already reached a final state.
        request.cancel();

        request.updateReason("Another reason");
    }
}