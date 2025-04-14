.PHONY: default
default: build

.PHONY: build
build:
	go build -o build_notes.exe main/main.go

.PHONY: clean
clean:
	rm *.exe

