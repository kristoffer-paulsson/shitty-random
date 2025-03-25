# Shitty Random

Description: Shitty Random is a CLI and server application built using Java. It provides a simple and intuitive interface for generating random numbers and performing various randomization tasks. The app is designed to be lightweight and easy to use, making it ideal for users who need quick and reliable random number generation. It is built with a real random entropy generator and a small fast and shitty random number generator. 

## Features:

* Uses a CLI interface.
* Can pipe random to a parent process.
* Can write a specified amount of random data to a file.
* Can even start a RNG server.
* Works on all platform, written in Java.

## Installation:

```
> ./gradlew installDist
> ./gradlew runServer
```

```
> ./gradlew installDist

> sudo cp -r build/install/shitty-random /opt/

> sudo cp src/main/resources/shitty-random.service /etc/systemd/system/

> sudo systemctl daemon-reload
> sudo systemctl enable shitty-random
> sudo systemctl start shitty-random
```


Please contribute with improvments or bug fixes or just try it out, it's fun!