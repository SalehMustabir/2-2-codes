
// ============================================================
// E-commerce Return and Refund Workflow
// Design Pattern: STATE PATTERN
// ============================================================

// State interface
// Every state must define how each operation behaves.
interface ReturnState {

    void updateReason(ReturnRequest request, String reason);

    void approve(ReturnRequest request);

    void reject(ReturnRequest request);

    void cancel(ReturnRequest request);

    void itemDelivered(ReturnRequest request);

    void inspect(ReturnRequest request, boolean eligible);

    void refundSuccessful(ReturnRequest request);

    void refundFailed(ReturnRequest request);
}


// ============================================================
// Context Class
// ============================================================

class ReturnRequest {

    // Current state of the return request
    private ReturnState state;

    // Store the return reason
    private String reason;

    public ReturnRequest(String reason) {
        this.reason = reason;

        // Every new request starts in Requested state
        this.state = new RequestedState();
    }

    // Change the current state
    public void setState(ReturnState state) {
        this.state = state;
    }

    // Getter for state
    public ReturnState getState() {
        return state;
    }

    // Getter for reason
    public String getReason() {
        return reason;
    }

    // Setter for reason
    public void setReason(String reason) {
        this.reason = reason;
    }


    // --------------------------------------------------------
    // Operations delegated to the current state
    // --------------------------------------------------------

    public void updateReason(String reason) {
        state.updateReason(this, reason);
    }

    public void approve() {
        state.approve(this);
    }

    public void reject() {
        state.reject(this);
    }

    public void cancel() {
        state.cancel(this);
    }

    public void itemDelivered() {
        state.itemDelivered(this);
    }

    public void inspect(boolean eligible) {
        state.inspect(this, eligible);
    }

    public void refundSuccessful() {
        state.refundSuccessful(this);
    }

    public void refundFailed() {
        state.refundFailed(this);
    }
}


// ============================================================
// 1. REQUESTED STATE
// ============================================================

class RequestedState implements ReturnState {

    @Override
    public void updateReason(ReturnRequest request, String reason) {

        // Reason can be updated multiple times
        request.setReason(reason);

        System.out.println("Return reason updated to: " + reason);
    }

    @Override
    public void approve(ReturnRequest request) {

        // Requested -> Approved
        request.setState(new ApprovedState());

        System.out.println("Return request approved.");
    }

    @Override
    public void reject(ReturnRequest request) {

        // Requested -> Rejected
        request.setState(new RejectedState());

        System.out.println("Return request rejected.");
    }

    @Override
    public void cancel(ReturnRequest request) {

        // Requested -> Cancelled
        request.setState(new CancelledState());

        System.out.println("Return request cancelled.");
    }

    @Override
    public void itemDelivered(ReturnRequest request) {

        // Item cannot be delivered before approval
        System.out.println(
            "Invalid operation: Item cannot be marked delivered before approval."
        );
    }

    @Override
    public void inspect(ReturnRequest request, boolean eligible) {

        System.out.println(
            "Invalid operation: Item cannot be inspected before delivery."
        );
    }

    @Override
    public void refundSuccessful(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Refund is not being processed."
        );
    }

    @Override
    public void refundFailed(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Refund is not being processed."
        );
    }
}


// ============================================================
// 2. APPROVED STATE
// ============================================================

class ApprovedState implements ReturnState {

    @Override
    public void updateReason(ReturnRequest request, String reason) {

        // Reason cannot be changed after approval
        System.out.println(
            "Invalid operation: Return reason cannot be modified after approval."
        );
    }

    @Override
    public void approve(ReturnRequest request) {

        System.out.println("Invalid operation: Request is already approved.");
    }

    @Override
    public void reject(ReturnRequest request) {

        // Requirements do not allow rejection from Approved state
        System.out.println(
            "Invalid operation: Approved request cannot be rejected."
        );
    }

    @Override
    public void cancel(ReturnRequest request) {

        // Approved -> Cancelled
        // Cancellation is allowed before item delivery.
        request.setState(new CancelledState());

        System.out.println("Approved return request cancelled.");
    }

