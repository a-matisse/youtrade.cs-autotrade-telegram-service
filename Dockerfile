FROM maven:3.9.9-amazoncorretto-17
RUN yum update -y && yum install -y git

COPY . /app
WORKDIR /app

# Копируем default.env (убедись, что файл рядом с Dockerfile)
COPY default.env /app/default.env

# Экспортим переменные из файла и запускаем сборку (sh: '.' поддерживается)
RUN set -o allexport && . /app/default.env && mvn clean package

CMD ["mvn", "install"]