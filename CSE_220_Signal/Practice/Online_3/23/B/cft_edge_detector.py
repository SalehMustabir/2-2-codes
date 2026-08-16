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
