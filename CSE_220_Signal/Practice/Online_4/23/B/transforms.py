"""
transforms.py  --  YOUR CODE GOES HERE.

The shared transform core used by BOTH tasks. Write it once; bigmul.py
(Task A) and image_conv.py (Task B) import it.

Nothing in this file may call numpy.fft, scipy.fft, numpy.convolve,
scipy.signal, or any other library routine that performs a Fourier
transform, a convolution or a correlation for you. NumPy is for array
arithmetic only.

A quick self-test you should run before touching either application:

    import numpy as np
    from transforms import DFTAnalyzer, FFTTransformer
    x = np.random.randn(64) + 1j * np.random.randn(64)
    d, f = DFTAnalyzer(), FFTTransformer()
    assert np.max(np.abs(d.transform(x) - f.transform(x))) < 1e-9
    assert np.max(np.abs(d.inverse(d.transform(x)) - x)) < 1e-9
"""

import numpy as np


def next_power_of_two(n):
    """
    Return the smallest power of two that is >= ``n`` (and at least 1).

    Both tasks need this to choose a transform length for the radix-2 FFT.
    """
    # TODO: implement this function
    if n <= 1:
        return 1
    power = 1
    while power < n:
        power *= 2
    return power
    #raise NotImplementedError("Implement next_power_of_two")


class DFTAnalyzer:
    """
    The Discrete Fourier Transform, computed straight from its definition.

        Analysis:   X[k] = sum_{n=0}^{N-1} x[n] * exp(-2j*pi*k*n/N)
        Synthesis:  x[n] = (1/N) * sum_{k=0}^{N-1} X[k] * exp(+2j*pi*k*n/N)

    How you write it is up to you -- a literal double loop, a precomputed
    table of twiddle factors indexed by (k*n) % N, or a NumPy expression --
    as long as it computes these sums directly and is not secretly an FFT.
    """

    name = "dft"

    def transform(self, x):
        """
        Forward DFT.

        Parameters
        ----------
        x : 1D array_like, length N (real or complex)

        Returns
        -------
        numpy.ndarray of complex128, shape (N,)
        """
        # TODO: implement this method

        x = np.asarray(x, dtype=np.complex128)
        N = len(x)
        n = np.arange(N)
        k = n.reshape((N, 1))
        M = np.exp(-2j * np.pi * k * n / N)
        return np.dot(M, x)

        raise NotImplementedError("Implement DFTAnalyzer.transform")

    def inverse(self, spectrum):
        """
        Inverse DFT, including the 1/N factor.

        Parameters
        ----------
        spectrum : 1D array_like, length N (complex)

        Returns
        -------
        numpy.ndarray of complex128, shape (N,)
            Do NOT discard the imaginary part here -- the caller decides when
            it is safe to take .real.
        """
        # TODO: implement this method
        
        spectrum = np.asarray(spectrum, dtype=np.complex128)
        N = len(spectrum)
        n = np.arange(N)
        k = n.reshape((N, 1))
        M = np.exp(2j * np.pi * k * n / N)
        return np.dot(M, spectrum) / N
        raise NotImplementedError("Implement DFTAnalyzer.inverse")


