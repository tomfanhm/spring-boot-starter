.PHONY: run test build lint format docker-up docker-down migrate

run:
	./gradlew bootRun --args='--spring.profiles.active=dev'

test:
	./gradlew test

build:
	./gradlew bootJar

lint:
	./gradlew spotlessCheck checkstyleMain checkstyleTest

format:
	./gradlew spotlessApply

docker-up:
	docker compose up -d

docker-down:
	docker compose down

migrate:
	./gradlew flywayMigrate
