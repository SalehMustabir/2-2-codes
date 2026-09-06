"""
image_conv.py  --  TASK B: blurring an image through the frequency domain.

YOUR CODE GOES HERE. image_utils.py (loading, saving, kernels, comparison
figures) and bench_utils.py (timing, runtime plots) are provided; the
transform, the padding logic and the convolution are yours.

Usage (the command line is already wired up for you):

    python3 image_conv.py images/skyline512.png --kernel bokeh --param 9 \
        --engine fft --out-dir outputs/skyline_bokeh
    python3 image_conv.py images/sunset512.png --gray --kernel motion \
        --param 41 --engine fft --out-dir outputs/sunset_motion
    python3 image_conv.py images/skyline512.png --benchmark \
        --out-dir outputs/benchmark

Restrictions: no numpy.fft / scipy.fft / scipy.ndimage / cv2 / PIL filters,
no numpy.convolve, no scipy.signal. Every transform goes through your own
transforms.py.
"""

import argparse
import os
import sys

import numpy as np

from bench_utils import plot_runtime_curve, time_best, timing_table_lines
from image_utils import (load_image, make_kernel, save_comparison, save_image,
                         save_kernel_preview)
from io_utils import write_report
from transforms import DFTAnalyzer, FFTTransformer, next_power_of_two, ArbitraryLengthFFT


def transform_2d(plane, engine):
    """
    2D forward transform of a single 2D array, by separability.

    The 2D DFT factorises into 1D transforms: transform every ROW, then
    transform every COLUMN of the result (the order does not matter). This is
    the only reason a 2D transform is affordable -- doing it directly from the
    2D definition would be O(N^4).

    Parameters
    ----------
    plane : 2D array_like, shape (P, Q)
    engine : DFTAnalyzer or FFTTransformer

    Returns
    -------
    numpy.ndarray of complex128, shape (P, Q)
    """
    # TODO: implement this function
    P, Q = plane.shape
    
    # Transform every ROW
    row_transformed = np.zeros((P, Q), dtype=np.complex128)
    for r in range(P):
        row_transformed[r, :] = engine.transform(plane[r, :])
        
    # Transform every COLUMN of the result
    result = np.zeros((P, Q), dtype=np.complex128)
    for c in range(Q):
        result[:, c] = engine.transform(row_transformed[:, c])
        
    return result
    raise NotImplementedError("Implement transform_2d")


def inverse_2d(spectrum, engine):
    """
    2D inverse transform, the same way round. Shape is preserved.
    """
    # TODO: implement this function
    P, Q = spectrum.shape
    
    # Inverse transform every ROW
    row_inv = np.zeros((P, Q), dtype=np.complex128)
    for r in range(P):
        row_inv[r, :] = engine.inverse(spectrum[r, :])
        
    # Inverse transform every COLUMN of the result[cite: 1, 2]
    result = np.zeros((P, Q), dtype=np.complex128)
    for c in range(Q):
        result[:, c] = engine.inverse(row_inv[:, c])
        
    return result
    raise NotImplementedError("Implement inverse_2d")


