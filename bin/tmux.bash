#!/usr/bin/env bash

set -e

tmux new-session -d -s CVC -n "App"
tmux send-keys -t "=CVC:App" "npm start" Enter

tmux new-window -d -t '=CVC' -n "Less"
tmux send-keys -t "=CVC:Less" "npm run less" Enter

[ -n "${TMUX:-}" ] &&
    tmux switch-client -t '=CVC' ||
    tmux attach-session -t '=CVC'
