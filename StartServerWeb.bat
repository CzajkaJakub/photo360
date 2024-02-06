call mvn clean install -P skipTests,docker
timeout 5
cd docker-sys-photo360-conf
docker-compose up -d
pause