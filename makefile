.PHONY: default
default: build

.PHONY: build
build:
	go build -o static_site_generator.exe main.go

.PHONY: clean
clean:
	rm *.exe

