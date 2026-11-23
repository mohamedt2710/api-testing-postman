# API Test Cases — Restful Booker

API under test: https://restful-booker.herokuapp.com (public practice API).
All requests were executed against the live API on 24 November 2026; results below are the observed behavior. Requests and assertions are in the Postman collection.

| ID | Request | Method / Endpoint | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| API-01 | Get token with valid credentials | POST /auth | 200 and a token | 200 with token | Pass |
| API-02 | Get token with bad credentials | POST /auth | Error, no token | "Bad credentials", no token | Pass |
| API-03 | Create a booking | POST /booking | 200, booking id, stored data matches the request | As expected | Pass |
| API-04 | Read the created booking | GET /booking/{id} | 200 with the same data | As expected | Pass |
| API-05 | Read a nonexistent booking | GET /booking/99999999 | 404 | 404 | Pass |
| API-06 | Filter bookings by name | GET /booking?firstname=&lastname= | 200, array containing the created booking | As expected | Pass |
| API-07 | Full update with token | PUT /booking/{id} | 200 with updated data | As expected | Pass |
| API-08 | Update without a token | PUT /booking/{id} | 403 Forbidden | 403 "Forbidden" | Pass |
| API-09 | Partial update with token | PATCH /booking/{id} | 200, only the given fields change | As expected | Pass |
| API-10 | Create with missing required fields | POST /booking | 400 with a validation message | 500 Internal Server Error | Fail (BUG-API-01) |
| API-11 | Create with a negative price | POST /booking | 400 rejected | 200 accepted | Fail (BUG-API-02) |
| API-12 | Create with checkout before checkin | POST /booking | 400 rejected | 200 accepted | Fail (BUG-API-03) |
| API-13 | Delete the booking with token | DELETE /booking/{id} | Success (this API documents 201) | 201 | Pass |
| API-14 | Read the deleted booking | GET /booking/{id} | 404 | 404 | Pass |

Result: 11 passed, 3 failed (all 3 failures are confirmed API defects, see bug-reports.md).
