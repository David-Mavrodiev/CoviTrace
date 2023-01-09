## CoviTrace API

### Short description

This is the node.js web api that the android client is going to comunicate with.

### Endpoints

#### [POST] `/get-status`

Get the infected & contacted status for a particular user by unique ID. If user with such id does not exist, then it will be created.
**Infected** means that the user contracted the disease.
**Contacted** means that the user has some close contact with some other infected individual.

Request body:

```json
{
  "uniqueId": "123",
  "contacted": false,
  "infected": false
}
```

Response body:

```json
{
  "uniqueId": "123",
  "contacted": true,
  "infected": false
}
```

#### [POST] `/status`

Change user current status to be **Infected** or **Contacted**.

Request body:

```json
{
  "uniqueId": "123",
  "contacted": true,
  "infected": false
}
```

Response body:

```json
{
  "uniqueId": "123",
  "contacted": true,
  "infected": false
}
```

#### [POST] `/location`

Send user location by id.
Request body:

```json
{
  "uniqueId": "123",
  "latitude": 124.124,
  "longitude": 123.123
}
```

Response body:

```json
{
  "uniqueId": "123",
  "latitude": 124.124,
  "longitude": 123.123
}
```

### Run using Docker

- Build the docker image `docker build davidmavrodiev/covitrace-api`. Replace `davidmavrodiev` with your docker.io username.
- Login to docker using `docker login`.
- Push the image to the `docker push davidmavrodiev/covitrace-api`. Replace `davidmavrodiev` with your docker.io username.
- Select your docker context and run the application with `docker compose up`.
