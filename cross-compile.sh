#!/bin/sh
set -ex
env GOOS=android GOARCH=arm64 go build -o build_notes.android.arm64.exe main/main.go 
env GOOS=darwin GOARCH=amd64 go build -o build_notes.darwin.amd64.exe main/main.go 
env GOOS=linux GOARCH=amd64 go build -o build_notes.linux.amd64.exe main/main.go 

