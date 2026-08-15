import numpy as np

from svg_utils import load_svg_path
from epicycle_animation import save_outputs


class FourierEpicycles:
    def __init__(self, t, signal, n_harmonics):
        """
        Step 1: Store the sampled signal and set up everything the other
        methods will need.

        Parameters
        ----------
        t : 1D numpy array, shape (M,)
            Uniformly spaced sample times covering ONE FULL PERIOD of the
            signal, as a *closed* interval: t[0] == 0 and t[-1] == T (the
            period). This is exactly what svg_utils.load_svg_path(...)
            returns.
        signal : 1D complex numpy array, shape (M,)
            signal[i] = f(t[i]) = x(t[i]) + 1j * y(t[i]). Periodic, so
            signal[-1] == signal[0].
        n_harmonics : int (call it N)
            The series will use every integer harmonic n with
            -N <= n <= N (i.e. 2N+1 terms in total -- do not forget the
            negative harmonics).

        You must set at least the following attributes, since the rest of
        this class (and the provided plotting/animation code) expects
        them to exist:
            self.t, self.signal, self.N
            self.T      -- the period (a float)
            self.omega  -- the fundamental angular frequency, 2*pi/T
            self.coeffs -- an (initially empty) dict that will map
                           n -> c_n once calculate_all_coefficients() has
                           been called
        """
        # TODO: implement this method
        self.t = t
        self.signal = signal
        self.N = n_harmonics
        self.T = t[-1]  # Assuming t[-1] is the period T
        self.omega = 2 * np.pi / self.T
        self.coeffs = {}

        # raise NotImplementedError("Implement __init__")

    def calculate_cn(self, n):
        """
        Step 2: Compute a single complex Fourier coefficient c_n using
        numerical integration (np.trapezoid) over the stored samples
        self.t, self.signal.

            c_n = (1/T) * integral_0^T  f(t) * exp(-j*n*omega*t)  dt

        n may be zero, positive, or negative.
        """
        # TODO: implement this method
        exp_term = np.exp(-1j * n * self.omega * self.t)
        integrand = self.signal * exp_term
        c_n = (1 / self.T) * np.trapezoid(integrand, self.t)
        return c_n
        #raise NotImplementedError("Implement calculate_cn")

    def calculate_all_coefficients(self):
        """
        Step 3: Populate self.coeffs with c_n for every harmonic
        n = -N, ..., -1, 0, 1, ..., N by repeatedly calling calculate_cn(n).
        """
        # TODO: implement this method
        for n in range(-self.N, self.N + 1):
            self.coeffs[n] = self.calculate_cn(n)
        # raise NotImplementedError("Implement calculate_all_coefficients")

    def approximate(self, t):
        """
        Step 4: Reconstruct (an approximation of) the signal at time(s) t
        from the coefficients already stored in self.coeffs:

            f_hat(t) = sum_{n=-N}^{N} c_n * exp(j*n*omega*t)

        t may be a single number or a numpy array of times -- your
        implementation must support both, since the provided
        plotting/animation code calls this both ways.
        """
        # TODO: implement this method
        t_array = np.asarray(t)  # Ensure t is a numpy array for vectorized operations
        f_hat = np.zeros_like(t_array, dtype=complex)  # Initialize the approximation
        for n, c_n in self.coeffs.items():
            f_hat += c_n * np.exp(1j * n * self.omega * t_array)
        return f_hat
        #raise NotImplementedError("Implement approximate")
    def evaluate_reconstruction_error(self):
        """
        Computes the Mean Squared Error (MSE) between the ground-truth 
        signal samples f(t) and the reconstructed approximation f_hat(t).
        """
        # Reconstruct the signal at all sampled times
        f_hat = self.approximate(self.t)
        
        # Calculate MSE: mean of absolute differences squared
        mse = np.mean(np.abs(self.signal - f_hat)**2)
        return mse
    def prune_harmonics_by_energy(self, r):
        """
        Retains the most energetic harmonics whose cumulative energy is at least 
        a fraction 'r' of the total energy. Sets discarded coefficients to zero.
        """
        
        # ---------------------------------------------------------
        # Step 1: Calculate the energy of each individual harmonic
        # ---------------------------------------------------------
        energies = {}
        for n, c_n in self.coeffs.items():
            # Energy is defined as the absolute value of the coefficient squared
            coefficient_magnitude = np.abs(c_n)
            energy_value = coefficient_magnitude ** 2
            
            # Store the energy mapped to its harmonic number (n)
            energies[n] = energy_value

        # ---------------------------------------------------------
        # Step 2: Calculate the total energy of the original signal
        # ---------------------------------------------------------
        total_energy = 0.0
        for energy_value in energies.values():
            total_energy += energy_value

        # ---------------------------------------------------------
        # Step 3: Sort the harmonics from highest energy to lowest
        # ---------------------------------------------------------
        # We define a simple helper function to tell the 'sorted' 
        # algorithm to sort based on the energy values, not the keys.
        def get_energy(harmonic_number):
            return energies[harmonic_number]
            
        sorted_harmonics = sorted(energies.keys(), key=get_energy, reverse=True)
        
        # ---------------------------------------------------------
        # Step 4: Accumulate energy until the threshold is met
        # ---------------------------------------------------------
        cumulative_energy = 0.0
        retained_harmonics = set()
        
        for n in sorted_harmonics:
            # Add the current largest harmonic's energy to our running total
            cumulative_energy += energies[n]
            retained_harmonics.add(n)
            
            # Check the current ratio against the user's target threshold (r)
            current_ratio = cumulative_energy / total_energy
            if current_ratio >= r:
                break  # Stop the loop the moment we hit or exceed the target

        # ---------------------------------------------------------
        # Step 5: Zero out the coefficients of discarded harmonics
        # ---------------------------------------------------------
        # We grab a static list of the keys to safely iterate through 
        # them while we modify the actual dictionary.
        all_harmonic_numbers = list(self.coeffs.keys())
        
        for n in all_harmonic_numbers:
            if n not in retained_harmonics:
                # Setting this to zero removes the rotating circle from the drawing
                self.coeffs[n] = 0.0
                
        # ---------------------------------------------------------
        # Step 6: Calculate and return the final metrics
        # ---------------------------------------------------------
        actual_energy_ratio = cumulative_energy / total_energy
        number_of_retained_harmonics = len(retained_harmonics)
        
        return number_of_retained_harmonics, actual_energy_ratio

