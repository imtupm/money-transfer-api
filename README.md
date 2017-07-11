
# moneymate
MoneyMate - A Rest API for money transfers between internal users and accounts.
Built in Java 8 / Dropwizard / JDBI / H2 (in memory).

## Usage

```
git clone https://github.com/sumit21roy/money-transfer-api.git
```
```
mvn package
```
```
java -jar target/moneymate-api-1.0.0-SNAPSHOT.jar server money-mate.yml
```


# Endpoints

```
- GET     /v1/users - get all users
- POST    /v1/users - create a user
- DELETE  /v1/users/{id} - delete a user by his id
- PUT     /v1/users/{id} - update a user
- GET     /v1/users/{username} - get a user by username
- GET     /v1/users/{username}/account - get a user account
- GET     /v1/accounts - get all accounts
- POST    /v1/accounts - add an account
- GET     /v1/accounts/{id} - get an account by id
- PUT     /v1/accounts/{id} - update an account
- GET     /v1/accounts/{id}/balance - get account balance
- DELETE  /v1/accounts/{username} - delete an account by username
- PUT     /v1/accounts/{username}/deposit - deposit money into an account
- PUT     /v1/accounts/{username}/withdraw - withdraw money from an account
- POST    /v1/transfers - transfer money between accounts
```

## Http Status Code Summary

```
200 OK - Everything worked as expected
204 No Content - Everything worked as expected and not additional content was sent back
422 Unprocessable Entity - The request might have missing / incorrect parameters or failed certain conditions
404 Not Found - The requested resource does not exist
```

## Account Resources

## GET /v1/accounts

##### Example

##### Request

```
GET /v1/accounts
```

##### Response
```
[
  {
    "id": 1,
    "username": "Sumit",
    "description": "salary account",
    "balance": 888,
    "currency": "EUR"
  },
  {
    "id": 2,
    "username": "Rohit",
    "description": "salary account",
    "balance": 888,
    "currency": "EUR"
  }
]
```

## POST    /v1/accounts

##### Example

###### Request

```
POST /v1/accounts
```

Body

```
{
  "username":"Sumit",
  "description":"description",
  "balance":123.00,
  "currency":"EUR"
}
```

###### Response

```
{
  "username":"Sumit",
  "description":"description",
  "balance":123.00,
  "currency":"EUR"
}
```

## GET     /v1/accounts/{id}

##### Example
###### Request

```
GET /v1/accounts/1
```

###### Response

```
{
  "username":"Sumit",
  "description":"description",
  "balance":123.00,
  "currency":"EUR"
}
```

## PUT     /v1/accounts/{id}

##### Example

###### Request

```
PUT /v1/accounts/{id}
```

Body
```
{
  "id":1,
  "username":"Sumit",
  "description":"updated description",
  "balance":123.00,
  "currency":"EUR"
}
```

###### Response

```
{
  "id":1,
  "username":"Sumit",
  "description":"updated description",
  "balance":123.00,
  "currency":"EUR"
}
```

## GET     /v1/accounts/{id}/balance

##### Example

###### Request

```
GET /v1/accounts/1/balance
```

###### Response

```
{
  "amount": 123,
  "currency": "EUR"
}
```

## DELETE  /v1/accounts/{username}

##### Example
###### Request

```
DELETE  /v1/accounts/Sumit
```

###### Response

STATUS 204 OK

## PUT     /v1/accounts/{username}/withdraw

##### Example

###### Request

```
PUT /v1/accounts/Sumit/withdraw
```

Body
```
{
  "amount": 10,
  "currency": "EUR"
}
```
###### Response

```
{
  "id": 1,
  "username": "Sumit",
  "description": "description",
  "balance": 113,
  "currency": "EUR"
}
```

## PUT     /v1/accounts/{username}/deposit

##### Example

###### Request

```
PUT /v1/accounts/Sumit/deposit
```

Body
```
{
  "amount": 10,
  "currency": "EUR"
}
```
###### Response

```
{
  "id": 2,
  "username": "Rohit",
  "description": "salary account",
  "balance": 1031.22,
  "currency": "EUR"
}
```


## User Resources

## GET /v1/users

##### Example
###### Request

```
GET /v1/users
```

###### Response

```
[
  {
    "id": 1,
    "username": "Sumit",
    "email": "sumit.roys@gmail.com"
  },
  {
    "id": 2,
    "username": "Rohit",
    "email": "rohit.roys@gmail.com"
  }
]
```

## POST    /v1/users

##### Example
###### Request

```
POST /v1/users
```

Body
```
 {
    "username": "nikhil",
    "email": "nikhil@gmail.com"
 }
```

###### Response

```
 {
     "id": 2,
     "username": "nikhil",
     "email": "nikhil@gmail.com"
 }
```

## DELETE  /v1/users/{id}

##### Example
###### Request

```
DELETE /v1/users/4
```

###### Response

STATUS 204

## PUT /v1/users/{id}

##### Example
###### Request

```
PUT /v1/users/2
```

Body
```
 {
    "id": 2,
    "username": "nikhil",
    "email": "nikhil@gmail.com"
 }
```

###### Response

```
 {
    "id": 2,
    "username": "nikhil",
    "email": "nikhil@gmail.com"
 }
```

## GET /v1/users/{username}

##### Example

###### Request

```
GET /v1/users/Sumit
```

###### Response

```
{
  "id": 1,
  "username": "Sumit",
  "email": "sumit.roys@gmail.com"
}
```

## GET /v1/users/{username}/account

##### Example
###### Request

```
GET /v1/users/Sumit/account
```

###### Response

```
{
  "id": 1,
  "username": "Sumit",
  "description": "salary account",
  "balance": 888,
  "currency": "EUR"
}
```

## Transferring Money

## POST /v1/transfers

##### Example

###### Request

```
POST /v1/transfers
```

Body
```json
{
  "amount":123.00,
  "currency":"EUR",
  "sourceAccountId":"1",
  "destinationAccountId":"2"
}
```

###### Response

STATUS 204






