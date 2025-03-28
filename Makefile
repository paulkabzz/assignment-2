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
MAIN_CLASS = GenericsKbAVLApp

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
.PHONY: all run clean javadoc test prepare-test