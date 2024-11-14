@echo off
:start
docker-compose build
docker-compose up -d
timeout /t 10
goto start
