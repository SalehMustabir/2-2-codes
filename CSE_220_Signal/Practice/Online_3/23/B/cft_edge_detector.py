import numpy as np
import matplotlib.pyplot as plt
from imageio.v2 import imread
import sys


# =====================================================================
# Given classes — paste your Task 2 implementations where indicated
# =====================================================================

class ContinuousImage:
    """Represents a grayscale image as a continuous 2D spatial signal. (Given)"""

    def __init__(self, image_path):
        self.image = imread(image_path, mode='L').astype(float)
        self.image = self.image / np.max(self.image)
        self.x = np.linspace(-1, 1, self.image.shape[1])
        self.y = np.linspace(-1, 1, self.image.shape[0])


class CFT2D:
    """2D Continuous Fourier Transform. (Given — paste your Task 2 solution)"""

    def __init__(self, image_obj: ContinuousImage):
        self.I = image_obj.image
        self.x = image_obj.x
        self.y = image_obj.y
        dx = self.x[1] - self.x[0]
        dy = self.y[1] - self.y[0]
        self.u = np.linspace(-1 / (2 * dx), 1 / (2 * dx), self.I.shape[1])
        self.v = np.linspace(-1 / (2 * dy), 1 / (2 * dy), self.I.shape[0])

    def compute_cft(self):
        rows, cols = self.I.shape
        C_x = np.zeros((rows, cols))
        S_x = np.zeros((rows, cols))
        for j in range(cols):
            u_values = self.u[j]
            angle_ux = 2 * np.pi * u_values * self.x
            cos_ux = np.cos(angle_ux)
            sin_ux = np.sin(angle_ux)

            C_x[:, j] = np.trapezoid(self.I * cos_ux, self.x, axis=1)
            S_x[:, j] = np.trapezoid(self.I * sin_ux, self.x, axis=1)

        real = np.zeros((rows, cols))
        imag = np.zeros((rows, cols))

        for k in range(rows):
            v_values = self.v[k]
            angle_vy = 2 * np.pi * v_values * self.y
            cos_vy = np.cos(angle_vy)[:, np.newaxis]
            sin_vy = np.sin(angle_vy)[:, np.newaxis]

            int_C_cos = np.trapezoid(C_x * cos_vy, self.y, axis=0)
            int_S_sin = np.trapezoid(S_x * sin_vy, self.y, axis=0)
            int_C_sin = np.trapezoid(C_x * sin_vy, self.y, axis=0)
            int_S_cos = np.trapezoid(S_x * cos_vy, self.y, axis=0)

            real[k, :] = int_C_cos - int_S_sin
            imag[k, :] = - (int_C_sin + int_S_cos)

        self.real = real
        self.imag = imag
        return real, imag
        #raise NotImplementedError("Implement CFT2D.compute_cft")
        raise NotImplementedError("Paste your Task 2 compute_cft here.")

    def plot_magnitude(self):
        magnitude = np.sqrt(self.real**2 + self.imag**2)
        log_magnitude = np.log(1 + magnitude)
        plt.imshow(log_magnitude, cmap='gray')
        plt.title("Log-scaled CFT Magnititude")
        plt.axis('off')
        plt.show()


class InverseCFT2D:
    """Inverse 2D-CFT. (Given — paste your Task 2 solution)"""

    def __init__(self, real, imag, u, v, x, y):
        self.real = real
        self.imag = imag
        self.u = u
        self.v = v
        self.x = x
        self.y = y

    def reconstruct(self):
        rows, cols = self.real.shape
        C_v = np.zeros((rows, cols))
        S_v = np.zeros((rows, cols))

        for i in range(rows):
            y_val = self.y[i]
            angle_vy = 2 * np.pi * self.v * y_val
            cos_vy = np.cos(angle_vy)[:, np.newaxis]
            sin_vy = np.sin(angle_vy)[:, np.newaxis]

            term1 = self.real * cos_vy - self.imag * sin_vy
            term2 = self.real * sin_vy + self.imag * cos_vy

            C_v[i, :] = np.trapezoid(term1, self.v, axis=0)
            S_v[i, :] = np.trapezoid(term2, self.v, axis=0)

        image = np.zeros((rows, cols))


        for j in range(cols):
            x_val = self.x[j]

            angle_ux = 2 * np.pi * self.u * x_val
            cos_ux = np.cos(angle_ux)[np.newaxis, :]
            sin_ux = np.sin(angle_ux)[np.newaxis, :]

            integrand = C_v * cos_ux - S_v * sin_ux

            image[:, j] = np.trapezoid(integrand, self.u, axis=1)
        return image


