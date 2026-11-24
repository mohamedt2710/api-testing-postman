# API Testing with Postman — Restful Booker

API testing project against [Restful Booker](https://restful-booker.herokuapp.com), a public API built for testing practice.

## What this covers

- Authentication (token-based)
- Full CRUD on booking records: create, read, update, partial update, delete
- Negative tests: invalid credentials, missing fields, wrong IDs, unauthorized access
- Test assertions written in Postman test scripts (status codes, response schema, field values)
- 10 automated RestAssured (Java + JUnit 5) tests of the main flows, run on GitHub Actions

## Repository contents

| File | Description |
|---|---|
| [Restful-Booker.postman_collection.json](Restful-Booker.postman_collection.json) | The full collection with tests |
| [Restful-Booker.postman_environment.json](Restful-Booker.postman_environment.json) | Environment variables (base URL, token, booking id) |
| [restassured-tests/](restassured-tests) | Java project: 10 RestAssured tests for auth, create, read, filter, update, delete |
| [api-test-cases.md](api-test-cases.md) | Test case documentation for each request |
| [bug-reports.md](bug-reports.md) | Defects found during API testing |

## How to run

1. Import both JSON files into Postman (Import > select files).
2. Select the "Restful Booker" environment (top right).
3. Run the collection with the Collection Runner, or run requests one by one in order:
   - `01 - Auth` first: it stores the token in the environment.
   - `02 - Create Booking`: stores the new booking id.
   - The remaining requests use those stored values.

## RestAssured tests

The same API is also checked with code, in `restassured-tests/`. Each test creates its own booking and deletes it afterwards. The tests only assert behaviour that works; the 3 known API defects stay documented in [bug-reports.md](bug-reports.md) and are not turned into tests.

```bash
cd restassured-tests
mvn test
```

The API is a shared public service, so a run can occasionally fail because of the service, not the code.

## Notes

- The API is shared and public; records created here may be modified by others and are periodically wiped.
- No real personal data is used anywhere in this collection.