def convolve_plane(plane, kernel, engine, circular=False):
    """
    Convolve one 2D plane with ``kernel`` through the frequency domain.

    Returns an array the SAME size as the input plane.

    circular=False (the normal case -- linear convolution):
        The full convolution of an (H, W) image with a (kh, kw) kernel is
        (H+kh-1, W+kw-1). Zero-pad both arrays to at least that size before
        transforming -- with FFTTransformer, pad further, up to a power of two
        in each dimension (every engine exposes a ``name`` attribute, so
        ``engine.name == "fft"`` tells you which rule applies). Multiply the
        two spectra, inverse-transform, take the real part, then crop the
        (H, W) window that corresponds to the original pixels: rows
        kh//2 .. kh//2+H-1 and columns kw//2 .. kw//2+W-1
        (the kernel sits at the origin of the padded array, so the result is
        offset by half the kernel -- forget this and your image comes out
        shifted diagonally).

    circular=True (the deliberate mistake -- see the specification):
        Transform at exactly (H, W) with no padding at all, with the kernel
        wrapped around the origin (np.roll is allowed -- it is not a
        transform). The output is the CIRCULAR convolution: content that
        should have fallen off one edge reappears on the opposite edge. The
        provided images are 256x256 and 512x512, so this path works with the
        radix-2 FFT directly.

    Parameters
    ----------
    plane : 2D numpy array of float, values in [0, 1]
    kernel : 2D numpy array of float, sums to 1
    engine : DFTAnalyzer or FFTTransformer
    circular : bool

    Returns
    -------
    numpy.ndarray of float64, same shape as ``plane``
    """
    # TODO: implement this function
    H, W = plane.shape
    kh, kw = kernel.shape
    
    if circular:
        # Transform at exactly (H, W) with no padding at all
        pad_H, pad_W = H, W
        pad_plane = plane.copy()
        
        # Wrap the kernel around the origin (using np.roll)
        pad_kernel = np.zeros((H, W), dtype=np.float64)
        pad_kernel[:kh, :kw] = kernel
        pad_kernel = np.roll(pad_kernel, -kh // 2, axis=0)
        pad_kernel = np.roll(pad_kernel, -kw // 2, axis=1)
        
    else:
        # Zero-pad to at least H + kh - 1, W + kw - 1 for linear convolution[cite: 1, 2]
        linear_H = H + kh - 1
        linear_W = W + kw - 1
        
        # If the engine is FFT, pad further to the next power of two[cite: 1, 2]
        if engine.name == "fft":
            pad_H = next_power_of_two(linear_H)
            pad_W = next_power_of_two(linear_W)
        else:
            pad_H = linear_H
            pad_W = linear_W
            
        pad_plane = np.zeros((pad_H, pad_W), dtype=np.float64)
        pad_plane[:H, :W] = plane
        
        # The kernel sits at the origin of the padded array (no rolling)
        pad_kernel = np.zeros((pad_H, pad_W), dtype=np.float64)
        pad_kernel[:kh, :kw] = kernel

    # Transform both into the frequency domain[cite: 1]
    Plane_F = transform_2d(pad_plane, engine)
    Kernel_F = transform_2d(pad_kernel, engine)
    
    # Multiply the two spectra pointwise and inverse-transform[cite: 1, 2]
    Conv_F = Plane_F * Kernel_F
    conv_time = inverse_2d(Conv_F, engine).real
    
    if circular:
        # For circular convolution, the result requires no cropping and remains (H, W)[cite: 1]
        return conv_time
    else:
        # Crop the (H, W) window corresponding to the original pixels[cite: 1, 2]
        # Rows kh//2 .. kh//2+H-1 and columns kw//2 .. kw//2+W-1
        r_start, r_end = kh // 2, kh // 2 + H
        c_start, c_end = kw // 2, kw // 2 + W
        return conv_time[r_start:r_end, c_start:c_end]
    raise NotImplementedError("Implement convolve_plane")


def convolve_image(image, kernel, engine, circular=False):
    """
    Apply convolve_plane to a whole image.

    A grayscale image is (H, W); a colour image is (H, W, 3) and each colour
    plane is convolved independently, then stacked back together.
    """
    # TODO: implement this function
    if image.ndim == 2:
        return convolve_plane(image, kernel, engine, circular)
        
    elif image.ndim == 3:
        # Each colour plane is convolved independently, then stacked back together[cite: 1]
        out_image = np.zeros_like(image, dtype=np.float64)
        for channel in range(image.shape[2]):
            out_image[:, :, channel] = convolve_plane(
                image[:, :, channel], kernel, engine, circular
            )
        return out_image
    raise NotImplementedError("Implement convolve_image")


def convolve_plane_direct(plane, kernel):
    """
    Spatial convolution, written out literally, as the correctness oracle and
    the third benchmark curve.

        out[r, c] = sum_i sum_j  plane[r + kh//2 - i, c + kw//2 - j] * kernel[i, j]

    with out-of-range pixels treated as zero. Four nested loops, O(N^2 K^2),
    no NumPy vectorisation -- this one is meant to be slow and obviously
    correct. It is never applied to a full 512x512 image (see run_single).
    """
    # TODO: implement this function
    H, W = plane.shape
    kh, kw = kernel.shape
    out = np.zeros_like(plane, dtype=np.float64)
    
    # Four nested loops, O(N^2 K^2), no NumPy vectorisation[cite: 1, 2]
    for r in range(H):
        for c in range(W):
            val = 0.0
            for i in range(kh):
                for j in range(kw):
                    # plane[r + kh//2 - i, c + kw//2 - j] * kernel[i, j][cite: 1]
                    pr = r + kh // 2 - i
                    pc = c + kw // 2 - j
                    # Out-of-range pixels treated as zero[cite: 1]
                    if 0 <= pr < H and 0 <= pc < W:
                        val += plane[pr, pc] * kernel[i, j]
            out[r, c] = val
            
    return out
    
    raise NotImplementedError("Implement convolve_plane_direct")


def run_single(path, kernel_name, param, engine_name, out_dir, gray=False):
    """
    Blur one image and write the required outputs.

    Build the kernel with image_utils.make_kernel:
        bokeh    -> make_kernel("bokeh", radius=param)
        gaussian -> make_kernel("gaussian", size=param)
        box      -> make_kernel("box", size=param)
        motion   -> make_kernel("motion", length=param, angle=30.0)

    Must produce, inside ``out_dir``:
      blurred.png     -- the linear (zero-padded) convolution
      wraparound.png  -- the same blur computed circularly, with no padding
      kernel.png      -- image_utils.save_kernel_preview of the kernel
      comparison.png  -- image_utils.save_comparison of original / blurred /
                         wraparound, side by side
      report.txt      -- image path and size, kernel name and size, engine,
                         the linear-convolution size, the transform size you
                         actually used, and the verification result. It is
                         written by your code; there is no separate write-up
                         to hand in.

    Verification: convolve the top-left 64x64 corner of the image (first colour
    plane, if colour) both ways -- convolve_plane and convolve_plane_direct --
    and report max |spectral - direct|. It should be ~1e-15, and anything above
    1e-9 is a bug, not rounding.
    """
    # TODO: implement this function
    image = load_image(path, as_gray=gray)

    if kernel_name == "bokeh":
        kernel = make_kernel("bokeh", radius=param)
    elif kernel_name == "gaussian":
        kernel = make_kernel("gaussian", size=param)
    elif kernel_name == "box":
        kernel = make_kernel("box", size=param)
    elif kernel_name == "motion":
        kernel = make_kernel("motion", length=param, angle=30.0)
    else:
        raise ValueError("unknown kernel: %r" % (kernel_name,))

    if engine_name == "dft":
        engine = DFTAnalyzer()
    elif engine_name == "fft":
        engine = FFTTransformer()
    elif engine_name == "arbitrary":
        engine = ArbitraryLengthFFT()
    else:
        raise ValueError("unknown engine: %r" % (engine_name,))

    H, W = image.shape[0], image.shape[1]
    kh, kw = kernel.shape
    full_h, full_w = H + kh - 1, W + kw - 1
    if engine.name == "fft":
        Nh, Nw = next_power_of_two(full_h), next_power_of_two(full_w)
    else:
        Nh, Nw = full_h, full_w

    blurred = convolve_image(image, kernel, engine, circular=False)
    wraparound = convolve_image(image, kernel, engine, circular=True)

    save_image(blurred, os.path.join(out_dir, "blurred.png"))
    save_image(wraparound, os.path.join(out_dir, "wraparound.png"))
    save_kernel_preview(kernel, os.path.join(out_dir, "kernel.png"),title=kernel_name)


    save_comparison(
        [image, blurred, wraparound],
        ["original", "linear convolution (zero-padded)",
         "circular convolution (no padding)"],
        os.path.join(out_dir, "comparison.png"),
        suptitle="%s, %s kernel %dx%d, engine=%s" % (
            os.path.basename(path), kernel_name, kh, kw, engine_name),
    )


    crop_size = 64
    crop = image[:crop_size, :crop_size, 0] if image.ndim == 3 \
        else image[:crop_size, :crop_size]
    spectral_crop = convolve_plane(crop, kernel, engine, circular=False)
    direct_crop = convolve_plane_direct(crop, kernel)
    max_err = float(np.max(np.abs(spectral_crop - direct_crop)))
    verdict = "MATCH" if max_err < 1e-9 else "MISMATCH"

    mode = "gray" if image.ndim == 2 else "RGB"
    write_report(os.path.join(out_dir, "report.txt"), [
        "Task B -- 2D convolution through the frequency domain",
        "image               : %s  (%d x %d, %s)" % (path, H, W, mode),
        "kernel              : %s  (%d x %d)" % (kernel_name, kh, kw),
        "engine              : %s" % engine_name,
        "linear-conv size    : %d x %d" % (full_h, full_w),
        "transform size      : %d x %d" % (Nh, Nw),
        "max |spectral - direct| on 64x64 crop : %.3e" % max_err,
        "verification        : %s" % verdict,
    ])

    print("%s [%s, %s]: max err=%.3e (%s)" %
         (path, kernel_name, engine_name, max_err, verdict))
    if verdict == "MISMATCH":
        print("  !! MISMATCH -- spectral convolution disagrees with the "
             "direct oracle", file=sys.stderr)


# ---------------------------------------------------------------------------
# PROVIDED -- run_benchmark is already written. It calls your convolve_plane
# and convolve_plane_direct, so it starts working as soon as those are
# correct. You do not need to modify anything below (though you may extend
# it).
# ---------------------------------------------------------------------------
IMAGE_SIZES = [16, 32, 64, 128, 256, 512]
KERNEL_RADII = [1, 3, 7, 15, 31]
BENCH_RADIUS = 7            # kernel used for the growing-image study
BENCH_SIZE = 256            # image crop used for the growing-kernel study
TIME_BUDGET = 8.0           # stop a sweep once one measurement exceeds this


def run_benchmark(path, out_dir):
    """
    Two timing studies, two plots, both on one grayscale plane:

      1. growing image, fixed kernel   -> runtime_vs_image_size.png
      2. growing kernel, fixed image   -> runtime_vs_kernel_size.png

    plus both timing tables in report.txt. Each sweep stops early once a
    single measurement exceeds TIME_BUDGET seconds, so a slow machine simply
    produces a shorter curve rather than hanging.
    """
    full = load_image(path, as_gray=True)

    def sweep(label, make_call, points):
        """points: list of (x_value, zero-argument-callable-factory input)."""
        xs, ys = [], []
        print("%s:" % label)
        for x, arg in points:
            seconds = time_best(make_call(arg), repeats=1)
            xs.append(x)
            ys.append(seconds)
            print("  %8s   %9.4f s" % (x, seconds))
            if seconds > TIME_BUDGET:
                print("  (stopping this curve -- over the time budget)")
                break
        return xs, ys

    # ---- study 1: fixed kernel, growing image
    kernel = make_kernel("bokeh", radius=BENCH_RADIUS)
    crops = [(n, full[:n, :n].copy()) for n in IMAGE_SIZES]

    size_series = {}
    size_series["Naive DFT (row-column)"] = sweep(
        "naive DFT", lambda img: (lambda: convolve_plane(img, kernel, DFTAnalyzer())), crops)
    size_series["Radix-2 FFT (row-column)"] = sweep(
        "radix-2 FFT", lambda img: (lambda: convolve_plane(img, kernel, FFTTransformer())), crops)
    size_series["Direct spatial convolution"] = sweep(
        "direct spatial", lambda img: (lambda: convolve_plane_direct(img, kernel)), crops)

    size_plot = os.path.join(out_dir, "runtime_vs_image_size.png")
    plot_runtime_curve(size_series, size_plot,
                       title="Task B: %d x %d blur of an N x N image" % kernel.shape,
                       xlabel="image side length N (pixels)",
                       references=("n3", "n2"))

    # ---- study 2: fixed image, growing kernel
    image = full[:BENCH_SIZE, :BENCH_SIZE].copy()
    kernels = [(make_kernel("bokeh", radius=r).shape[0], make_kernel("bokeh", radius=r))
               for r in KERNEL_RADII]

    kernel_series = {}
    kernel_series["Direct spatial convolution"] = sweep(
        "direct spatial", lambda k: (lambda: convolve_plane_direct(image, k)), kernels)
    kernel_series["Radix-2 FFT (row-column)"] = sweep(
        "radix-2 FFT", lambda k: (lambda: convolve_plane(image, k, FFTTransformer())), kernels)

    kernel_plot = os.path.join(out_dir, "runtime_vs_kernel_size.png")
    plot_runtime_curve(kernel_series, kernel_plot,
                       title="Task B: %d x %d image, growing kernel" % image.shape,
                       xlabel="kernel side length K (pixels)",
                       references=("n2",))

    write_report(os.path.join(out_dir, "report.txt"),
                 ["Task B -- runtime benchmark", "",
                  "Study 1: fixed %d x %d kernel, growing image" % kernel.shape, ""]
                 + timing_table_lines(size_series, size_label="N")
                 + ["", "plot: %s" % os.path.basename(size_plot), "",
                    "Study 2: fixed %d x %d image, growing kernel" % image.shape, ""]
                 + timing_table_lines(kernel_series, size_label="K")
                 + ["", "plot: %s" % os.path.basename(kernel_plot)])
    print("wrote", size_plot, "and", kernel_plot)


def main():
    ap = argparse.ArgumentParser(description="2D convolution by DFT/FFT")
    ap.add_argument("image", help="path to the input image")
    ap.add_argument("--kernel", default="bokeh",
                    choices=["bokeh", "gaussian", "box", "motion"])
    ap.add_argument("--param", type=float, default=9,
                    help="bokeh radius / gaussian size / box size / motion length")
    ap.add_argument("--engine", default="fft", choices=["dft", "fft", "arbitrary"])
    ap.add_argument("--gray", action="store_true", help="process as grayscale")
    ap.add_argument("--out-dir", default="outputs")
    ap.add_argument("--benchmark", action="store_true",
                    help="run the timing study instead of a single blur")
    args = ap.parse_args()

    os.makedirs(args.out_dir, exist_ok=True)
    if args.benchmark:
        run_benchmark(args.image, args.out_dir)
    else:
        run_single(args.image, args.kernel, args.param, args.engine,
                   args.out_dir, gray=args.gray)


if __name__ == "__main__":
    main()
