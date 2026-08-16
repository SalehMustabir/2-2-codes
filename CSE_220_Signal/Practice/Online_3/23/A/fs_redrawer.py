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


def detect_shape_symmetry(self):
        """
        THEORY OF SHAPE SYMMETRY AND COEFFICIENT SPARSITY:
        
        The complex Fourier Series has elegant mathematical properties when 
        dealing with symmetrical signals[cite: 1228]. If a 2D shape possesses 
        rotational or reflectional symmetry, its Fourier coefficients (c_n) 
        will not be randomly distributed[cite: 1228, 1229]. Instead, many 
        coefficients will naturally evaluate to zero (a property known as 
        sparsity)[cite: 1229].
        
        Specifically, if a shape has k-fold rotational symmetry (for example, 
        a 5-pointed star where k=5), only harmonics that are multiples of k 
        (usually plus or minus 1, depending on the drawing direction) will 
        contain significant energy[cite: 1231]. 
        
        Mathematical Explanation for Cancellation:
        When we calculate a Fourier coefficient, we evaluate the integral of 
        the shape's position multiplied by a rotating complex exponential over 
        one full period T. If the shape has k-fold symmetry, iterating through 
        one period means the shape repeats its geometry k times. For any 
        harmonic n that is NOT related to this symmetry (i.e., not a multiple 
        of k +/- 1), the complex vectors generated during the numerical 
        integration will point in evenly distributed, opposing directions 
        across the complex plane. When summed together, these opposing vectors 
        perfectly cancel out to zero[cite: 1234]. Therefore, those harmonics 
        store almost 0% of the signal's energy.
        
        This function analyzes the coefficients to automatically detect if 
        the shape has k-fold rotational symmetry by checking which harmonics 
        hold the vast majority (>99%) of the signal's energy[cite: 1233].
        """
        
        # Step 1: Calculate the total energy of all harmonics combined.
        # Parseval's theorem states that the total energy in the time domain 
        # equals the sum of the squared magnitudes of the Fourier coefficients.
        total_energy = 0.0
        
        # Loop through every harmonic index 'n' in our coefficients dictionary
        for n in self.coeffs:
            c_n = self.coeffs[n]
            
            # The magnitude of a complex number (a + bj) is sqrt(a^2 + b^2).
            # The energy is the magnitude squared, so we just do (a^2 + b^2).
            # We avoid math.pow or numpy for simplicity.
            real_part = c_n.real
            imag_part = c_n.imag
            energy = (real_part * real_part) + (imag_part * imag_part)
            
            total_energy = total_energy + energy

        # Step 2: Identify which specific harmonics hold the significant energy.
        # We will store the harmonic numbers (n) that contribute a meaningful amount.
        significant_harmonics = []
        
        for n in self.coeffs:
            # We skip n = 0 because the DC component just represents the 
            # center of mass (translation) and doesn't affect shape symmetry.
            if n == 0:
                continue 
                
            c_n = self.coeffs[n]
            real_part = c_n.real
            imag_part = c_n.imag
            energy = (real_part * real_part) + (imag_part * imag_part)
            
            # If this single harmonic holds more than 1% of the total energy,
            # we consider it a "significant" harmonic that drives the shape.
            if energy > (0.01 * total_energy):
                significant_harmonics.append(n)

        # Step 3: Sort the significant harmonics from lowest to highest.
        # Since we cannot use built-in sort functions or lambdas, we use a 
        # basic Bubble Sort algorithm using standard loops.
        num_significant = len(significant_harmonics)
        for i in range(num_significant):
            for j in range(0, num_significant - i - 1):
                if significant_harmonics[j] > significant_harmonics[j + 1]:
                    # Swap the variables if the left one is bigger than the right
                    temp = significant_harmonics[j]
                    significant_harmonics[j] = significant_harmonics[j + 1]
                    significant_harmonics[j + 1] = temp

        # Step 4: Calculate the gaps (differences) between consecutive significant harmonics.
        # If a shape has k-fold symmetry, the dominant harmonics will be evenly spaced by 'k'.
        gaps = []
        for i in range(len(significant_harmonics) - 1):
            # Calculate the distance between the current harmonic and the next one
            gap = significant_harmonics[i + 1] - significant_harmonics[i]
            
            # We only care about positive gaps, so we append the absolute value
            if gap < 0:
                gap = gap * -1
            gaps.append(gap)

        # Step 5: Find the most frequent gap size to determine 'k'
        # We will count how many times each gap appears using a dictionary
        gap_counts = {}
        for gap in gaps:
            # If the gap is already in our dictionary, add 1 to its count
            if gap in gap_counts:
                gap_counts[gap] = gap_counts[gap] + 1
            # If we haven't seen this gap yet, set its count to 1
            else:
                gap_counts[gap] = 1

        # Step 6: Find the gap that appeared the most times (the maximum count)
        estimated_k = 1
        max_count = 0
        
        for gap in gap_counts:
            count = gap_counts[gap]
            if count > max_count:
                max_count = count
                estimated_k = gap

        # Step 7: Output the analytical results
        print("Symmetry Analysis Complete:")
        print("Significant Harmonics Found:", significant_harmonics)
        print("Distance between dominant harmonics:", gaps)
        
        if estimated_k > 1:
            print(f"Result: The shape likely possesses {estimated_k}-fold rotational symmetry!")
        else:
            print("Result: No clear rotational symmetry detected. The shape is likely asymmetric.")
            
        return estimated_k



