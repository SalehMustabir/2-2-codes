import numpy as np
import matplotlib.pyplot as plt

# ----------------------------
# Time axis
# ----------------------------
T_MIN, T_MAX, N = -4.0, 4.0, 4001


def x_of_t(t: np.ndarray) -> np.ndarray:
    """
    Base signal x(t): sinusoidal signal
    """
    return (
        np.sin(2 * np.pi * 0.5 * t)
        + 0.5 * np.sin(2 * np.pi * 1.5 * t)
    )


# ==========================================================
# ANSWER IMPLEMENTATION
# ==========================================================

def interpolate_signal(
    t_original: np.ndarray,
    x_original: np.ndarray,
    t_query: np.ndarray
) -> np.ndarray:
    """
    Interpolate using average of two neighboring samples.
    """

    #signal y
    y = []
    for tq in t_query:
        if tq < t_original[0] or tq > t_original[-1]:
            y.append(0)
            continue
        idx = t_original.searchsorted(tq)

        if t_original[idx] == tq:
            y.append(x_original[idx])
        else:
            l_idx = idx - 1
            r_idx = idx + 1
            i_val = x_original[l_idx] + x_original[r_idx]
            i_val /= 2
            y.append(i_val)


    return y    

        

    raise NotImplementedError


def time_scale(
    t: np.ndarray,
    x: np.ndarray,
    k: int
) -> np.ndarray:
    """
    Time sub-scaling:
        y(t) = x(t / k)
    """
    tq = t/k
    y = interpolate_signal(t,x,tq)


    return y
    
    raise NotImplementedError


def plot_pair(t: np.ndarray, x: np.ndarray, y: np.ndarray, title: str):
    """
    Plot graphs.
    """
    plt.figure(figsize=(10,5))
    plt.plot(t,x, label = 'x(t)', color = 'blue')
    plt.plot(t,y, label = 'x(t/k)', color = 'red')
    plt.title(title)

    plt.xlabel("Time")

    plt.ylabel("Amplitude")

    plt.grid(True)

    plt.legend()

    plt.show()

    raise NotImplementedError


# ----------------------------
# Main
# ----------------------------
def main():
    t = np.linspace(T_MIN, T_MAX, N)
    x = x_of_t(t)

    k = 2   # sub-scaling factor
    y = time_scale(t, x, k)

    plot_pair(
        t,
        x,
        y,
        title=f"Time Sub-scaling: y(t) = x(t / {k})"
    )
    plt.show()


if __name__ == "__main__":
    main()
