# Assignment: Linear Interpolation Sampling

## Background

A sampled signal contains values only at discrete sample times. To obtain a
continuous-looking signal between those samples, we can interpolate between
neighboring samples.

In **linear interpolation**, two neighboring samples are connected by a
straight line. Instead of holding a sample constant like a zero-order hold,
the signal continuously moves from one sample value to the next.

For a sinusoid, ideal linear interpolation introduces frequency-dependent
attenuation. Its normalized gain is

```text
gain(f) = sinc²(f / fs)
```

where

```text
sinc(x) = sin(πx) / (πx)
```

NumPy's `np.sinc(x)` already uses this normalized definition.

Your job is to:

1. Predict the gain using the formula.
2. Actually create a linearly interpolated waveform from samples and measure
   its amplitude.
3. Confirm that the predicted and measured gains agree.

---

## Your task

The starter file contains two functions with their bodies removed.
Fill in both functions.

Do **not** modify `tone_amplitude()` or `main()`.

### Part 1 — `linear_interp_gain(f, fs)`

Return:

```text
gain(f) = |sinc(f / fs)|²
```

### Part 2 — `measured_linear_interp_gain(f, fs, upsample, duration)`

1. Sample `cos(2πft)` at rate `fs`.
2. Create a finer grid at `upsample * fs`.
3. Linearly interpolate the original samples onto that fine grid.
4. Use `tone_amplitude()` to measure the amplitude at frequency `f`.
5. Since the input cosine has amplitude 1, the measured amplitude is the gain.

---

# Starter file — `linear_interpolation.py`

```python
"""Linear interpolation sampling.

Complete the two functions marked TODO.
Do not modify anything below the divider.

Run with:
    python linear_interpolation.py
"""

import numpy as np


def linear_interp_gain(f, fs):
    """Predicted gain of linear interpolation at frequency f.

    gain = |sinc(f / fs)|^2

    TODO:
        Return the predicted gain.
    """
    raise NotImplementedError


def measured_linear_interp_gain(f, fs, upsample, duration):
    """Measure the gain produced by linear interpolation.

    Sample cos(2*pi*f*t) at rate fs, linearly interpolate the samples
    onto a finer grid running at upsample*fs, and measure the amplitude
    of the f component using tone_amplitude().

    TODO:
        Implement the sampling and linear interpolation.
    """
    raise NotImplementedError


# --------------------------------------------------------------------------
# Everything below is provided. Do not modify.
# --------------------------------------------------------------------------

def tone_amplitude(x, fs_fine, f):
    """Amplitude of the component of x at frequency f, via the DFT."""
    n = len(x)
    spectrum = np.fft.rfft(x)
    freqs = np.fft.rfftfreq(n, 1 / fs_fine)
    bin_index = int(np.argmin(np.abs(freqs - f)))
    return 2 * np.abs(spectrum[bin_index]) / n


SAMPLE_RATE = 1000
UPSAMPLE = 100
DURATION = 0.1
TOLERANCE = 1e-3

TEST_FREQS = [50, 100, 200, 300, 450]


def main():
    print(f"{'f (Hz)':>8} {'f/fs':>7} {'predicted':>11} "
          f"{'measured':>10} {'|error|':>10}")
    print("-" * 54)

    failures = 0

    for f in TEST_FREQS:
        predicted = linear_interp_gain(f, SAMPLE_RATE)

        measured = measured_linear_interp_gain(
            f, SAMPLE_RATE, UPSAMPLE, DURATION
        )

        error = abs(predicted - measured)
        failures += error >= TOLERANCE

        print(f"{f:>8} {f / SAMPLE_RATE:>7.2f} "
              f"{predicted:>11.4f} {measured:>10.4f} "
              f"{error:>10.2e}")

    print("-" * 54)

    if failures:
        print(
            f"{failures} of {len(TEST_FREQS)} case(s) disagree by more "
            f"than {TOLERANCE:g}. Check your interpolation."
        )
    else:
        print("Prediction matches measurement at every frequency.")


if __name__ == "__main__":
    main()
```

---

# Expected output

Exact final digits can vary slightly because the measured value comes from
a finite waveform and DFT.

```text
  f (Hz)   f/fs   predicted   measured    |error|
------------------------------------------------------
      50    0.05      0.9918     0.9918    small
     100    0.10      0.9675     0.9675    small
     200    0.20      0.8751     0.8751    small
     300    0.30      0.7378     0.7378    small
     450    0.45      0.4881     0.4881    small
------------------------------------------------------
Prediction matches measurement at every frequency.
```

---

# SOLUTION — Complete code with detailed TODO comments

