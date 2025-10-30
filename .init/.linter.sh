#!/bin/bash
cd /home/kavia/workspace/code-generation/usp-device-control-platform-537-546/usp_controller_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

