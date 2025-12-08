.PHONY: default
default: build

.PHONY: build
build: test
	echo "TODO"

.PHONY: clean
clean:
	echo "TODO"

.PHONY: test
test:
	echo "TODO"

.PHONY: publish
publish:
	echo "TODO"

.PHONY: repl
repl:
	lein repl :start :port 40000

