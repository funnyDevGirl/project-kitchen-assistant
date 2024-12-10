FROM gradle:8.6.0-jdk21

WORKDIR /

COPY / .

ENV SPRING_PROFILES_ACTIVE=prod

RUN ./gradlew installDist

CMD ./build/install/app/bin/app

