# Manual-timing

image:https://img.shields.io/badge/vert.x-4.5.1-purple.svg[link="https://vertx.io"]

This application was generated using http://start.vertx.io
## prerequisites
install the following tool
- java 17

(tested fine on java 21)
## Building

To launch your tests:
```
./mvnw test
```

To package your application:
```
./mvnw clean package -P build-frontend
```
## Deploying

1) Start the jar produced in the backend module. Exemple:
```
java -jar backend-1.0.0-fat.jar
```
2) edit the file configFile.json
3) Start the jar again

## Developing
### backend
To run your backend application:
start java class lu.even.manual_timing.MainApp from module backend in your favourite ide
```
cd backend
mvn exec:java
```

### Angular
To run your frontend application:
```
cd frontend/src/main/angular
npm install
npm run start-dev
```

## Help
### Vert.x
* https://vertx.io/docs/[Vert.x Documentation]
* https://stackoverflow.com/questions/tagged/vert.x?sort=newest&pageSize=15[Vert.x Stack Overflow]
* https://groups.google.com/forum/?fromgroups#!forum/vertx[Vert.x User Group]
* https://discord.gg/6ry7aqPWXy[Vert.x Discord]
* https://gitter.im/eclipse-vertx/vertx-users[Vert.x Gitter]


