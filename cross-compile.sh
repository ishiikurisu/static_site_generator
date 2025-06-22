#!/bin/sh
set -ex
env GOOS=linux GOARCH=arm64 go build -ldflags "-s -w" -o build_notes.linux.aarch64.exe main/main.go 
env GOOS=linux GOARCH=amd64 go build -ldflags "-s -w" -o build_notes.linux.x86_64.exe main/main.go 
env GOOS=darwin GOARCH=amd64 go build -ldflags "-s -w" -o build_notes.darwin.x86_64.exe main/main.go 

