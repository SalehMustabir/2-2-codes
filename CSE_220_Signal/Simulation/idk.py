import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation

# 1. Define the signal parameters matching the image
t = np.linspace(0, 1, 1000)
w0 = 2 * np.pi  # Fundamental frequency (Period T0 = 1)
# x(t) = 1 + 0.8*cos(w0*t) + 0.5*cos(2*w0*t)
x = 1 + 0.8 * np.cos(w0 * t) + 0.5 * np.cos(2 * w0 * t)

# 2. Setup the figure and axes
fig = plt.figure(figsize=(12, 5))
fig.canvas.manager.set_window_title("Fourier Series Winding Animation")

# Left plot: Original Signal x(t)
ax1 = fig.add_subplot(121)
ax1.plot(t, x, color='#4A6984', linewidth=2)
ax1.set_title("Original Signal $x(t)$")
ax1.set_xlabel("Time (t)")
ax1.set_ylabel("Amplitude")
ax1.grid(True, alpha=0.3)
ax1.set_ylim(-0.5, 2.5)

# Right plot: Wound Signal and Center of Mass
ax2 = fig.add_subplot(122)
ax2.set_xlim(-1.5, 2.5)
ax2.set_ylim(-2, 2)
ax2.set_aspect('equal')
ax2.set_title("Wound Signal in Complex Plane")
ax2.axhline(0, color='black', linewidth=0.5, alpha=0.5)
ax2.axvline(0, color='black', linewidth=0.5, alpha=0.5)

# Line objects to update during animation
wound_line, = ax2.plot([], [], color='#4A6984', linewidth=1.5, alpha=0.8)
com_dot, = ax2.plot([], [], 'ro', markersize=8, label='Center of Mass ($c_k$)')
com_text = ax2.text(0.05, 0.9, '', transform=ax2.transAxes)
ax2.legend(loc='lower right')

# 3. Animation Update Function
def update(frame):
    # 'frame' acts as the continuous winding frequency 'k' (sweeping from 0 to 3)
    k = frame
    
    # Wrap the signal around the origin: x(t) * e^(-j * k * w0 * t)
    wound_signal = x * np.exp(-1j * k * w0 * t)
    
    # Calculate Center of Mass (integral over one period, divided by T0)
    com = np.mean(wound_signal)
    
    # Update the wound curve drawing
    wound_line.set_data(np.real(wound_signal), np.imag(wound_signal))
    
    # Update the center of mass dot
    com_dot.set_data([np.real(com)], [np.imag(com)])
    
    # Update text
    com_text.set_text(f"Winding Frequency (k): {k:.2f}\nCentroid (c_k): {np.real(com):.2f} + {np.imag(com):.2f}j")
    
    return wound_line, com_dot, com_text

# 4. Run Animation (sweep k from 0 to 3.2 over 400 frames)
frames = np.linspace(0, 3.2, 400)
ani = animation.FuncAnimation(fig, update, frames=frames, interval=30, blit=True)

plt.tight_layout()
plt.show()