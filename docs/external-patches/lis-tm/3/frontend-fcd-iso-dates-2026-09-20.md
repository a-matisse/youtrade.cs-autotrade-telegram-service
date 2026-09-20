# FCD: ISO-8601 dates in history responses

Fields `boughtAt` and `soldAt` in the FCD purchase and sale history responses are now ISO-8601 local date-times:

```json
{
  "boughtAt": "2026-09-13T10:27:00",
  "soldAt": "2026-09-14T11:28:00"
}
```

The frontend should parse these values as machine-readable dates and apply locale-specific formatting only when rendering them. The previous `13 Sep 2026, 10:27` representation is no longer returned.
