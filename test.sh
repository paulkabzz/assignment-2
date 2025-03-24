#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== GenericsKB Testing Framework ===${NC}\n"

# Test data
TEST_TERMS=("dog" "cat" "tree" "cellular mechanism" "maple syrup")
TEST_SENTENCES=("Dogs bark" "Cats meow" "Trees grow tall" "Cellular mechanism is complex" "Maple syrup is sweet")
TEST_SCORES=(0.9 0.8 0.95 0.85 0.75)

run_tests() {
    implementation=$1
    impl_choice=$2
    successful_searches=0
    successful_inserts=0

    echo -e "\n${BLUE}=== Testing $implementation Implementation ===${NC}\n"
    echo -e "${BLUE}Running $implementation tests...${NC}\n"

    # Create input file
    {
        # Choose implementation
        echo "$impl_choice"
        
        # Load knowledge base
        echo "1"
        echo "GenericsKB.txt"
        
        # Add test statements
        for i in "${!TEST_TERMS[@]}"; do
            echo "2"
            echo "${TEST_TERMS[$i]}"
            echo "${TEST_SENTENCES[$i]}"
            echo "${TEST_SCORES[$i]}"
            echo ""  # Enter to continue
        done

        # Search for terms
        for term in "${TEST_TERMS[@]}"; do
            echo "3"
            echo "$term"
            echo ""  # Enter to continue
        done

        # Exit
        if [ "$implementation" == "BST" ]; then
            echo "6"
        else
            echo "5"
        fi
    } > input.txt

    # Run the program
    make run < input.txt > output.txt 2>&1

    # Check results
    while IFS= read -r line; do
        if [[ $line == *"has been updated"* ]]; then
            ((successful_inserts++))
            echo -e "${GREEN}Insert:${NC} $line"
        fi
        
        # Capture full statement output
        if [[ $line == *"Statement found:"* ]]; then
            ((successful_searches++))
            echo -e "\n${GREEN}Found:${NC} $line"
            # Read the next 3 lines which contain term, statement, and confidence
            read -r term_line
            read -r statement_line
            read -r confidence_line
            echo -e "${YELLOW}Term:${NC} ${term_line#*: }"
            echo -e "${YELLOW}Statement:${NC} ${statement_line#*: }"
            echo -e "${YELLOW}Confidence:${NC} ${confidence_line#*: }"
        fi
    done < output.txt

    # Print results
    echo -e "\n${BLUE}=== Test Results for $implementation Implementation ===${NC}"
    echo -e "Successful searches: ${GREEN}$successful_searches${NC} / ${#TEST_TERMS[@]}"
    echo -e "Successful inserts: ${GREEN}$successful_inserts${NC} / ${#TEST_TERMS[@]}"

    # Clean up
    rm input.txt output.txt
}

# Run tests for both implementations
run_tests "Array" 1
echo -e "\n${BLUE}Press Enter to continue with BST tests...${NC}"
read

run_tests "BST" 2

echo -e "\n${BLUE}=== Testing Complete ===${NC}" 