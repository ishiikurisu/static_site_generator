.PHONY: default
default: build

.PHONY: build
build:
	go build -o build_notes.exe main/main.go

.PHONY: clean
clean:
	rm *.exe

.PHONY: quick_test
quick_test: build
	./build_notes.exe tmp/notes tmp/output

.PHONY: publish
publish:
	sh cross-compile.sh