class FFTTransformer(DFTAnalyzer):
    """
    Radix-2 decimation-in-time (Cooley-Tukey) FFT, in O(N log N).

    It inherits from DFTAnalyzer so that both applications can treat the two
    interchangeably: they call ``engine.transform(...)`` and
    ``engine.inverse(...)`` without caring which engine they hold.

    Requirements:
      * Recursive or iterative (with bit-reversal permutation) -- your choice.
      * N must be a power of two; raise ValueError for any other length.
        The caller is responsible for zero-padding up to next_power_of_two.
      * The inverse must reuse the same butterfly machinery (conjugated
        twiddles, or conjugate-transform-conjugate), not a second copy of it.
      * Twiddle factors for a stage are computed once per stage, never once
        per butterfly.
    """

    name = "fft"

    def transform(self, x):
        """Forward FFT. Same contract as DFTAnalyzer.transform."""
        # TODO: implement this method
        x = np.asarray(x, dtype=np.complex128)
        N = len(x)
        if N == 0 or (N & (N - 1)) != 0:
            raise ValueError("Length of input must be a power of two.") 
        num_bits = (N - 1).bit_length()

        #vectorized bit-reversal permutation
        indices = np.arange(N)
        rev_indices = np.zeros(N, dtype=int)
        for i in range(num_bits):
            rev_indices |= ((indices >> i) & 1) << (num_bits - 1 - i)
        X = x[rev_indices]
        #iterative Cooley-Tukey FFT
        for stage in range(1, num_bits + 1):
            L = 1 << stage      # Length of the sub-problem in this stage
            H = L // 2          # Half-length
            
            # Compute twiddle factors EXACTLY once per stage
            W = np.exp(-2j * np.pi * np.arange(H) / L)
            
            # Reshape array to isolate the N//L blocks of length L.
            
            X_reshaped = X.reshape(-1, L)
            
            even = X_reshaped[:, :H]
            odd = X_reshaped[:, H:] * W
            
            # Butterfly operations
            new_even = even + odd
            new_odd = even - odd
            
            # Write back in-place
            X_reshaped[:, :H] = new_even
            X_reshaped[:, H:] = new_odd

        return X
        raise NotImplementedError("Implement FFTTransformer.transform")

    def inverse(self, spectrum):
        """Inverse FFT, including the 1/N factor."""
        # TODO: implement this method
        spectrum = np.asarray(spectrum, dtype=np.complex128)
        N = len(spectrum)
        if N == 0 or (N & (N - 1)) != 0:
            raise ValueError("Length of input must be a power of two.")
        # Conjugate the input spectrum
        conjugated_spectrum = np.conjugate(spectrum)
        # forward FFT of the conjugated spectrum
        X = self.transform(conjugated_spectrum)
        # Conjugating the result and scaling by 1/N
        return np.conjugate(X) / N
        raise NotImplementedError("Implement FFTTransformer.inverse")


# ---------------------------------------------------------------------------
# BONUS (optional) -- arbitrary-length FFT.
#
# Delete this class if you are not attempting the bonus. If you do attempt it,
# run both tasks with --engine arbitrary and leave those output directories in
# your submission as the evidence.
# ---------------------------------------------------------------------------
class ArbitraryLengthFFT(FFTTransformer):
    """
    Bonus: an O(N log N) transform for ANY length N, not just powers of two.

    Bluestein's chirp-z algorithm is the usual route: rewrite the DFT as a
    convolution of two chirp sequences, and evaluate that convolution with a
    radix-2 FFT of length >= 2N-1. A mixed-radix Cooley-Tukey that factorises
    N is equally acceptable.

    With this engine, Task A no longer has to pad the digit arrays up to a
    power of two, and Task B no longer has to pad the image up to one.
    """

    name = "arbitrary"

    def transform(self, x):
        # TODO (bonus): implement this method
        x = np.asarray(x, dtype=np.complex128)
        N = len(x)
        if N > 0 and (N & (N - 1)) == 0:
            return super().transform(x)  # FFT
        
        M = 1 << (2 * N - 1).bit_length()  # Next power of two >= 2N-1

        n = np.arange(N)
        chirp = np.exp(-1j * np.pi * n**2 / N)

        A = np.zeros(M, dtype=np.complex128)
        A[:N] = x * chirp

        B = np.zeros(M, dtype=np.complex128)
        anti_chirp = np.exp(1j * np.pi * n**2 / N)
        B[:N] = anti_chirp
        B[M-N+1:] = anti_chirp[1:][::-1]
        #fast convolution using FFT
        A_fft = super().transform(A)
        B_fft = super().transform(B)
        C_fft = A_fft * B_fft
        C = super().inverse(C_fft)
        return C[:N] * chirp
        raise NotImplementedError("Bonus: implement ArbitraryLengthFFT.transform")

    def inverse(self, spectrum):
        # TODO (bonus): implement this method
        spectrum = np.asarray(spectrum, dtype=np.complex128)
        N = len(spectrum)
        if N > 0 and (N & (N - 1)) == 0:
            return super().inverse(spectrum)  # FFT
        return np.conj(self.transform(np.conj(spectrum))) / N
        
        #raise NotImplementedError("Bonus: implement ArbitraryLengthFFT.inverse")
