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
        # Prepare Java command
        java_command = [
            'make', 
            'run',
        ]
        
        # Set environment variables to pass subset file
        env = os.environ.copy()
        env['INPUT_FILE'] = subset_file
        
        # Run the Java application
        try:
            # Capture output
            result = subprocess.run(
                java_command, 
                capture_output=True, 
                text=True,
                env=env
            )
            
            # Parse output
            lines = result.stdout.split('\n')
            insert_count = int(lines[-2].split(':')[1].strip())
            search_count = int(lines[-1].split(':')[1].strip())
            
            return insert_count, search_count
        
        except Exception as e:
            print(f"Error running experiment: {e}")
            return None, None
    
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
            
            for _ in range(num_runs):
                # Generate subset
                subset_file = self.generate_subset(size)
                
                # Run experiment
                insert_count, search_count = self.run_java_experiment(subset_file)
                
                # Store results
                if insert_count is not None:
                    self.insert_counts[size].append(insert_count)
                    self.search_counts[size].append(search_count)
                
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