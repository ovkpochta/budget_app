#!/usr/bin/env sh

# Gradle wrapper script

# Exit on error
set -e

# The directory containing this script
GRADLE_DIR=$(cd "$(dirname "$0")/.." && pwd)
# The Gradle executable to use
GRADLE_EXECUTABLE="${GRADLE_DIR}/gradle" 

# Check if the Gradle executable exists
if [ ! -x "$GRADLE_EXECUTABLE" ]; then
    echo "Gradle executable does not exist: $GRADLE_EXECUTABLE"
    exit 1
fi

# Run the Gradle command with any additional arguments
exec "$GRADLE_EXECUTABLE" "$@"