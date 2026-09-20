"""Two Tones, One Sample Set.

Complete the two functions marked TODO. Do not modify main().
Run with:  python alias.py
"""

import numpy as np


def lowest_alias_pair(f, fs):
    """Smallest positive frequency other than f giving identical samples at fs.

    Assumes 0 < f < fs/2.
    """

    # ---------------------------------------------------------------
    # TODO PART 1
    # ---------------------------------------------------------------
    #
    # When we sample a cosine:
    #
    #     cos(2*pi*f*t)
    #
    # at sample times:
    #
    #     t = n/fs
    #
    # we get:
    #
    #     cos(2*pi*f*n/fs)
    #
    # Two frequencies produce the same samples if their frequencies
    # are related through:
    #
    #     f2 = |k*fs +/- f|
    #
    # for some integer k.
    #
    # We are given:
    #
    #     0 < f < fs/2
    #
    # The first positive alias different from f is therefore:
    #
    #     fs - f
    #
    # Example:
    #
    #     f  = 300 Hz
    #     fs = 1000 Hz
    #
    #     partner = 1000 - 300
    #             = 700 Hz
    #
    # Why do they produce the same samples?
    #
    # cos(2*pi*700*n/1000)
    #
    # = cos(2*pi*(1 - 300/1000)*n)
    #
    # = cos(2*pi*n - 2*pi*300*n/1000)
    #
    # Since cos(theta + 2*pi*n) = cos(theta),
    #
    # this becomes:
    #
    # cos(-2*pi*300*n/1000)
    #
    # and cosine is even:
    #
    # cos(-theta) = cos(theta)
    #
    # so:
    #
    # cos(2*pi*700*n/1000)
    # =
    # cos(2*pi*300*n/1000)
    #
    # Therefore the samples are identical.
    # ---------------------------------------------------------------

    return fs - f


def max_sample_difference(f1, f2, fs, duration):
    """Largest absolute difference between samples of two cosines.

    Samples cos(2*pi*f*t) at both f1 and f2, at rate fs, using sample
    times t = n/fs for n = 0 ... N-1 with N = int(duration * fs).

    Returns a single float.
    """

    # ---------------------------------------------------------------
    # TODO PART 2
    # ---------------------------------------------------------------
    #
    # We need:
    #
    #     N = int(duration * fs)
    #
    # samples.
    #
    # The sample numbers are:
    #
    #     n = 0, 1, 2, ..., N-1
    #
    # np.arange(N) gives exactly those values.
    # ---------------------------------------------------------------

    N = int(duration * fs)

    # Create the sample indices:
    #
    #     [0, 1, 2, ..., N-1]
    #
    n = np.arange(N)

    # ---------------------------------------------------------------
    # The assignment specifically says that sample times must be:
    #
    #     t = n / fs
    #
    # So create the time array this way.
    # ---------------------------------------------------------------

    t = n / fs

    # ---------------------------------------------------------------
    # Generate samples for the first frequency:
    #
    #     x1(t) = cos(2*pi*f1*t)
    # ---------------------------------------------------------------

    samples1 = np.cos(2 * np.pi * f1 * t)

    # Generate samples for the second frequency:
    #
    #     x2(t) = cos(2*pi*f2*t)
    # ---------------------------------------------------------------

    samples2 = np.cos(2 * np.pi * f2 * t)

    # ---------------------------------------------------------------
    # Find the difference between corresponding samples.
    #
    # Example:
    #
    # samples1 = [1.0, 0.2, -0.8, ...]
    # samples2 = [1.0, 0.2, -0.8, ...]
    #
    # difference =
    #
    #     [0.0, 0.0, 0.0, ...]
    #
    # Because of floating-point calculations, the differences may
    # actually be tiny values such as:
    #
    #     1e-14
    #
    # rather than exactly zero.
    # ---------------------------------------------------------------

    differences = np.abs(samples1 - samples2)

    # ---------------------------------------------------------------
    # We need the LARGEST absolute difference among all samples.
    #
    # np.max() finds the largest value.
    #
    # float() converts the NumPy scalar into a normal Python float.
    # ---------------------------------------------------------------

    return float(np.max(differences))


# --------------------------------------------------------------------------
# Everything below is provided. Do not modify.
# --------------------------------------------------------------------------

TEST_CASES = [
    # (f in Hz, fs in Hz)
    (300, 1000),
    (100, 1000),
    (440, 8000),
    (50, 400),
    (1200, 3000),
]

DURATION = 0.1  # seconds
TOLERANCE = 1e-9


def main():
    print(f"{'f (Hz)':>10} {'fs (Hz)':>10} {'partner (Hz)':>14} "
          f"{'max |diff|':>14}   result")
    print("-" * 64)

    failures = 0
    for f, fs in TEST_CASES:
        partner = lowest_alias_pair(f, fs)
        diff = max_sample_difference(f, partner, fs, DURATION)

        ok = diff < TOLERANCE
        failures += not ok
        print(f"{f:>10} {fs:>10} {partner:>14} {diff:>14.3e}   "
              f"{'identical' if ok else 'DIFFERENT'}")

    print("-" * 64)
    if failures:
        print(f"{failures} of {len(TEST_CASES)} case(s) did not match. "
              f"Check your partner frequency and your sample times.")
    else:
        print("All cases produced identical samples.")


if __name__ == "__main__":
    main()