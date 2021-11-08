# hibernate
hibernate

To build: `mvn -DskipTests=true clean package`
To run the tests: `mvn clean test`

1) Run `EasyNotesApplicationTests`

2) Test the API:

curl -X POST localhost:46353/api/notes \
    -H 'Content-Type: application/json' \
    -d '{"title":"a","content":"b"}'

curl localhost:46353/api/notes

curl -X POST localhost:46353/api/notes \
    -H 'Content-Type: application/json' \
    -d '{"title":"a2","content":"b2"}'

curl localhost:46353/api/notes

curl -X DELETE localhost:46353/api/notes/1

curl -X POST localhost:46353/api/notes \
    -H 'Content-Type: application/json' \
    -d '{"title":"a2","content":"b2", "contentOpt":"foobar"}'