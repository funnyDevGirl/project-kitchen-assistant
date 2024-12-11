build:
	./gradlew clean build

install:
	./gradlew installDist

test-report:
	./gradlew test jacocoTestReport

run:
	./gradlew bootRun

.PHONY: build