    @Override
    public void itemDelivered(ReturnRequest request) {

        // Approved -> Delivered
        request.setState(new DeliveredState());

        System.out.println("Returned item marked as delivered.");
    }

    @Override
    public void inspect(ReturnRequest request, boolean eligible) {

        System.out.println(
            "Invalid operation: Item must be delivered before inspection."
        );
    }

    @Override
    public void refundSuccessful(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Refund processing has not started."
        );
    }

    @Override
    public void refundFailed(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Refund processing has not started."
        );
    }
}


// ============================================================
// 3. DELIVERED STATE
// ============================================================

class DeliveredState implements ReturnState {

    @Override
    public void updateReason(ReturnRequest request, String reason) {

        System.out.println(
            "Invalid operation: Return reason cannot be modified."
        );
    }

    @Override
    public void approve(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Request has already been approved."
        );
    }

    @Override
    public void reject(ReturnRequest request) {

        // Delivered -> Rejected
        request.setState(new RejectedState());

        System.out.println("Return request rejected.");
    }

    @Override
    public void cancel(ReturnRequest request) {

        // Cannot cancel after delivery
        System.out.println(
            "Invalid operation: Delivered request cannot be cancelled."
        );
    }

    @Override
    public void itemDelivered(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Item is already delivered."
        );
    }

    @Override
    public void inspect(ReturnRequest request, boolean eligible) {

        // Inspection determines what happens next.
        if (eligible) {

            // Eligible -> Processing Refund
            request.setState(new ProcessingRefundState());

            System.out.println(
                "Inspection successful. Return is eligible."
            );
            System.out.println(
                "Refund processing started."
            );

        } else {

            // Not eligible -> Rejected
            request.setState(new RejectedState());

            System.out.println(
                "Inspection failed. Return is not eligible."
            );
            System.out.println(
                "Return request rejected."
            );
        }
    }

    @Override
    public void refundSuccessful(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Refund processing has not started."
        );
    }

    @Override
    public void refundFailed(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Refund processing has not started."
        );
    }
}


// ============================================================
// 4. PROCESSING REFUND STATE
// ============================================================

class ProcessingRefundState implements ReturnState {

    @Override
    public void updateReason(ReturnRequest request, String reason) {

        System.out.println(
            "Invalid operation: Return reason cannot be modified."
        );
    }

    @Override
    public void approve(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Request is already approved."
        );
    }

    @Override
    public void reject(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Refund is already being processed."
        );
    }

    @Override
    public void cancel(ReturnRequest request) {

        // Cannot cancel while refund is processing
        System.out.println(
            "Invalid operation: Request cannot be cancelled during refund processing."
        );
    }

    @Override
    public void itemDelivered(ReturnRequest request) {

        System.out.println(
            "Invalid operation: Item has already been delivered."
        );
    }

    @Override
    public void inspect(ReturnRequest request, boolean eligible) {

        System.out.println(
            "Invalid operation: Inspection has already been completed."
        );
    }

    @Override
    public void refundSuccessful(ReturnRequest request) {

        // Processing Refund -> Refunded
        request.setState(new RefundedState());

        System.out.println("Refund successful.");
        System.out.println("Return request is now refunded.");
    }

    @Override
    public void refundFailed(ReturnRequest request) {

        // Stay in ProcessingRefundState.
        // Therefore, refund can be attempted again later.
        System.out.println(
            "Refund failed. It can be attempted again later."
        );
    }
}


// ============================================================
// 5. REFUNDED STATE
// ============================================================

class RefundedState implements ReturnState {

    @Override
    public void updateReason(ReturnRequest request, String reason) {
        System.out.println("Invalid operation: Request is already finalized.");
    }

    @Override
    public void approve(ReturnRequest request) {
        System.out.println("Invalid operation: Request is already finalized.");
    }

    @Override
    public void reject(ReturnRequest request) {
        System.out.println("Invalid operation: Request is already finalized.");
    }

    @Override
    public void cancel(ReturnRequest request) {
        System.out.println("Invalid operation: Request is already finalized.");
    }

