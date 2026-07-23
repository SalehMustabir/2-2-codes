import numpy as np
import matplotlib.pyplot as plt

DT = 0.05 # sampling interval for the time axis
T_MIN, T_MAX = -np.pi, np.pi # x(t) is defined only on this range

def generate_time_axis(t_min=T_MIN, t_max=T_MAX, dt=DT):
    return np.arange(t_min, t_max + dt / 2, dt)


def base_signal(t):
    x = np.sin(t)
    x[(t < T_MIN) | (t > T_MAX)] = 0
    return x

def interpolate_signal(t, x, query_t):
    
    # TODO: implement interpolation
    y = []
    t = np.array(t)
    for tq in query_t:
        if tq < t[0] or tq > t[-1]:
            y.append(0)
            continue
        idx = t.searchsorted(tq)
        if(t[idx] == tq):
            y.append(x[idx])
        else:
            l_idx = idx - 1
            r_idx = idx
            val = x[l_idx] + x[r_idx]
            val /= 2
            y.append(val)

    return np.array(y)


    
    

def transform_signal(t, x, alpha, beta):
    
    # TODO: implement transformation
    tq = alpha*t + beta
    y = interpolate_signal(t,x,tq)
    
    
    return y

def plot_signals(t, x, y, alpha, beta):
    plt.figure(figsize=(9, 5))
    plt.plot(t, x, label="x(t)", linewidth=2)
    plt.plot(t, y, label=f"y(t) = x({alpha}t + {beta})", linewidth=2, linestyle="--")
    plt.title("Time Scaling and Shifting of a Signal")
    plt.xlabel("t")
    plt.ylabel("Amplitude")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.show()

def main():
    t = generate_time_axis()
    x = base_signal(t)

    print("Enter alpha and beta to plot y(t) = x(alpha*t + beta).")
    print("Type 'q' at any prompt to quit.\n")

    while True:
        
        # TODO: complete the loop
        alpha = input("enter alpha")
        if(alpha == "q"):
            break
        beta = input("enter Beta")
        if(beta == "q"):
            break
        alpha = float(alpha)
        beta = float(beta)
        y = transform_signal(t,x,alpha,beta)
        plot_signals(t,x,y,alpha,beta)

    print("Exiting.")


if __name__ == "__main__":
    main()