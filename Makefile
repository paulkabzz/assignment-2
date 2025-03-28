# Compiler
JAVAC = javac
JAVA = java
JAVADOC = javadoc

# Directories
SRC_DIR = src
BIN_DIR = bin
DOCS_DIR = docs
TEST_SCRIPT = test.sh

# Find all Java files
SRC_FILES = $(shell find $(SRC_DIR) -name "*.java")

# Convert source paths to class file paths
CLASS_FILES = $(patsubst $(SRC_DIR)/%.java, $(BIN_DIR)/%.class, $(SRC_FILES))

# Main class
MAIN_CLASS = Main

# Default target: compile
all: $(CLASS_FILES)

# Compile Java files
$(BIN_DIR)/%.class: $(SRC_DIR)/%.java
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) $(SRC_FILES)

# Generate Javadoc
javadoc:
	@mkdir -p $(DOCS_DIR)
	$(JAVADOC) -d $(DOCS_DIR) -sourcepath $(SRC_DIR) -private $(SRC_FILES)

# Run the program
run: all
	$(JAVA) -cp $(BIN_DIR) $(MAIN_CLASS)

# Make test script executable if it isn't already
prepare-test:
	@if [ ! -x $(TEST_SCRIPT) ]; then chmod +x $(TEST_SCRIPT); fi

# Run tests
test: all prepare-test
	./$(TEST_SCRIPT)

# Clean compiled files and documentation
clean:
	rm -rf $(BIN_DIR) $(DOCS_DIR)

# Phony targets
# Run the Python plotting script
plot: all
	@if command -v python3 >/dev/null 2>&1; then \
		python3 $(SRC_DIR)/index.py; \
	elif command -v python >/dev/null 2>&1; then \
		python $(SRC_DIR)/index.py; \
	else \
		echo "Python not found. Please install Python to run the plotting script."; \
		exit 1; \
	fi

# Add plot to the phony targets
.PHONY: all run clean javadoc test prepare-test plot