    @Override
    public void itemDelivered(ReturnRequest request) {
        System.out.println("Invalid operation: Request is already finalized.");
    }

    @Override
    public void inspect(ReturnRequest request, boolean eligible) {
        System.out.println("Invalid operation: Request is already finalized.");
    }

    @Override
    public void refundSuccessful(ReturnRequest request) {
        System.out.println("Invalid operation: Refund is already completed.");
    }

    @Override
    public void refundFailed(ReturnRequest request) {
        System.out.println("Invalid operation: Refund is already completed.");
    }
}


// ============================================================
// 6. REJECTED STATE
// ============================================================

class RejectedState implements ReturnState {

    // Rejected is a final state.
    // Therefore, no operation can change the request.

    @Override
    public void updateReason(ReturnRequest request, String reason) {
        System.out.println("Invalid operation: Request is rejected.");
    }

    @Override
    public void approve(ReturnRequest request) {
        System.out.println("Invalid operation: Request is rejected.");
    }

    @Override
    public void reject(ReturnRequest request) {
        System.out.println("Invalid operation: Request is already rejected.");
    }

    @Override
    public void cancel(ReturnRequest request) {
        System.out.println("Invalid operation: Request is rejected.");
    }

    @Override
    public void itemDelivered(ReturnRequest request) {
        System.out.println("Invalid operation: Request is rejected.");
    }

    @Override
    public void inspect(ReturnRequest request, boolean eligible) {
        System.out.println("Invalid operation: Request is rejected.");
    }

    @Override
    public void refundSuccessful(ReturnRequest request) {
        System.out.println("Invalid operation: Request is rejected.");
    }

    @Override
    public void refundFailed(ReturnRequest request) {
        System.out.println("Invalid operation: Request is rejected.");
    }
}


// ============================================================
// 7. CANCELLED STATE
// ============================================================

class CancelledState implements ReturnState {

    // Cancelled is a final state.
    // Therefore, no operation can change the request.

    @Override
    public void updateReason(ReturnRequest request, String reason) {
        System.out.println("Invalid operation: Request is cancelled.");
    }

    @Override
    public void approve(ReturnRequest request) {
        System.out.println("Invalid operation: Request is cancelled.");
    }

    @Override
    public void reject(ReturnRequest request) {
        System.out.println("Invalid operation: Request is cancelled.");
    }

    @Override
    public void cancel(ReturnRequest request) {
        System.out.println("Invalid operation: Request is already cancelled.");
    }

    @Override
    public void itemDelivered(ReturnRequest request) {
        System.out.println("Invalid operation: Request is cancelled.");
    }

    @Override
    public void inspect(ReturnRequest request, boolean eligible) {
        System.out.println("Invalid operation: Request is cancelled.");
    }

    @Override
    public void refundSuccessful(ReturnRequest request) {
        System.out.println("Invalid operation: Request is cancelled.");
    }

    @Override
    public void refundFailed(ReturnRequest request) {
        System.out.println("Invalid operation: Request is cancelled.");
    }
}


// ============================================================
// MAIN CLASS
// ============================================================

public class StateB1 {

    public static void main(String[] args) {

        // Create a new return request.
        // Initial state = Requested
        ReturnRequest request =
            new ReturnRequest("Product is defective");

        System.out.println("----- REQUESTED -----");

        // Reason can be changed in Requested state
        request.updateReason("Product is damaged");

        // Approve the request
        request.approve();


        System.out.println("\n----- APPROVED -----");

        // Reason cannot be changed after approval
        request.updateReason("Wrong size");

        // Deliver the returned item
        request.itemDelivered();


        System.out.println("\n----- DELIVERED -----");

        // Inspect the item.
        // true means item satisfies return policy.
        request.inspect(true);


        System.out.println("\n----- PROCESSING REFUND -----");

        // First refund attempt fails.
        // State remains ProcessingRefund.
        request.refundFailed();

        // Try refund again.
        request.refundSuccessful();


        System.out.println("\n----- REFUNDED -----");

        // Refunded is final.
        request.cancel();
        request.updateReason("Another reason");
    }
}

