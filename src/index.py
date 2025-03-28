import os
import random
import subprocess
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd

class AVLTreeExperiment:
    def __init__(self, input_file, query_file):
        """
        Initialize the experiment with input and query files
        
        :param input_file: Path to the original knowledge base file
        :param query_file: Path to the query terms file
        """
        self.input_file = input_file
        self.query_file = query_file
        
        # Read full dataset
        self.full_dataset = self.read_dataset()
        
        # Experiment parameters
        self.dataset_sizes = [
            5, 50, 500, 1000, 2000, 5000, 
            10000, 20000, 35000, 50000
        ]
        
        # Results storage
        self.insert_counts = []
        self.search_counts = []
    
    def read_dataset(self):
        """
        Read the entire dataset from the input file
        
        :return: List of all data entries
        """
        with open(self.input_file, 'r') as f:
            return [line.strip() for line in f]
    
    def generate_subset(self, size):
        """
        Generate a randomized subset of the dataset
        
        :param size: Number of entries to include in subset
        :return: Subset of dataset
        """
        # Ensure we don't try to sample more than available
        size = min(size, len(self.full_dataset))
        
        # Randomly sample entries
        subset = random.sample(self.full_dataset, size)
        
        # Write subset to a temporary file
        with open('temp_subset.txt', 'w') as f:
            for entry in subset:
                f.write(entry + '\n')
        
        return 'temp_subset.txt'
    
    def run_java_experiment(self, subset_file):
        """
        Run the Java application and capture comparison counts
        
        :param subset_file: Path to the subset file
        :return: Tuple of (insert_count, search_count)
        """
        # Get absolute paths to ensure Java can find the files
        abs_subset_path = os.path.abspath(subset_file)
        abs_query_path = os.path.abspath(self.query_file)
        
        # Prepare Java command with the subset file as first argument
        java_command = [
            'java', 
            '-cp', 
            'bin', 
            'GenericsKbAVLApp',
            abs_subset_path,  # Pass subset file as first argument
            abs_query_path    # Pass query file as second argument
        ]
        
        print(f"Running command: {' '.join(java_command)}")
        
        # Run the Java application
        try:
            # Capture output
            result = subprocess.run(
                java_command, 
                capture_output=True, 
                text=True
            )
            
            # Print any errors for debugging
            if result.stderr:
                print(f"Java stderr: {result.stderr}")
            
            # Parse output
            insert_count = None
            search_count = None
            
            for line in result.stdout.split('\n'):
                if "Insert Comparison Count:" in line:
                    insert_count = int(line.split(':')[1].strip())
                elif "Search Comparison Count:" in line:
                    search_count = int(line.split(':')[1].strip())
            
            if insert_count is None or search_count is None:
                print(f"Could not find comparison counts in output. Full output:\n{result.stdout}")
                return None, None
                
            return insert_count, search_count
        
        except Exception as e:
            print(f"Error running experiment: {e}")
            return None, None
    
    # Add this to your run_experiments method
    def run_experiments(self, num_runs=5):
        """
        Conduct experiments for different dataset sizes
        
        :param num_runs: Number of times to repeat each experiment
        """
        # Reset results
        self.insert_counts = {size: [] for size in self.dataset_sizes}
        self.search_counts = {size: [] for size in self.dataset_sizes}
        
        # Run experiments for each dataset size
        for size in self.dataset_sizes:
            print(f"Running experiments for dataset size: {size}")
            
            for run in range(num_runs):
                print(f"  Run {run+1}/{num_runs}")
                
                # Generate subset
                subset_file = self.generate_subset(size)
                print(f"  Created subset file: {subset_file} with {size} entries")
                
                # Run experiment
                insert_count, search_count = self.run_java_experiment(subset_file)
                
                # Store results
                if insert_count is not None:
                    self.insert_counts[size].append(insert_count)
                    self.search_counts[size].append(search_count)
                    print(f"  Results: Insert={insert_count}, Search={search_count}")
                else:
                    print(f"  Failed to get results for run {run+1}")
                
                # Clean up temporary file
                os.remove(subset_file)
    
    def analyze_results(self):
        """
        Analyze and compute statistics for the experimental results
        
        :return: DataFrame with experimental results
        """
        results = []
        for size in self.dataset_sizes:
            results.append({
                'Size': size,
                'Insert_Min': min(self.insert_counts[size]),
                'Insert_Max': max(self.insert_counts[size]),
                'Insert_Avg': np.mean(self.insert_counts[size]),
                'Search_Min': min(self.search_counts[size]),
                'Search_Max': max(self.search_counts[size]),
                'Search_Avg': np.mean(self.search_counts[size])
            })
        
        return pd.DataFrame(results)
    
    def plot_results(self, results_df):
        """
        Create visualizations of experimental results
        
        :param results_df: DataFrame with experimental results
        """
        plt.figure(figsize=(12, 5))
        
        # Insertion Comparison Counts
        plt.subplot(1, 2, 1)
        plt.title('Insertion Comparison Counts')
        plt.plot(results_df['Size'], results_df['Insert_Avg'], label='Average')
        plt.plot(results_df['Size'], results_df['Insert_Min'], label='Minimum')
        plt.plot(results_df['Size'], results_df['Insert_Max'], label='Maximum')
        plt.xlabel('Dataset Size')
        plt.ylabel('Comparison Count')
        plt.xscale('log')
        plt.legend()
        plt.grid(True)
        
        # Search Comparison Counts
        plt.subplot(1, 2, 2)
        plt.title('Search Comparison Counts')
        plt.plot(results_df['Size'], results_df['Search_Avg'], label='Average')
        plt.plot(results_df['Size'], results_df['Search_Min'], label='Minimum')
        plt.plot(results_df['Size'], results_df['Search_Max'], label='Maximum')
        plt.xlabel('Dataset Size')
        plt.ylabel('Comparison Count')
        plt.xscale('log')
        plt.legend()
        plt.grid(True)
        
        plt.tight_layout()
        plt.savefig('avl_tree_performance.png')
        plt.close()
    
    def run(self):
        """
        Execute the entire experiment workflow
        """
        # Run experiments
        self.run_experiments()
        
        # Analyze results
        results_df = self.analyze_results()
        
        # Plot results
        self.plot_results(results_df)
        
        # Save results to CSV
        results_df.to_csv('avl_tree_performance.csv', index=False)
        
        print("Experiment completed. Results saved to avl_tree_performance.csv and avl_tree_performance.png")

# Main execution
if __name__ == '__main__':
    experiment = AVLTreeExperiment(
        input_file='src/GenericsKB.txt',
        query_file='src/GenericsKB-queries.txt'
    )
    experiment.run()