```python
"""Linear interpolation sampling.

Complete the two functions marked TODO.
Do not modify anything below the divider.

Run with:
    python linear_interpolation.py
"""

import numpy as np


def linear_interp_gain(f, fs):
    """Predicted gain of linear interpolation at frequency f.

    gain = |sinc(f / fs)|^2
    """

    # np.sinc(x) uses the normalized definition:
    #
    #     sinc(x) = sin(pi*x) / (pi*x)
    #
    # Therefore np.sinc(f / fs) directly gives the sinc term.
    sinc_value = np.sinc(f / fs)

    # Linear interpolation has a sinc-squared response.
    # Gain is a magnitude, so use abs() before squaring.
    gain = np.abs(sinc_value) ** 2

    return float(gain)


def measured_linear_interp_gain(f, fs, upsample, duration):
    """Measure the gain produced by linear interpolation."""

    # ---------------------------------------------------------------
    # STEP 1: Find the number of original samples.
    #
    # N = duration * sampling_rate
    #
    # Example:
    # duration = 0.1 s
    # fs = 1000 Hz
    # N = 100 samples
    # ---------------------------------------------------------------
    N = int(duration * fs)

    # ---------------------------------------------------------------
    # STEP 2: Create sample indices:
    #
    #     n = 0, 1, 2, ..., N-1
    # ---------------------------------------------------------------
    n = np.arange(N)

    # ---------------------------------------------------------------
    # STEP 3: Convert sample indices into sample times.
    #
    # Samples occur at:
    #
    #     t = n / fs
    # ---------------------------------------------------------------
    t_samples = n / fs

    # ---------------------------------------------------------------
    # STEP 4: Sample the original cosine.
    #
    #     x(t) = cos(2*pi*f*t)
    #
    # These are the actual discrete samples before interpolation.
    # ---------------------------------------------------------------
    samples = np.cos(2 * np.pi * f * t_samples)

    # ---------------------------------------------------------------
    # STEP 5: Create a fine grid.
    #
    # The fine-grid sampling rate is:
    #
    #     fs_fine = upsample * fs
    #
    # Example:
    #     fs = 1000
    #     upsample = 100
    #     fs_fine = 100000 Hz
    #
    # The fine grid lets us represent the straight lines between
    # neighboring samples using many small points.
    # ---------------------------------------------------------------
    fs_fine = upsample * fs

    t_fine = np.arange(N * upsample) / fs_fine

    # ---------------------------------------------------------------
    # STEP 6: Perform linear interpolation.
    #
    # np.interp(new_x, old_x, old_y)
    #
    # Here:
    #     new_x = t_fine
    #     old_x = t_samples
    #     old_y = samples
    #
    # For each fine-grid time, np.interp() finds the two neighboring
    # original samples and calculates the value on the straight line
    # connecting them.
    # ---------------------------------------------------------------
    interpolated = np.interp(
        t_fine,
        t_samples,
        samples
    )

    # ---------------------------------------------------------------
    # STEP 7: Measure the amplitude at frequency f.
    #
    # tone_amplitude() uses the DFT to find the amplitude of the
    # f-frequency component.
    #
    # The input cosine has amplitude 1, therefore:
    #
    #     measured amplitude = interpolation gain
    # ---------------------------------------------------------------
    measured_gain = tone_amplitude(
        interpolated,
        fs_fine,
        f
    )

    return float(measured_gain)


# --------------------------------------------------------------------------
# Everything below is provided. Do not modify.
# --------------------------------------------------------------------------

def tone_amplitude(x, fs_fine, f):
    """Amplitude of the component of x at frequency f, via the DFT."""
    n = len(x)
    spectrum = np.fft.rfft(x)
    freqs = np.fft.rfftfreq(n, 1 / fs_fine)
    bin_index = int(np.argmin(np.abs(freqs - f)))
    return 2 * np.abs(spectrum[bin_index]) / n


SAMPLE_RATE = 1000
UPSAMPLE = 100
DURATION = 0.1
TOLERANCE = 1e-3

TEST_FREQS = [50, 100, 200, 300, 450]


def main():
    print(f"{'f (Hz)':>8} {'f/fs':>7} {'predicted':>11} "
          f"{'measured':>10} {'|error|':>10}")
    print("-" * 54)

    failures = 0

    for f in TEST_FREQS:
        predicted = linear_interp_gain(f, SAMPLE_RATE)

        measured = measured_linear_interp_gain(
            f, SAMPLE_RATE, UPSAMPLE, DURATION
        )

        error = abs(predicted - measured)
        failures += error >= TOLERANCE

        print(f"{f:>8} {f / SAMPLE_RATE:>7.2f} "
              f"{predicted:>11.4f} {measured:>10.4f} "
              f"{error:>10.2e}")

    print("-" * 54)

    if failures:
        print(
            f"{failures} of {len(TEST_FREQS)} case(s) disagree by more "
            f"than {TOLERANCE:g}. Check your interpolation."
        )
    else:
        print("Prediction matches measurement at every frequency.")


if __name__ == "__main__":
    main()
```

