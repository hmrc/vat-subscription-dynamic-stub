# vat-subscription-dynamic-stub

[![Build Status](https://travis-ci.org/hmrc/vat-subscription-dynamic-stub.svg)](https://travis-ci.org/hmrc/manage-vat-subscription-dynamic-stub) [ ![Download](https://api.bintray.com/packages/hmrc/releases/manage-vat-subscription-dynamic-stub/images/download.svg) ](https://bintray.com/hmrc/releases/manage-vat-subscription-dynamic-stub/_latestVersion)

## Summary
This stub microservice is used to receive, store and serve DES API stub data for the VAT View & Change project. It is dynamic, allowing it to be a stub for any request to any API without needing to implement individual static routes. This is achieved by storing JSON objects in a Mongo database with IDs that are valid API URLs. When an incoming request is made, if a matching record is found then the JSON inside the `response` field of the record will be returned.

## Using the service
The stub can be used to store JSON schemas and data that corresponds to a stored schema.

### Storing
URLs:
* `/setup/schema`
* `/setup/data`

Methods:
* `POST`

Request body:
* A valid JSON schema item (see [schema format](#schemas))
* A valid JSON data item (see [data format](#data))

Response codes:
* `200` - when population was successful
* `400` - when population was unsuccessful due to an expected reason, such as invalid JSON, no matching schema or an unsupported HTTP method
* `500` - when population was unsuccessful due to an unexpected reason

### Requesting data
URLs:
* `/*` - this can be anything and is intended to substitute a DES API URL. If a JSON record exists in the database with an ID matching this URL, it will serve the data.

Methods:
* `GET`
* `POST`
* `PUT`

Response codes:
* `*` - if an existing record is found that matches the requested URL, the status code provided in the record will be returned
* `400` - when a matching record is found, but the request body does not validate against the relevant request schema
* `404` - when no matching record is found

### Removing data
URLs:
* `/setup/schema?id=mySchemaId` - where `mySchemaId` is the unique ID of a populated schema 
* `/setup/all-schemas`
* `/setup/all-data`

Methods:
* `DELETE`

Response codes:
* `200` - when deletion was successful
* `500` - when deletion was unsuccessful due to an unexpected reason

## JSON formats

### Schemas
```
{
  "_id": "MySchemaId",
  "url": "/enterprise/my-des-url",
  "method": "GET",
  "responseSchema": {
    //a valid JSON schema
  },
  "requestSchema": {
    //a valid JSON schema
  }
}
```

* `_id` - the unique schema ID
* `url` - the regex URL used as part of validation for storing stub data
* `method` - the HTTP method for the API request
* `responseSchema` - the JSON schema of the response
* `requestSchema` - the JSON schema of the request (optional)

### Data
```
{
  "_id": "/enterprise/my-des-url",
  "schemaId": "myDesSchema",
  "method": "GET",
  "status": 200,
  "response": {
    //the JSON body the stubbed API will return
  }
}
```

* `_id` - the DES URL
* `schemaId` - the unique ID that must match a schema populated in the stub
* `method` - the HTTP method for the API request
* `status` - the status code returned by the API
* `response` - the JSON body returned by the API (optional)

## Running the service
The service can be started with `sbt run`

## License
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
