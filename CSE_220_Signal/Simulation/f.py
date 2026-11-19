import numpy as np
import matplotlib.pyplot as plt
from matplotlib.widgets import Slider, RadioButtons

# ==========================================
# 1. SIMULATION PARAMETERS & GRID SETUP
# ==========================================
# Dense grid to represent "continuous" time
T_SIM = 2.0                 # Total duration (-1.0s to +1.0s)
FS_DENSE = 4000             # Dense analog rate (Hz)
N_DENSE = int(T_SIM * FS_DENSE)
t = np.linspace(-T_SIM / 2, T_SIM / 2, N_DENSE, endpoint=False)
dt = t[1] - t[0]

# Frequency grid for dense CTFT approximation
freqs = np.fft.fftshift(np.fft.fftfreq(N_DENSE, d=dt))
df = freqs[1] - freqs[0]

def get_signal(sig_type):
    """Generates continuous signal and returns (signal, bandwidth f_max in Hz)"""
    if sig_type == 'Two Tones (3Hz + 7Hz)':
        # Bandwidth is 7 Hz -> Nyquist rate is 14 Hz
        return np.cos(2 * np.pi * 3 * t), 7.0
        return np.cos(2 * np.pi * 3 * t) + 0.6 * np.cos(2 * np.pi * 7 * t), 7.0
    elif sig_type == 'Sinc Pulse (B = 4Hz)':
        # Sinc(2*B*t) has a brickwall spectrum between -B and +B
        return np.sinc(8 * t), 4.0
    elif sig_type == 'Gaussian Pulse':
        # Gaussian e^(-a*t^2) is smooth in both domains
        sigma = 0.08
        return np.exp(-t**2 / (2 * sigma**2)), 6.0

# ==========================================
# 2. COMPUTATION ENGINE (ROADS 1 & 2)
# ==========================================
def compute_all(x, fs):
    T_s = 1.0 / fs
    
    # 1. CTFT of x(t) [Scaled FFT approximates continuous integral]
    X_f = np.fft.fftshift(np.fft.fft(x)) * dt
    
    # 2. Impulse train p(t) on dense grid
    # A Dirac delta on a grid of spacing dt has discrete height 1/dt
    p = np.zeros_like(t)
    sample_indices = np.arange(0, N_DENSE, int(round(FS_DENSE / fs)))
    # Center sample indices symmetrically around t=0
    center_idx = N_DENSE // 2
    offset = center_idx % int(round(FS_DENSE / fs))
    sample_indices = np.arange(offset, N_DENSE, int(round(FS_DENSE / fs)))
    p[sample_indices] = 1.0 / dt
    
    # Spectrum P(f) of the impulse train
    P_f = np.fft.fftshift(np.fft.fft(p)) * dt
    
    # 3. Sampled signal x_p(t) = x(t) * p(t)
    x_p = x * p
    
    # --- ROAD 1: Direct Transform of x_p(t) ---
    Xp_road1 = np.fft.fftshift(np.fft.fft(x_p)) * dt
    
    # --- ROAD 2: Frequency Convolution: (1 / 2pi) * X * P ---
    # In regular frequency f (Hz): (X * P)(f) = \int X(\lambda) P(f - \lambda) d\lambda
    # We evaluate this using circular/linear FFT convolution
    X_uncentered = np.fft.fft(x) * dt
    P_uncentered = np.fft.fft(p) * dt
    # Convolution in time-domain is multiplication in frequency; here it is dual:
    conv_freq = np.fft.fftshift(np.fft.ifft(np.fft.fft(X_f) * np.fft.fft(P_f))) * df
    
    return X_f, p, P_f, x_p, Xp_road1, conv_freq, sample_indices

# ==========================================
# 3. INTERACTIVE VISUALIZATION
# ==========================================
fig, axs = plt.subplots(3, 2, figsize=(14, 9))
plt.subplots_adjust(left=0.25, bottom=0.12, hspace=0.45, wspace=0.25)
fig.canvas.manager.set_window_title('Impulse Sampling & Fourier Commutative Diagram')

initial_signal_type = 'Two Tones (3Hz + 7Hz)'
initial_fs = 18.0  # Above Nyquist (14 Hz)

x, f_max = get_signal(initial_signal_type)
X_f, p, P_f, x_p, Xp_r1, Xp_r2, sample_idx = compute_all(x, initial_fs)

# Setup subplots
# (Row 1, Col 1): Continuous x(t)
line_x, = axs[0, 0].plot(t, x, 'b-', lw=1.5)
axs[0, 0].set_title(r"$\mathbf{1.\; Continuous\; Signal}\; x(t)$", fontsize=11)
axs[0, 0].set_xlim(-0.6, 0.6)
axs[0, 0].grid(True, alpha=0.3)
axs[0, 0].set_ylabel("Amplitude")

# (Row 1, Col 2): Spectrum X(f)
line_X, = axs[0, 1].plot(freqs, np.abs(X_f), 'b-', lw=1.5)
axs[0, 1].set_title(r"$\mathbf{Spectrum}\; X(j\omega)$", fontsize=11)
axs[0, 1].set_xlim(-25, 25)
axs[0, 1].grid(True, alpha=0.3)

# (Row 2, Col 1): Impulse Train p(t)
markerline, stemlines, baseline = axs[1, 0].stem(
    t[sample_idx], np.ones(len(sample_idx)), 
    linefmt='m-', markerfmt='m^', basefmt='k-'
)
stemlines.set_linewidth(1.2)
markerline.set_markersize(5)
axs[1, 0].set_title(r"$\mathbf{2.\; Impulse\; Train}\; p(t) = \sum \delta(t - nT)$", fontsize=11)
axs[1, 0].set_xlim(-0.6, 0.6)
axs[1, 0].set_ylim(0, 1.3)
axs[1, 0].grid(True, alpha=0.3)
axs[1, 0].set_ylabel("Impulse Area = 1")

