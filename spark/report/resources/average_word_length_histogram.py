import matplotlib.pyplot as plt
import matplotlib
import numpy as np

data = [ 7.19, 7.11, 7.01, 7.01, 6.87, 6.37, 5.98, 5.93, 5.8, 5.74, 5.61, 5.59, 5.37, 5.34, 5.17, 5.14, 4.81, 4.54, 4.28, 3.81, 3.74, 3.72, 3.64, 3.46, 3.41, 3.01]

letters = ['c', 'e', 'q', 'p', 'r', 'd', 'v', 'g', 's', 'z', 'u', 'j', 'k', 'l', 'f', 'm', 'n', 'b', 'w', 'h', 'a', 'y', 't', 'i', 'x', 'o']

font = {'family' : 'normal',
        'weight' : 'normal',
        'size'   : 30}

matplotlib.rc('font', **font)

plt.figure(figsize=(30, 10))
plt.bar(letters, data, color='orange', alpha=0.7)
plt.title('Word length Histogram')
plt.xlabel('Letter')
plt.ylabel('Average word length')
plt.axhline(y = np.mean(data), color = 'r', linestyle = '-.', label="Mean average word length")
plt.legend()
plt.show()