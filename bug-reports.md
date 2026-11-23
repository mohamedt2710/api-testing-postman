# API Bug Reports — Restful Booker

Defects confirmed against the live API on 24 November 2026. Each was reproduced with direct HTTP calls and is covered by a failing test in the Postman collection (requests 10-12, which assert the correct behavior).

---

## BUG-API-01: Creating a booking with missing required fields returns 500

| | |
|---|---|
| Severity | Major |
| Priority | High |
| Endpoint | POST /booking |
| Found in | API-10 |

**Description:** Sending a booking with missing required fields (for example only `{"firstname": "OnlyName"}`) or an empty body returns **500 Internal Server Error**. Invalid JSON correctly returns 400, so validation exists for malformed JSON but not for missing fields.

**Expected:** 400 Bad Request with a message naming the missing fields.
**Actual:** 500 Internal Server Error with no useful detail. A 500 tells the client the server is broken, hides the real problem, and pollutes server logs and monitoring.

**Reproduction:** `POST /booking` with body `{"firstname": "OnlyName"}`.

---

## BUG-API-02: Negative price is accepted

| | |
|---|---|
| Severity | Major |
| Priority | Medium |
| Endpoint | POST /booking |
| Found in | API-11 |

**Description:** A booking with `"totalprice": -50` is accepted and stored (HTTP 200). There is no business validation on the price.

**Expected:** 400 Bad Request rejecting a negative (or otherwise invalid) price.
**Actual:** The booking is created with a negative price.

---

## BUG-API-03: Checkout date earlier than checkin date is accepted

| | |
|---|---|
| Severity | Major |
| Priority | Medium |
| Endpoint | POST /booking |
| Found in | API-12 |

**Description:** A booking with checkin 2026-12-10 and checkout 2026-12-01 (checkout before checkin) is accepted and stored (HTTP 200). Impossible stay dates should never be bookable.

**Expected:** 400 Bad Request rejecting the date combination.
**Actual:** The booking is created.

---

## Notes (observations, not defects)

- DELETE /booking/{id} returns **201** on success. This is unusual (200 or 204 is conventional) but it is the API's documented behavior, so it is not reported as a defect.
- The API is shared: any record can be modified or deleted by other users, and data is periodically wiped. Test data here uses no real personal information.
