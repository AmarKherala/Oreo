#!/bin/bash


BOT_DIR="$HOME/Oreo"
SCREEN_NAME="mojava"

function ensure_screen {
    if ! screen -list | grep -q "$SCREEN_NAME"; then
        echo "Creating screen session $SCREEN_NAME..."
        screen -dmS "$SCREEN_NAME"
    fi
}

function stop_bot {
    if screen -list | grep -q "$SCREEN_NAME"; then
        echo "Stopping existing bot..."
        screen -S "$SCREEN_NAME" -X stuff $'\003'  # Ctrl+C
        sleep 2
    fi
}

function run_bot {
    echo "Starting bot..."
    screen -S "$SCREEN_NAME" -X stuff "cd $BOT_DIR && mvn clean compile && mvn exec:java$(printf '\r')"
}


echo "Deploy started"
ensure_screen
stop_bot

echo "Updating repo..."
cd "$BOT_DIR" || exit 1
git pull

run_bot
echo "Deploy finished"
