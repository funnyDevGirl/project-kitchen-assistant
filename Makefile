build:
	./gradlew clean build

install:
	./gradlew installDist

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

dev:
	./gradlew run

prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

.PHONY: build