---

# TODO 1 — `linear_interp_gain()`

The required formula is:

```text
gain(f) = |sinc(f / fs)|²
```

So first calculate:

```python
sinc_value = np.sinc(f / fs)
```

Then:

```python
gain = np.abs(sinc_value) ** 2
```

and return it.

For example, if:

```text
f  = 100 Hz
fs = 1000 Hz
```

then:

```text
f/fs = 0.1
```

and:

```text
gain = sinc(0.1)² ≈ 0.9675
```

So the output amplitude is about 96.75% of the original amplitude.

---

# TODO 2 — `measured_linear_interp_gain()`

This TODO demonstrates the interpolation process directly.

## Step 1 — Original sample times

```python
N = int(duration * fs)
n = np.arange(N)
t_samples = n / fs
```

If `fs = 1000`:

```text
n:          0       1       2       3       ...
t_samples:  0   0.001   0.002   0.003   ...
```

---

## Step 2 — Original sampled signal

```python
samples = np.cos(2 * np.pi * f * t_samples)
```

This gives the discrete samples of the cosine.

---

## Step 3 — Fine grid

```python
fs_fine = upsample * fs
```

For:

```text
fs = 1000
upsample = 100
```

we get:

```text
fs_fine = 100000 Hz
```

The original signal is **not** actually sampled at 100000 Hz.
We are only using the fine grid to numerically represent the interpolated
waveform.

---

## Step 4 — Linear interpolation

The key line is:

```python
interpolated = np.interp(t_fine, t_samples, samples)
```

`np.interp()` connects neighboring samples using straight lines.

For example:

```text
sample A = 0.8
sample B = 0.2
```

The interpolated values between them look like:

```text
0.80
0.74
0.68
0.62
0.56
...
0.26
0.20
```

Unlike zero-order hold, the value changes continuously between samples.

---

# Linear interpolation vs Zero-order hold

## Zero-order hold

```text
sample
  |
  |────────────
  |            |
  |            |────────────
  |            |            |
  +------------+------------+----> t
```

Each sample stays constant.

Gain:

```text
|sinc(f/fs)|
```

## Linear interpolation

```text
sample
  |  |   |    |     |    \ sample
  |       +------\------------------> t
```

Neighboring samples are connected by straight lines.

Gain:

```text
|sinc(f/fs)|²
```

---

# Why measured amplitude equals gain

The original signal is:

```text
x(t) = cos(2πft)
```

Its input amplitude is:

```text
1
```

Suppose after interpolation the DFT measures:

```text
0.8751
```

Then:

```text
gain = output amplitude / input amplitude

     = 0.8751 / 1

     = 0.8751
```

Therefore the measured amplitude itself is the gain.

---

# Important exam points

### 1. NumPy's sinc is normalized

Use:

```python
np.sinc(x)
```

because NumPy defines:

```text
sinc(x) = sin(πx)/(πx)
```

---

### 2. `np.interp()` argument order

Remember:

```python
np.interp(new_times, old_times, old_values)
```

Here:

```python
np.interp(t_fine, t_samples, samples)
```

means:

- `t_fine` → positions where we want new values
- `t_samples` → original sample positions
- `samples` → original sample values

---

### 3. Fine-grid sampling rate

If:

```text
fs = 1000
upsample = 100
```

then:

```text
fs_fine = 100 × 1000
       = 100000 Hz
```

The same `fs_fine` must be passed to `tone_amplitude()`.

---

### 4. Upsampling is not the original sampling rate

The signal is initially sampled at:

```text
fs
```

Only the interpolated representation uses:

```text
upsample * fs
```

---

# Workflow summary

```text
Original cosine
      |
      v
Sample at fs
      |
      v
Discrete samples
      |
      v
Create fine time grid
      |
      v
Linear interpolation
      |
      v
Interpolated waveform
      |
      v
DFT / tone_amplitude()
      |
      v
Measured amplitude
      |
      v
Compare with sinc²(f/fs)
```

## The two TODO answers in compact form

```python
def linear_interp_gain(f, fs):
    sinc_value = np.sinc(f / fs)
    gain = np.abs(sinc_value) ** 2
    return float(gain)
```

```python
def measured_linear_interp_gain(f, fs, upsample, duration):
    N = int(duration * fs)

    n = np.arange(N)
    t_samples = n / fs

    samples = np.cos(2 * np.pi * f * t_samples)

    fs_fine = upsample * fs
    t_fine = np.arange(N * upsample) / fs_fine

    interpolated = np.interp(
        t_fine,
        t_samples,
        samples
    )

    measured_gain = tone_amplitude(
        interpolated,
        fs_fine,
        f
    )

    return float(measured_gain)
```