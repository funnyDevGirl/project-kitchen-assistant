build:
	./gradlew clean build

install:
	./gradlew installDist

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

run:
	./gradlew bootRun

.PHONY: build