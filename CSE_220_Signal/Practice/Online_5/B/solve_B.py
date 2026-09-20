"""The Staircase Droops: zero-order hold gain.

Complete the two functions marked TODO. Do not modify anything below
the divider. Run with:  python zoh.py
"""

import numpy as np


def zoh_gain(f, fs):
    """Predicted zero-order-hold gain at frequency f, sampling at rate fs.

    gain = |sinc(f / fs)|
    """

    # ---------------------------------------------------------------
    # TODO PART 1
    # ---------------------------------------------------------------
    #
    # The assignment gives us the formula:
    #
    #              gain(f) = |sinc(f / fs)|
    #
    # NumPy's np.sinc(x) is defined as:
    #
    #              sinc(x) = sin(pi*x) / (pi*x)
    #
    # So we simply calculate:
    #
    #              np.sinc(f / fs)
    #
    # and take the absolute value.
    #
    # Example:
    #
    #     f  = 100 Hz
    #     fs = 1000 Hz
    #
    #     f / fs = 0.1
    #
    #     gain = |sinc(0.1)|
    #
    # This gives approximately:
    #
    #     0.9836
    #
    # ---------------------------------------------------------------

    return float(np.abs(np.sinc(f / fs)))


def measured_zoh_gain(f, fs, upsample, duration):
    """Gain of the zero-order hold, measured rather than predicted.

    Samples cos(2*pi*f*t) at rate fs over [0, duration), holds each
    sample for `upsample` steps of a grid running at upsample*fs (use
    np.repeat(samples, upsample)), then returns the amplitude of the
    f component of the resulting staircase (using the tone amplitude
    method below, second parameter should be upsample * fs).

    The input amplitude is 1, so this amplitude is the gain.
    """

    # ---------------------------------------------------------------
    # TODO PART 2
    # ---------------------------------------------------------------
    #
    # We need to perform the zero-order-hold process manually.
    #
    # Original signal:
    #
    #     x(t) = cos(2*pi*f*t)
    #
    # First, we sample this signal at the original sampling rate fs.
    #
    # The number of original samples is:
    #
    #     N = int(duration * fs)
    #
    # For example:
    #
    #     duration = 0.1 seconds
    #     fs       = 1000 Hz
    #
    # Then:
    #
    #     N = 0.1 * 1000
    #       = 100 samples
    #
    # ---------------------------------------------------------------

    N = int(duration * fs)

    # ---------------------------------------------------------------
    # Create the original sample indices:
    #
    #     n = 0, 1, 2, ..., N-1
    #
    # ---------------------------------------------------------------

    n = np.arange(N)

    # ---------------------------------------------------------------
    # Convert the sample indices into sample times.
    #
    # Sampling times are:
    #
    #     t = n / fs
    #
    # ---------------------------------------------------------------

    t = n / fs

    # ---------------------------------------------------------------
    # Sample the cosine signal.
    #
    # The original continuous-time signal is:
    #
    #     cos(2*pi*f*t)
    #
    # So samples contains:
    #
    #     x[0], x[1], x[2], ..., x[N-1]
    #
    # ---------------------------------------------------------------

    samples = np.cos(2 * np.pi * f * t)

    # ---------------------------------------------------------------
    # Now create the zero-order-hold staircase.
    #
    # Each sample must be held for `upsample` steps.
    #
    # np.repeat() does exactly this.
    #
    # Suppose:
    #
    #     samples = [1, 2, 3]
    #
    # and:
    #
    #     upsample = 4
    #
    # np.repeat(samples, 4) gives:
    #
    #     [1, 1, 1, 1,
    #      2, 2, 2, 2,
    #      3, 3, 3, 3]
    #
    # This is the staircase produced by the zero-order hold.
    # ---------------------------------------------------------------

    staircase = np.repeat(samples, upsample)

    # ---------------------------------------------------------------
    # The staircase is now sampled on a FINER grid.
    #
    # The fine sampling rate is:
    #
    #     fs_fine = upsample * fs
    #
    # Example:
    #
    #     fs       = 1000 Hz
    #     upsample = 100
    #
    #     fs_fine  = 100 * 1000
    #              = 100000 Hz
    #
    # ---------------------------------------------------------------

    fs_fine = upsample * fs

    # ---------------------------------------------------------------
    # Now measure how much of the ORIGINAL frequency f remains in
    # the staircase.
    #
    # tone_amplitude() performs a DFT and extracts the amplitude
    # corresponding to frequency f.
    #
    # IMPORTANT:
    #
    # We must give it the fine-grid sampling rate, NOT the original
    # sampling rate.
    #
    # Therefore:
    #
    #     tone_amplitude(staircase, fs_fine, f)
    #
    # ---------------------------------------------------------------

    measured_gain = tone_amplitude(staircase, fs_fine, f)

    # ---------------------------------------------------------------
    # The original cosine had amplitude 1:
    #
    #     cos(2*pi*f*t)
    #
    # Therefore, if the staircase's f-component has amplitude:
    #
    #     0.9355
    #
    # then the gain is:
    #
    #     0.9355 / 1
    #     = 0.9355
    #
    # So the measured amplitude itself is the gain.
    # ---------------------------------------------------------------

    return float(measured_gain)


# --------------------------------------------------------------------------
# Everything below is provided. Do not modify.
# --------------------------------------------------------------------------

def tone_amplitude(x, fs_fine, f):
    """Amplitude of the component of x at frequency f, via the DFT.

    x is real and sampled at rate fs_fine. Assumes f falls on a DFT bin,
    which the parameters in main() guarantee.
    """

    n = len(x)

    # Calculate the real-valued DFT.
    spectrum = np.fft.rfft(x)

    # Calculate the frequency corresponding to each DFT bin.
    freqs = np.fft.rfftfreq(n, 1 / fs_fine)

    # Find the DFT bin closest to the requested frequency f.
    bin_index = int(np.argmin(np.abs(freqs - f)))

    # Convert the magnitude of the DFT coefficient into sinusoid
    # amplitude.
    return 2 * np.abs(spectrum[bin_index]) / n


SAMPLE_RATE = 1000     # Hz
UPSAMPLE = 100         # fine-grid steps per held sample
DURATION = 0.1         # seconds
TOLERANCE = 1e-3

TEST_FREQS = [50, 100, 200, 300, 450]   # Hz


def main():
    print(f"{'f (Hz)':>8} {'f/fs':>7} {'predicted':>11} {'measured':>10} "
          f"{'|error|':>10} {'droop':>10}")
    print("-" * 60)

    failures = 0
    for f in TEST_FREQS:
        predicted = zoh_gain(f, SAMPLE_RATE)
        measured = measured_zoh_gain(f, SAMPLE_RATE, UPSAMPLE, DURATION)
        error = abs(predicted - measured)

        failures += error >= TOLERANCE
        droop_db = 20 * np.log10(predicted)
        print(f"{f:>8} {f / SAMPLE_RATE:>7.2f} {predicted:>11.4f} "
              f"{measured:>10.4f} {error:>10.2e} {droop_db:>7.2f} dB")

    print("-" * 60)
    if failures:
        print(f"{failures} of {len(TEST_FREQS)} case(s) disagree by more than "
              f"{TOLERANCE:g}. Check your formula and your staircase.")
    else:
        print("Prediction matches measurement at every frequency.")


if __name__ == "__main__":
    main()