def morph_shapes(self, coeffs_shape1, coeffs_shape2, alpha):
        """
        THEORY OF SHAPE MORPHING VIA COEFFICIENT INTERPOLATION:
        
        The Fourier Transform is a linear mathematical operation. 
        This means that if you have the Fourier coefficients for two 
        completely different shapes (e.g., a heart and a star), taking 
        a weighted average of their coefficients will yield a brand-new 
        set of coefficients that draws a mathematically perfect intermediate shape.
        
        The interpolated coefficients for any morphing step alpha (where 
        0.0 <= alpha <= 1.0) are calculated as:
        c_n_morph = (1 - alpha) * c_n_shape1 + alpha * c_n_shape2
        
        If alpha is 0.0, the output is 100% shape 1.
        If alpha is 1.0, the output is 100% shape 2.
        If alpha is 0.5, it is a perfect 50/50 structural blend of both shapes.
        """
        
        # Step 1: Create an empty dictionary to hold our new morphed coefficients
        morphed_coeffs = {}
        
        # Step 2: Loop through every harmonic index 'n' in our first shape
        for n in coeffs_shape1:
            
            # Step 3: Ensure the second shape also has this harmonic calculated.
            # (Both shapes must be sampled with the same number of points to align)
            if n in coeffs_shape2:
                
                # Extract the complex coefficient for harmonic 'n' from both shapes
                c_n_1 = coeffs_shape1[n]
                c_n_2 = coeffs_shape2[n]
                
                # Step 4: Apply the weighted average formula to the REAL parts
                # We multiply shape1 by (1 - alpha) and shape2 by alpha
                weight_1 = 1.0 - alpha
                weight_2 = alpha
                
                real_morph = (weight_1 * c_n_1.real) + (weight_2 * c_n_2.real)
                
                # Step 5: Apply the exact same weighted average to the IMAGINARY parts
                imag_morph = (weight_1 * c_n_1.imag) + (weight_2 * c_n_2.imag)
                
                # Step 6: Recombine the new real and imaginary parts into a complex number
                # and store it in our new dictionary
                morphed_coeffs[n] = complex(real_morph, imag_morph)
                
        # Return the new coefficient dictionary ready to be drawn by the epicycles
        return morphed_coeffs


def calculate_velocity_coefficients(self, original_coeffs, total_period_T):
        """
        THEORY OF THE DERIVATIVE THEOREM AND PEN VELOCITY:
        
        According to the Time Derivative property of the Fourier Series, 
        taking the derivative of a signal in the time domain is mathematically 
        equivalent to multiplying its Fourier coefficients by (j * n * omega) 
        in the frequency domain.
        
        In the context of our 2D shape redrawer, the original signal f(t) 
        represents the position (x,y) of the pen. Therefore, its derivative f'(t) 
        represents the velocity vector of the pen as it traces the SVG. 
        
        By multiplying every coefficient by (j * n * omega), we create a brand 
        new Fourier Series. If you pass these new coefficients into your redrawer, 
        the epicycles will draw a graph of the pen's speed! The highest peaks of 
        this new drawing will perfectly correspond to the sharpest physical 
        corners of the original SVG, where the pen had to change direction fastest.
        """
        
        # Step 1: Create an empty dictionary for the velocity coefficients
        velocity_coeffs = {}
        
        # Step 2: Calculate the fundamental angular frequency (omega)
        # Omega = 2 * Pi / T
        pi = 3.141592653589793
        omega = (2.0 * pi) / total_period_T
        
        # Step 3: Loop through every harmonic in the original shape
        for n in original_coeffs:
            
            c_n = original_coeffs[n]
            a = c_n.real
            b = c_n.imag
            
            # Step 4: Multiply the complex number (a + bj) by (0 + (n * omega)j)
            # The math breakdown:
            # (a + bj) * (n * omega * j) 
            # = (a * n * omega * j) + (b * n * omega * j^2)
            # Since j^2 = -1, the equation becomes:
            # = (a * n * omega * j) - (b * n * omega)
            
            # Therefore, the new REAL part becomes: -b * n * omega
            new_real = -1.0 * b * n * omega
            
            # And the new IMAGINARY part becomes: a * n * omega
            new_imag = a * n * omega
            
            # Step 5: Create the new complex number and save it
            velocity_coeffs[n] = complex(new_real, new_imag)
            
        # Return the velocity coefficients. Reconstructing this will yield
        # the velocity vectors instead of the spatial coordinates!
        return velocity_coeffs



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
