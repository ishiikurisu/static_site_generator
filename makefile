.PHONY: default
default: build

.PHONY: build
build: test
	lein uberjar

.PHONY: clean
clean:
	git clean -xdf

.PHONY: test
test:
	lein test

.PHONY: publish
publish: build
	cp target/uberjar/*-standalone.jar ssg.jar

.PHONY: repl
repl:
	lein repl :start :port 40000