# =====================================================================
# Task 1 — band_pass and band_stop filters
# =====================================================================

class FrequencyFilter:

    def high_pass(self, real, imag, cutoff):
        """Given high-pass filter."""
        rows, cols = real.shape
        cx, cy = rows // 2, cols // 2
        real = real.copy()
        imag = imag.copy()
        for i in range(rows):
            for j in range(cols):
                if np.sqrt((i - cx) ** 2 + (j - cy) ** 2) <= cutoff:
                    real[i, j] = 0
                    imag[i, j] = 0
        return real, imag

    def band_pass(self, real, imag, r_low, r_high):
        """Retain entries with r_low < d(i,j) <= r_high, zero the rest."""
        # Get the dimensions of the frequency spectrum
        rows, cols = real.shape
        
        # Calculate the exact center coordinates (cx, cy)
        # This center represents the zero-frequency (DC) component
        cx, cy = rows // 2, cols // 2
        
        # Create copies of the original arrays so we don't modify them directly
        real_bp = real.copy()
        imag_bp = imag.copy()
        
        # Iterate over every single row (i) and column (j) in the spectrum
        for i in range(rows):
            for j in range(cols):
                # Calculate the Euclidean distance of the current pixel (i, j) from the center (cx, cy)
                # Formula: d(i,j) = sqrt((i - c_i)^2 + (j - c_j)^2)
                dist = np.sqrt((i - cx) ** 2 + (j - cy) ** 2)
                
                # A band-pass filter ONLY keeps frequencies inside the band (r_low to r_high).
                # If the distance is NOT strictly greater than r_low OR NOT less than/equal to r_high...
                if not (r_low < dist <= r_high):
                    # ...we zero out the real and imaginary components (blocking those frequencies)
                    real_bp[i, j] = 0
                    imag_bp[i, j] = 0
                    
        return real_bp, imag_bp

    def band_stop(self, real, imag, r_low, r_high):
        """Zero entries with r_low < d(i,j) <= r_high, retain the rest."""
        # Get dimensions and center coordinates
        rows, cols = real.shape
        cx, cy = rows // 2, cols // 2
        
        # Create copies of the arrays
        real_bs = real.copy()
        imag_bs = imag.copy()
        
        # Iterate over every single pixel
        for i in range(rows):
            for j in range(cols):
                # Calculate distance from the center
                dist = np.sqrt((i - cx) ** 2 + (j - cy) ** 2)
                
                # A band-stop filter REMOVES frequencies inside the band.
                # If the distance IS inside our target band (r_low to r_high)...
                if (r_low < dist <= r_high):
                    # ...we zero it out (stopping those specific frequencies)
                    real_bs[i, j] = 0
                    imag_bs[i, j] = 0
                    
        return real_bs, imag_bs

    def shift_brightness(self, real, imag, shift_amount):
        """Task 3. Add shift_amount to the real component of the exact center pixel."""
        # Find the center coordinates of the spectrum
        rows, cols = real.shape
        cx, cy = rows // 2, cols // 2
        
        # Create copies to safely modify
        real_shifted = real.copy()
        imag_shifted = imag.copy()
        
        # The center pixel [cx, cy] is the "DC Component" - it dictates global brightness.
        # We strictly add the shift_amount to the real part of this single center pixel.
        # All other frequencies remain completely untouched.
        real_shifted[cx, cy] += shift_amount
        
        return real_shifted, imag_shifted


# =====================================================================
# Task 2 — complementarity check on raw spatial reconstructions
# =====================================================================

