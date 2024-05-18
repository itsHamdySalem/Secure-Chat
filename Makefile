# Define variables
JAVAC=javac
JAVA=java
SRC_DIR=src
BIN_DIR=bin

# Find all Java source files
SOURCES=$(shell find $(SRC_DIR) -name "*.java")

# Default target to build everything
build: $(BIN_DIR)/all_classes

# Rule to compile all .java files
$(BIN_DIR)/all_classes: $(SOURCES)
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) $(SOURCES)
	@touch $(BIN_DIR)/all_classes

# Clean up .class files
clean:
	rm -rf $(BIN_DIR)

# Run the server
run-server: build
	$(JAVA) -cp $(BIN_DIR) socket.Server

# Run the client
run-client: build
	$(JAVA) -cp $(BIN_DIR) socket.Client

.PHONY: build clean run-server run-client