if __name__ == "__main__":
    # import sys
    # from pathlib import Path

    # # Usage: python3 assignment.py <path_to_svg> [n_harmonics] [comparison_png_path] [gif_path]
    # if len(sys.argv) < 2:
    #     print("Usage: python3 assignment.py <path_to_svg> [n_harmonics] [comparison_png_path] [gif_path]")
    #     print("Example: python3 assignment.py svgs/heart.svg 150 heart_comparison.png heart_epicycles.gif")
    #     sys.exit(1)

    # svg_path = sys.argv[1]
    # N_HARMONICS = int(sys.argv[2]) if len(sys.argv) > 2 else 150
    # stem = Path(svg_path).stem
    # comparison_path = sys.argv[3] if len(sys.argv) > 3 else f"{stem}_comparison.png"
    # gif_path = sys.argv[4] if len(sys.argv) > 4 else f"{stem}_epicycles.gif"

    # t, z = load_svg_path(svg_path, num_points=1000)
    # fs = FourierEpicycles(t, z, n_harmonics=N_HARMONICS)
    # fs.calculate_all_coefficients()

    # save_outputs(fs, z, comparison_path, gif_path, num_frames=240)
    import sys
    from pathlib import Path

    # Set up the target SVG and base parameters
    svg_path = "svgs/heart.svg"
    N_HARMONICS = 150
    target_ratios = [0.96, 0.98, 0.99, 1.00]

    # Load the ground-truth signal
    t, z = load_svg_path(svg_path, num_points=1000)

    # Print the table header exactly as requested
    print(f"{'Target Ratio':<14} | {'Harmonics Retained':<18} | {'Actual Energy Ratio':<19} | {'MSE'}")
    print("-" * 75)

    # Loop through each target energy ratio
    for r in target_ratios:
        # Re-initialize and calculate full coefficients for a clean slate each loop
        fs = FourierEpicycles(t, z, n_harmonics=N_HARMONICS)
        fs.calculate_all_coefficients()
        
        # Prune the harmonics and evaluate the error
        retained_count, actual_r = fs.prune_harmonics_by_energy(r)
        mse = fs.evaluate_reconstruction_error()
        
        # Print the formatted row
        print(f"{r:<14.2f} | {retained_count:<18} | {actual_r:<19.4f} | {mse:.6f}")
        
        # Setup file paths dynamically based on 'r'
        comparison_path = f"heart_pruned_{r:.2f}.png"
        gif_path = f"heart_pruned_{r:.2f}.gif" # Assuming save_outputs requires a gif path
        
        # Save the outputs for this specific pruned state
        save_outputs(fs, z, comparison_path, gif_path, num_frames=240)
