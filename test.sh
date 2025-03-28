#!/bin/bash

# Compile the Java code
echo "Compiling Java code..."
make

# Run the application with test files
echo "Running application with test files..."
# Redirect both stdout and stderr to test.output
java -cp bin Main src/GenericsKB.txt src/test-query.txt > test.output 2>&1

echo "Test completed. Results saved to test.output"
# Display the first few lines of the output file
echo "Preview of test.output:"
head -n 10 test.output