class ReconstructionValidator:

    def verify_complementarity(self, I_recon, I_bp, I_bs):
        """Verify the complementarity property. Return (is_valid, delta)."""
        # Get the dimensions of the reconstructed spatial image
        rows, cols = I_recon.shape
        
        # Initialize our maximum difference tracker to 0
        max_delta = 0.0
        
        # Loop through every spatial pixel in the image
        for i in range(rows):
            for j in range(cols):
                # Add the band-pass pixel and band-stop pixel together
                summed_pixel = I_bp[i, j] + I_bs[i, j]
                
                # Find the absolute difference between the summed filters and the original reconstruction
                diff = abs(summed_pixel - I_recon[i, j])
                
                # If this difference is the largest one we've seen so far, update max_delta
                if diff > max_delta:
                    max_delta = diff
                    
        # The specification requires the difference to be exceptionally small (less than 10^-9)
        # to prove the two filters are mathematically perfect opposites.
        is_valid = max_delta < 1e-9
        
        # Return the boolean validation result and the computed maximum difference
        return is_valid, max_delta


def verify_parsevals(self, I_recon, real, imag):
        """
        ===================================================================
        THEORY: Parseval's Energy Theorem
        ===================================================================
        Parseval's Theorem states that the total energy of a signal is conserved 
        whether it is measured in the spatial domain (pixels) or the frequency 
        domain (waves). 
        
        Mathematically:
        Sum( |I(x,y)|^2 ) ≈ (1 / N) * Sum( |F(u,v)|^2 )
        
        Where:
        - I(x,y) is the spatial pixel intensity.
        - F(u,v) is the complex frequency component.
        - |F(u,v)|^2 is the squared magnitude, calculated as (real^2 + imag^2).
        - N is the total number of pixels (Height x Width). 
        
        Why divide by N? 
        When computing a discrete Fourier transform, the formulas simply sum up 
        the wave amplitudes across the image. Without scaling, the raw frequency 
        energy would be N times larger than the spatial energy. Dividing by N 
        normalizes it so the two domains match perfectly.
        ===================================================================
        """
        # Extract the dimensions of the spatial image
        rows, cols = I_recon.shape
        
        # N represents the total number of pixels in the 2D grid
        N = rows * cols 
        
        # Initialize our energy accumulators (starting at 0.0)
        spatial_energy = 0.0
        frequency_energy = 0.0
        
        # -----------------------------------------------------------------
        # STEP 1: Compute Total Spatial Energy
        # -----------------------------------------------------------------
        # We loop through every single (x, y) coordinate in the reconstructed image
        for i in range(rows):
            for j in range(cols):
                # The energy of a single pixel is its absolute intensity squared.
                # We use abs() just in case the reconstruction left tiny negative artifacts.
                pixel_energy = abs(I_recon[i, j]) ** 2
                
                # Add this pixel's energy to our running total
                spatial_energy += pixel_energy
                
        # -----------------------------------------------------------------
        # STEP 2: Compute Total Frequency Energy
        # -----------------------------------------------------------------
        # We loop through every single (u, v) coordinate in the frequency spectrum
        for i in range(rows):
            for j in range(cols):
                # The energy of a frequency component is its magnitude squared.
                # Since F = real + j(imag), magnitude^2 = real^2 + imag^2
                magnitude_squared = (real[i, j] ** 2) + (imag[i, j] ** 2)
                
                # Add this wave's energy to our running total
                frequency_energy += magnitude_squared
                
        # -----------------------------------------------------------------
        # STEP 3: Scale the Frequency Energy
        # -----------------------------------------------------------------
        # Apply the (1 / N) scaling factor required by Parseval's theorem for discrete data
        scaled_frequency_energy = frequency_energy / N
        
        # -----------------------------------------------------------------
        # STEP 4: Calculate the Error (Delta)
        # -----------------------------------------------------------------
        # Find the absolute difference between the energies calculated in both domains
        delta_energy = abs(spatial_energy - scaled_frequency_energy)
        
        # -----------------------------------------------------------------
        # STEP 5: Validate 
        # -----------------------------------------------------------------
        # Check if the difference is infinitesimally small (accounting for 
        # standard floating-point arithmetic errors in Python).
        # We use 10^-5 (1e-5) as a safe threshold for equivalence.
        is_valid = delta_energy < 1e-5
        
        return is_valid, delta_energy