# (Row 2, Col 2): Spectrum P(f)
line_P, = axs[1, 1].plot(freqs, np.abs(P_f), color='purple', lw=1.2)
axs[1, 1].set_title(r"$\mathbf{Impulse\; Spectrum}\; P(j\omega) = \omega_s \sum \delta(\omega - k\omega_s)$", fontsize=11)
axs[1, 1].set_xlim(-25, 25)
axs[1, 1].grid(True, alpha=0.3)

# (Row 3, Col 1): Sampled Signal x_p(t)
axs[2, 0].plot(t, x, 'b--', alpha=0.3, label='Envelope $x(t)$')
markerline_xp, stemlines_xp, _ = axs[2, 0].stem(
    t[sample_idx], x[sample_idx], 
    linefmt='r-', markerfmt='ro', basefmt='k-'
)
markerline_xp.set_markersize(4)
axs[2, 0].set_title(r"$\mathbf{3.\; Sampled\; Signal}\; x_p(t) = x(t) \cdot p(t)$", fontsize=11)
axs[2, 0].set_xlim(-0.6, 0.6)
axs[2, 0].grid(True, alpha=0.3)
axs[2, 0].set_ylabel("Sample Weight")
axs[2, 0].set_xlabel("Time (s)")

# (Row 3, Col 2): Sampled Spectrum (Road 1 vs Road 2)
line_xp_r1, = axs[2, 1].plot(freqs, np.abs(Xp_r1), 'r-', lw=2.0, label='Road 1: FFT of $x_p(t)$')
line_xp_r2, = axs[2, 1].plot(freqs, np.abs(Xp_r2), 'k--', lw=1.2, label='Road 2: Conv $X * P$')
axs[2, 1].set_title(r"$\mathbf{Sampled\; Spectrum}\; X_p(j\omega)\; \mathrm{[Replicas\; at\;} k\cdot f_s]$", fontsize=11)
axs[2, 1].set_xlim(-25, 25)
axs[2, 1].grid(True, alpha=0.3)
axs[2, 1].set_xlabel("Frequency (Hz)")
axs[2, 1].legend(loc='upper right', fontsize=8)

# Aliasing status text
status_text = fig.text(
    0.60, 0.03, "", fontsize=11, fontweight='bold', 
    bbox=dict(boxstyle='round,pad=0.5', facecolor='white', edgecolor='gray')
)

# ==========================================
# 4. GUI CONTROLS
# ==========================================
ax_radio = plt.axes([0.03, 0.55, 0.17, 0.25])
radio = RadioButtons(ax_radio, ('Two Tones (3Hz + 7Hz)', 'Sinc Pulse (B = 4Hz)', 'Gaussian Pulse'))

ax_slider = plt.axes([0.30, 0.03, 0.25, 0.03])
slider_fs = Slider(ax_slider, '$f_s$ (Hz)', 4.0, 30.0, valinit=initial_fs, valstep=0.5)

def update(val=None):
    sig_name = radio.value_selected
    current_fs = slider_fs.val
    
    x_curr, f_max_curr = get_signal(sig_name)
    X_f_c, p_c, P_f_c, x_p_c, Xp_r1_c, Xp_r2_c, s_idx = compute_all(x_curr, current_fs)
    
    # Update continuous signal & spectrum
    line_x.set_ydata(x_curr)
    line_X.set_ydata(np.abs(X_f_c))
    axs[0, 1].set_ylim(0, np.max(np.abs(X_f_c)) * 1.25)
    
    # Update impulse train stems
    markerline.set_xdata(t[s_idx])
    markerline.set_ydata(np.ones(len(s_idx)))
    stemlines.set_paths([np.array([[ti, 0], [ti, 1]]) for ti in t[s_idx]])
    
    # Update impulse spectrum
    line_P.set_ydata(np.abs(P_f_c))
    axs[1, 1].set_ylim(0, np.max(np.abs(P_f_c)) * 1.25)
    
    # Update sampled stems
    markerline_xp.set_xdata(t[s_idx])
    markerline_xp.set_ydata(x_curr[s_idx])
    stemlines_xp.set_paths([np.array([[ti, 0], [ti, xi]]) for ti, xi in zip(t[s_idx], x_curr[s_idx])])
    axs[2, 0].set_ylim(np.min(x_curr) * 1.2, np.max(x_curr) * 1.2)
    
    # Update sampled spectrum (both roads)
    line_xp_r1.set_ydata(np.abs(Xp_r1_c))
    line_xp_r2.set_ydata(np.abs(Xp_r2_c))
    max_spec = max(np.max(np.abs(Xp_r1_c)), np.max(np.abs(Xp_r2_c)))
    axs[2, 1].set_ylim(0, max_spec * 1.25)
    
    # Update aliasing feedback
    nyquist_rate = 2 * f_max_curr
    if current_fs >= nyquist_rate:
        status_text.set_text(f"No Aliasing: fs ({current_fs:.1f} Hz) ≥ 2×f_max ({nyquist_rate:.1f} Hz)")
        status_text.set_color("forestgreen")
    else:
        status_text.set_text(f"ALIASING OCCURRING! fs ({current_fs:.1f} Hz) < 2×f_max ({nyquist_rate:.1f} Hz)")
        status_text.set_color("crimson")
        
    fig.canvas.draw_idle()

radio.on_clicked(update)
slider_fs.on_changed(update)

update()
plt.show()