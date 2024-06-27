#!/bin/bash

# Function to check if Maven is installed
check_maven() {
    if ! command -v mvn &> /dev/null; then
        echo "Maven is not installed. Installing Maven..."
        # Install Maven
        sudo apt update
        sudo apt install maven -y
        echo "Maven installed successfully."
    fi
}

# Check if Maven is installed
check_maven

# Clean the project
echo "Cleaning project..."
mvn clean

# Install dependencies
echo "Installing dependencies..."
mvn install

# Build the project
echo "Building project..."
mvn package

echo "Done."