def translate_image(self, real, imag, u, v, dx, dy):
        """
        ===================================================================
        THEORY: Fourier Shift Theorem (Spatial Translation)
        ===================================================================
        In Fourier theory, the *magnitude* of a wave tells you how strong 
        that frequency is, while the *phase* tells you where that wave is 
        located in space. 
        
        The Fourier Shift Theorem states that shifting an image in the 
        spatial domain by (dx, dy) is exactly equivalent to multiplying 
        its frequency spectrum by a complex exponential:
        
        F_shifted(u,v) = F(u,v) * e^(-j * 2π * (u*dx + v*dy))
        
        To code this, we use Euler's formula to break the exponential down 
        into real (cosine) and imaginary (sine) parts:
        e^(jθ) = cos(θ) + j*sin(θ)
        
        Let Phase Angle θ = -2π * (u*dx + v*dy).
        Then we are multiplying two complex numbers for every pixel:
        Original Spectrum: (a + jb)   <-- real + j*imag
        Shift Factor:      (c + jd)   <-- cos(θ) + j*sin(θ)
        
        The result of (a + jb) * (c + jd) is:
        New Real = (a*c - b*d)
        New Imag = (a*d + b*c)
        ===================================================================
        """
        # Extract dimensions of the spectrum
        rows, cols = real.shape
        
        # Create copies of the arrays so we don't overwrite the original data
        real_shifted = real.copy()
        imag_shifted = imag.copy()
        
        # -----------------------------------------------------------------
        # Apply the Phase Shift Pixel-by-Pixel
        # -----------------------------------------------------------------
        # Iterate over every row (i) and column (j)
        for i in range(rows):
            for j in range(cols):
                
                # 1. Fetch the exact frequency coordinates for this pixel.
                # j corresponds to the horizontal axis (u)
                # i corresponds to the vertical axis (v)
                freq_u = u[j] 
                freq_v = v[i]
                
                # 2. Calculate the phase angle (theta) based on the shift theorem formula.
                # theta = -2π * (u * dx + v * dy)
                theta = -2.0 * np.pi * ((freq_u * dx) + (freq_v * dy))
                
                # 3. Calculate the shift factor using Euler's formula (cos + j*sin).
                # This acts as our (c + jd) multiplier.
                c = np.cos(theta)
                d = np.sin(theta)
                
                # 4. Fetch the original complex components for this pixel.
                # This is our (a + jb).
                a = real[i, j]
                b = imag[i, j]
                
                # 5. Perform the complex multiplication.
                # (a + jb) * (c + jd) = (ac - bd) + j(ad + bc)
                
                # Calculate and store the new real component (ac - bd)
                real_shifted[i, j] = (a * c) - (b * d)
                
                # Calculate and store the new imaginary component (ad + bc)
                imag_shifted[i, j] = (a * d) + (b * c)
                
        # Return the newly translated frequency spectrum
        return real_shifted, imag_shifted
