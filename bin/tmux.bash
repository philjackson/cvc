#!/usr/bin/env bash

tmux new-session -d -s CVC -n "App"
tmux send-keys -t "=CVC:App" "npm start" Enter

tmux new-window -d -t '=CVC' -n "Test"
tmux send-keys -t "=CVC:Test" "sleep 5 && npm test" Enter

tmux new-window -d -t '=CVC' -n "Less"
tmux send-keys -t "=CVC:Less" "npm run less" Enter

[ -n "${TMUX:-}" ] &&
    tmux switch-client -t '=CVC' ||
    tmux attach-session -t '=CVC'
