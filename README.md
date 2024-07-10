**Project description:**
1. Project is aiming to provide books managing service for clients
2. Service has two roles: admin and user
3. Please note, that there is conflict between lombok and mapstruct, that forced dev to avoid some annotations
4. Service contains integration and unit tests

**Possible enhancements:**
1. Add relations between user and book, retrieve user from jwt token.
2. Consider adding more test scenarios
3. Consider error responses enhancement
`

**To start locally:**
run mvn clean install
run docker desktop
run docker-compose up -d
run app within active profile local
`

**To test as admin:**
admin is able to create/update/delete book

curl --location --request POST 'http://localhost:8080/api/auth/login?username=ADMIN&password=ADMIN'

curl --location 'http://localhost:8080/api/books' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer {jwt_token}' \
--data '{
"title": "title",
"isbn": "isbn",
"category": "category"
}'
`

**To test as user:**
user is able to get all/get by id/borrow/return book

curl --location --request POST 'http://localhost:8080/api/auth/login?username=USER&password=USER'

curl --location 'http://localhost:8080/api/books' \
--header 'Authorization: Bearer {jwt_token}'
`