def verify_symmetry(self, real, imag):
        """
        ===================================================================
        THEORY: Hermitian (Conjugate) Symmetry
        ===================================================================
        When you take the Fourier Transform of an image that contains strictly 
        real values (like a standard grayscale photograph with no imaginary 
        components), the resulting frequency spectrum has a special mathematical 
        property called Hermitian Symmetry.
        
        This means that if you pick any frequency component at some distance 
        from the center, its exact opposite component on the other side of the 
        center will be its "complex conjugate". 
        
        Mathematically:
        F(c_i + Δi, c_j + Δj) = F*(c_i - Δi, c_j - Δj)
        
        If a complex number is (a + jb), its complex conjugate is (a - jb). 
        Therefore, across the center of the spectrum:
        1. The REAL components should perfectly mirror each other (symmetric).
        2. The IMAGINARY components should be perfectly inverted (anti-symmetric).
        ===================================================================
        """
        # Get the dimensions of the frequency spectrum
        rows, cols = real.shape
        
        # Calculate the exact center coordinates (cx, cy)
        cx, cy = rows // 2, cols // 2
        
        # Initialize our maximum error tracker to 0
        max_error = 0.0
        
        # -----------------------------------------------------------------
        # Iterate and Verify Mirrored Pairs
        # -----------------------------------------------------------------
        # Loop through every single pixel in the frequency spectrum
        for i in range(rows):
            for j in range(cols):
                
                # 1. Calculate the offset (Δi, Δj) from the center
                di = i - cx
                dj = j - cy
                
                # 2. Calculate the coordinates of the mirrored pixel 
                # on the exact opposite side of the center
                mirror_i = cx - di
                mirror_j = cy - dj
                
                # 3. Ensure the mirrored coordinates actually exist within the array bounds.
                # (For arrays with even dimensions, the furthest negative Nyquist 
                # frequency doesn't have a positive counterpart to check against).
                if (0 <= mirror_i < rows) and (0 <= mirror_j < cols):
                    
                    # 4. Check the REAL components (they should be identical)
                    # We subtract them; the ideal difference is 0.
                    err_real = abs(real[i, j] - real[mirror_i, mirror_j])
                    
                    # 5. Check the IMAGINARY components (they should be inverted)
                    # Since one should be negative of the other, adding them 
                    # together should ideally equal 0.
                    err_imag = abs(imag[i, j] + imag[mirror_i, mirror_j])
                    
                    # Find the highest error between the real and imaginary checks
                    current_error = max(err_real, err_imag)
                    
                    # If this is the largest error we've seen so far, update max_error
                    if current_error > max_error:
                        max_error = current_error
                        
        # -----------------------------------------------------------------
        # Validate Against Threshold
        # -----------------------------------------------------------------
        # If the image was originally purely real, the maximum error should be 
        # incredibly small (just standard floating-point rounding dust).
        is_valid = max_error < 1e-9
        
        return is_valid, max_error
def directional_filter(self, real, imag, theta_center, angular_width):
        """
        ===================================================================
        THEORY: Directional Edge Detection
        ===================================================================
        In the spatial domain, an edge is a sharp change in brightness. 
        In the frequency domain, that sharp change creates high-frequency 
        waves that spread out perpendicular to the edge. 
        
        For example:
        - Vertical edges in an image create frequencies along the horizontal 
          axis of the spectrum.
        - Horizontal edges create frequencies along the vertical axis.
        
        By retaining only a "wedge" of frequencies at a specific angle (theta) 
        and blocking everything else, we can isolate edges pointing in a 
        specific direction!
        
        Because of Hermitian (conjugate) symmetry, every frequency component 
        on one side of the center has a mirrored counterpart 180 degrees 
        (π radians) on the other side. Therefore, if we keep a wedge at 
        theta, we MUST also keep the wedge at (theta + π).
        ===================================================================
        """
        import math
        
        # Extract dimensions and find the exact center (cx, cy)
        rows, cols = real.shape
        cx, cy = rows // 2, cols // 2
        
        # Create copies of the arrays so we don't modify the original spectrum
        real_wedge = real.copy()
        imag_wedge = imag.copy()
        
        half_width = angular_width / 2.0
        
        # -----------------------------------------------------------------
        # Iterate over every pixel and check its angle
        # -----------------------------------------------------------------
        for i in range(rows):
            for j in range(cols):
                
                # 1. Calculate the angle of the current pixel relative to the center.
                # math.atan2(y, x) returns an angle between -π and π.
                # Note: 'i' represents the vertical distance (y), 'j' represents horizontal (x).
                theta = math.atan2(i - cx, j - cy)
                
                # Normalize the angle to strictly be between 0 and 2π for easier math
                if theta < 0:
                    theta += 2 * math.pi
                    
                # 2. Normalize our target angles to [0, 2π]
                target1 = theta_center % (2 * math.pi)
                target2 = (theta_center + math.pi) % (2 * math.pi)
                
                # 3. Calculate the shortest angular distance to our primary wedge (target1)
                # We use min() to account for the circle wrapping around at 2π.
                diff1 = abs(theta - target1)
                dist1 = min(diff1, 2 * math.pi - diff1)
                
                # 4. Calculate the shortest angular distance to our symmetric wedge (target2)
                diff2 = abs(theta - target2)
                dist2 = min(diff2, 2 * math.pi - diff2)
                
                # 5. Filter the pixel
                # If the pixel is strictly INSIDE either the primary wedge OR the symmetric wedge,
                # we keep it. Otherwise, we zero it out to block that directional frequency.
                
                if (dist1 <= half_width) or (dist2 <= half_width):
                    pass # Retain the pixel (do nothing)
                else:
                    # Block the pixel
                    real_wedge[i, j] = 0
                    imag_wedge[i, j] = 0
                    
        return real_wedge, imag_wedge

