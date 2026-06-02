FROM maven:3.9.16-amazoncorretto-25
RUN yum update -y && yum install -y git
COPY . /app
WORKDIR /app
RUN mvn clean package
CMD ["mvn", "install"]