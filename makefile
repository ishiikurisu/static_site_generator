.PHONY: default
default: build

.PHONY: build
build: test
	echo "TODO"]

.PHONY: clean
clean:
	echo "TODO"

.PHONY: test
test:
	lein test

.PHONY: publish
publish:
	echo "TODO"

.PHONY: repl
repl:
	lein repl :start :port 40000

