mvn package -Dmaven.test.skip=true
docker buildx build --platform=linux/arm64,linux/amd64 -t jxch/capital-pa:$(Get-Date -Format 'yyyyMMdd') -t jxch/capital-pa:latest . --push
