import os
import random
import subprocess
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import argparse

class AVLTreeExperiment:
    def __init__(self, input_file=None, query_file=None):
        """
        Initialize the experiment with input and query files
        
        :param input_file: Path to the original knowledge base file
        :param query_file: Path to the query terms file
        """
        # If files not provided, prompt for them
        if input_file is None:
            input_file = input("Enter knowledge base file path (or press Enter for default 'src/GenericsKB.txt'): ")
            if not input_file:
                input_file = "src/GenericsKB.txt"
        
        if query_file is None:
            query_file = input("Enter query file path (or press Enter for default 'src/GenericsKB-queries.txt'): ")
            if not query_file:
                query_file = "src/GenericsKB-queries.txt"
        
        self.input_file = input_file
        self.query_file = query_file
        
        print(f"Using knowledge base file: {self.input_file}")
        print(f"Using query file: {self.query_file}")
        
        # Read full dataset
        self.full_dataset = self.read_dataset()
        
        # Experiment parameters
        self.dataset_sizes = [
            1, 5, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 7500, 
            10000, 20000, 25000, 30000, 35000, 40000, 45000, 50000
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
            'Main',
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
        plt.plot(results_df['Size'], results_df['Insert_Avg'], 'o-', label='Average')
        plt.plot(results_df['Size'], results_df['Insert_Min'], 's-', label='Minimum')
        plt.plot(results_df['Size'], results_df['Insert_Max'], '^-', label='Maximum')
        
        # Add theoretical n*log(n) line for comparison
        sizes = results_df['Size']
        n_log_n = [n * np.log2(n) for n in sizes]
        plt.plot(sizes, n_log_n, '--', label='n*log₂(n)')
        
        plt.xlabel('Dataset Size')
        plt.ylabel('Comparison Count')
        plt.xscale('log')
        plt.yscale('log')  # Use log-log scale to better see the relationship
        plt.legend()
        plt.grid(True)
        
        # Search Comparison Counts
        plt.subplot(1, 2, 2)
        plt.title('Search Comparison Counts')
        plt.plot(results_df['Size'], results_df['Search_Avg'], 'o-', label='Average')
        plt.plot(results_df['Size'], results_df['Search_Min'], 's-', label='Minimum')
        plt.plot(results_df['Size'], results_df['Search_Max'], '^-', label='Maximum')
        
        # Add theoretical log(n) line for comparison
        log_n = [np.log2(n) for n in sizes]
        plt.plot(sizes, log_n, '--', label='log₂(n)')
        
        plt.xlabel('Dataset Size')
        plt.ylabel('Comparison Count')
        plt.xscale('log')
        plt.legend()
        plt.grid(True)
        
        plt.tight_layout()
        plt.savefig('avl_tree_performance.png')
        plt.close()
        
        # Also create a theoretical comparison plot
        plt.figure(figsize=(10, 5))
        
        # Calculate theoretical log(n) values
        sizes = results_df['Size']
        log_n = [np.log2(n) for n in sizes]
        n_log_n = [n * np.log2(n) for n in sizes]
        
        plt.subplot(1, 2, 1)
        plt.title('Search: Actual vs Theoretical log(n)')
        plt.plot(sizes, results_df['Search_Avg'], 'o-', label='Actual')
        plt.plot(sizes, log_n, 's-', label='log₂(n)')
        plt.xlabel('Dataset Size')
        plt.ylabel('Comparison Count')
        plt.xscale('log')
        plt.legend()
        plt.grid(True)
        
        plt.subplot(1, 2, 2)
        plt.title('Insert: Actual vs Theoretical n·log(n)')
        plt.plot(sizes, results_df['Insert_Avg'], 'o-', label='Actual')
        plt.plot(sizes, n_log_n, 's-', label='n·log₂(n)')
        plt.xlabel('Dataset Size')
        plt.ylabel('Comparison Count')
        plt.xscale('log')
        plt.yscale('log')
        plt.legend()
        plt.grid(True)
        
        plt.tight_layout()
        plt.savefig('avl_tree_theoretical.png', dpi=300)
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
    # Set up argument parser
    parser = argparse.ArgumentParser(description='Run AVL Tree performance experiments')
    parser.add_argument('--input', '-i', help='Path to knowledge base file')
    parser.add_argument('--query', '-q', help='Path to query file')
    
    args = parser.parse_args()
    
    # Create experiment with command line arguments or prompt for input
    experiment = AVLTreeExperiment(
        input_file=args.input,
        query_file=args.query
    )
    experiment.run()