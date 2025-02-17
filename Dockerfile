FROM ghcr.io/navikt/baseimages/temurin:21
COPY /target/tiltak-auditlogger-1.0-SNAPSHOT.jar app.jar
