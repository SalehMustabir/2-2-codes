import numpy as np
y = np.array([1,3,5,7,9,11])
x = y.searchsorted(4)
y = np.array(y)
z = np.searchsorted(y,4)
print(x,z)