def verify_orthogonality(self, I_bp, I_bs):
        """
        ===================================================================
        THEORY: Spatial Orthogonality of Disjoint Spectra
        ===================================================================
        Because the Band-Pass filter and the Band-Stop filter use strictly 
        opposite masks, they share absolutely zero frequencies. 
        
        A core theorem of Fourier analysis dictates that signals with 
        non-overlapping (disjoint) frequency spectra are orthogonal in the 
        spatial domain. 
        
        Mathematically, their spatial dot product must be zero:
        Sum( I_bp(x,y) * I_bs(x,y) ) ≈ 0
        ===================================================================
        """
        # Get the dimensions of the images
        rows, cols = I_bp.shape
        
        # Initialize the dot product accumulator
        dot_product = 0.0
        
        # -----------------------------------------------------------------
        # Compute the Spatial Dot Product
        # -----------------------------------------------------------------
        # Loop through every single pixel in the spatial domain
        for i in range(rows):
            for j in range(cols):
                # Multiply the pixel from the band-pass image with the 
                # corresponding pixel from the band-stop image, and add it
                # to our running total.
                dot_product += I_bp[i, j] * I_bs[i, j]
                
        # -----------------------------------------------------------------
        # Validate Against Threshold
        # -----------------------------------------------------------------
        # Because of minor floating point rounding errors during the inverse 
        # Fourier transform, the result might be 0.000000001 instead of a 
        # perfect 0. We use a threshold of 10^-5.
        delta = abs(dot_product)
        is_valid = delta < 1e-5
        
        return is_valid, delta


# =====================================================================
# Entry point (given — do not modify)
# =====================================================================
if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python3 cft_edge_detector.py <input_image>")
        sys.exit(1)

    input_path = sys.argv[1]
    r_low, r_high = 10, 50

    img   = ContinuousImage(input_path)
    cft2d = CFT2D(img)
    real, imag = cft2d.compute_cft()

    filt = FrequencyFilter()
    real_bp, imag_bp = filt.band_pass(real, imag, r_low, r_high)
    real_bs, imag_bs = filt.band_stop(real, imag, r_low, r_high)

    def reconstruct(r, im):
        return InverseCFT2D(r, im, cft2d.u, cft2d.v, img.x, img.y).reconstruct()

    I_recon = reconstruct(real,    imag)
    I_bp    = reconstruct(real_bp, imag_bp)
    I_bs    = reconstruct(real_bs, imag_bs)

    validator = ReconstructionValidator()
    is_valid, delta = validator.verify_complementarity(I_recon, I_bp, I_bs)
    print(f"Complementarity check: {is_valid} | max delta: {delta:.2e}")

    def save_edge_map(I_raw, path):
        edge_map = np.abs(I_raw)
        if edge_map.max() > 0:
            edge_map = edge_map / edge_map.max()
        plt.imsave(path, 1 - edge_map, cmap='gray')
        print(f"Saved {path}")

    save_edge_map(I_bp, "pikachu_bandpass.png")
    save_edge_map(I_bs, "pikachu_bandstop.png")

    # Task 3 execution
    real_shifted, imag_shifted = filt.shift_brightness(real, imag, shift_amount=2.0)
    I_brightened = reconstruct(real_shifted, imag_shifted)
    
    # Save brightened image (clip to [0,1], no edge-map inversion)
    I_brightened_clipped = np.clip(I_brightened, 0, 1)
    plt.imsave("pikachu_brightened.png", I_brightened_clipped, cmap='gray')
    print("Saved pikachu_brightened.png")
