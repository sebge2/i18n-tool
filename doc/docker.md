# Docker Tips

## Delete All sessions
````
$ docker exec -it [DOCKER IMAGE] bash
$ mongo
$ db.sessions.remove({})
````

## Find All sessions
````
$ docker exec -it [DOCKER IMAGE] bash
$ mongo
$ db.sessions.find()
````