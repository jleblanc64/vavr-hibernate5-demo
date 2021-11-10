app_version=3.0

mvn clean package
docker build -t springio/gs-spring-boot-docker:$app_version .

aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 601730646169.dkr.ecr.us-west-2.amazonaws.com
docker tag springio/gs-spring-boot-docker:$app_version 601730646169.dkr.ecr.us-west-2.amazonaws.com/scalademopriv:$app_version
docker push 601730646169.dkr.ecr.us-west-2.amazonaws.com/scalademopriv:$app_version