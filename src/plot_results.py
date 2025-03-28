import csv
import matplotlib.pyplot as plt
import numpy as np

# Function to read CSV without pandas
def read_csv(filename):
    data = {'Size': [], 'InsertMin': [], 'InsertAvg': [], 'InsertMax': [], 
            'SearchMin': [], 'SearchAvg': [], 'SearchMax': []}
    
    with open(filename, 'r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            for key in data:
                if key in row:
                    data[key].append(float(row[key]))
    
    return data

try:
    # Read the CSV file
    data = read_csv('experiment_results.csv')
    
    # Create a figure with two subplots
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(15, 6))
    
    # Plot insert operations
    ax1.plot(data['Size'], data['InsertMin'], 'g-', label='Best Case')
    ax1.plot(data['Size'], data['InsertAvg'], 'b-', label='Average Case')
    ax1.plot(data['Size'], data['InsertMax'], 'r-', label='Worst Case')
    
    # Plot theoretical O(log n) for comparison
    x = np.array(data['Size'])
    ax1.plot(x, np.log2(x), 'k--', label='O(log n)')
    
    ax1.set_title('AVL Tree Insert Operations')
    ax1.set_xlabel('Dataset Size (n)')
    ax1.set_ylabel('Number of Comparisons')
    ax1.set_xscale('log')
    ax1.legend()
    ax1.grid(True)
    
    # Plot search operations
    ax2.plot(data['Size'], data['SearchMin'], 'g-', label='Best Case')
    ax2.plot(data['Size'], data['SearchAvg'], 'b-', label='Average Case')
    ax2.plot(data['Size'], data['SearchMax'], 'r-', label='Worst Case')
    
    # Plot theoretical O(log n) for comparison
    ax2.plot(x, np.log2(x), 'k--', label='O(log n)')
    
    ax2.set_title('AVL Tree Search Operations')
    ax2.set_xlabel('Dataset Size (n)')
    ax2.set_ylabel('Number of Comparisons')
    ax2.set_xscale('log')
    ax2.legend()
    ax2.grid(True)
    
    plt.tight_layout()
    plt.savefig('avl_tree_performance.png')
    plt.show()
    
except Exception as e:
    print(f"Error running the script: {e}")