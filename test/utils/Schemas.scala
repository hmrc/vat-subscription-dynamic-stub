/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

object Schemas {

  val subscriptionCreateIndvOrgSchema = """{
                                          |    "$schema": "http://json-schema.org/draft-04/schema#",
                                          |    "title": "Subscription Create",
                                          |    "description": "JSON representation of finding Subscription Create",
                                          |    "type": "object",
                                          |    "oneOf": [{
                                          |            "$ref": "#/definitions/successResponse"
                                          |        },
                                          |        {
                                          |            "$ref": "#/definitions/failureResponse"
                                          |        }
                                          |    ],
                                          |    "definitions": {
                                          |        "successResponse": {
                                          |            "type": "object",
                                          |            "properties": {
                                          |                "processingDate": {
                                          |                    "type": "string",
                                          |                    "pattern": "^(((19|20)([2468][048]|[13579][26]|0[48])|2000)[-]02[-]29|((19|20)[0-9]{2}[-](0[469]|11)[-](0[1-9]|[11][0-9]|30)|(19|20)[0-9]{2}[-](0[13578]|1[02])[-](0[1-9]|[12][0-9]|3[01])|(19|20)[0-9]{2}[-]02[-](0[1-9]|1[0-9]|2[0-8])))$",
                                          |                    "description": "YYYY-MM-DD"
                                          |                },
                                          |                "subscriptionCGT": {
                                          |                    "type": "object",
                                          |                    "properties": {
                                          |                        "referenceNumber": {
                                          |                            "type": "string",
                                          |                            "pattern": "^[A-Za-z0-9]{15}$"
                                          |                        }
                                          |                    },
                                          |                    "additionalProperties": false,
                                          |                    "required": [
                                          |                        "referenceNumber"
                                          |                    ]
                                          |                }
                                          |            },
                                          |            "additionalProperties": false
                                          |        },
                                          |        "failureResponse": {
                                          |            "description": "DES Error Response Schema",
                                          |            "type": "object",
                                          |            "oneOf": [{
                                          |                    "$ref": "#/definitions/failureResponseElement"
                                          |                },
                                          |                {
                                          |                    "$ref": "#/definitions/failureResponseArray"
                                          |                }
                                          |            ]
                                          |        },
                                          |        "failureResponseArray": {
                                          |            "type": "object",
                                          |            "properties": {
                                          |                "failures": {
                                          |                    "type": "array",
                                          |                    "minItems": 2,
                                          |                    "uniqueItems": true,
                                          |                    "items": {
                                          |                        "$ref": "#/definitions/failureResponseElement"
                                          |                    }
                                          |                }
                                          |            },
                                          |            "additionalProperties": false
                                          |        },
                                          |        "failureResponseElement": {
                                          |            "type": "object",
                                          |            "properties": {
                                          |                "code": {
                                          |                    "type": "string",
                                          |                    "enum": [
                                          |                        "INVALID_NINO",
                                          |                        "INVALID_SAFEID",
                                          |                        "INVALID_SCHEMA",
                                          |                        "REQUIRED_ID_MISSING",
                                          |                        "INVALID_REQUEST",
                                          |                        "DUPLICATE_SUBMISSION",
                                          |                        "MATCH_NOT_FOUND",
                                          |                        "INACTIVE_SUBSCRIPTION",
                                          |                        "INACTIVE_DATA_FOR_PROCESSING",
                                          |                        "SYSTEM_ERROR",
                                          |                        "INVALID_ACKNOWLEDGEMENTREFERENCE",
                                          |                        "UNKNOWN_ERROR",
                                          |                        "SERVER_ERROR",
                                          |                        "SERVICE_UNAVAILABLE"
                                          |                    ],
                                          |                    "description": "Keys for all the errors returned. Custom per API"
                                          |                },
                                          |                "reason": {
                                          |                    "type": "string",
                                          |                    "minLength": 1,
                                          |                    "maxLength": 80,
                                          |                    "description": "A simple description for the failure"
                                          |                }
                                          |            },
                                          |            "required": [
                                          |                "code",
                                          |                "reason"
                                          |            ],
                                          |            "additionalProperties": false
                                          |        }
                                          |    }
                                          |}"""
  val registrationGhostSchema = """{
                                  |    "$schema": "http://json-schema.org/draft-04/schema#",
                                  |    "title": "Register without UTR",
                                  |    "description": "Register without UTR",
                                  |    "type": "object",
                                  |    "oneOf": [{
                                  |            "$ref": "#/definitions/organisationRegistrant"
                                  |        },
                                  |        {
                                  |            "$ref": "#/definitions/individualRegistrant"
                                  |        }
                                  |    ],
                                  |    "definitions": {
                                  |        "dateString": {
                                  |            "type": [
                                  |                "string"
                                  |            ],
                                  |            "description": "Format CCYY-MM-DD"
                                  |        },
                                  |        "individualRegistrant": {
                                  |            "type": "object",
                                  |            "properties": {
                                  |                "acknowledgementReference": {
                                  |                    "type": "string",
                                  |                    "pattern": "^[A-Za-z0-9 -]{1,32}$"
                                  |                },
                                  |                "isAnAgent": {
                                  |                    "type": "boolean"
                                  |                },
                                  |                "isAGroup": {
                                  |                    "type": "boolean"
                                  |                },
                                  |                "identification": {
                                  |                    "$ref": "#/definitions/identificationType"
                                  |                },
                                  |                "individual": {
                                  |                    "type": "object",
                                  |                    "properties": {
                                  |                        "firstName": {
                                  |                            "type": "string",
                                  |                            "pattern": "^[a-zA-Z &`\\-\\'^]{1,35}$"
                                  |                        },
                                  |                        "middleName": {
                                  |                            "type": "string",
                                  |                            "pattern": "^[a-zA-Z &`\\-\\'^]{1,35}$"
                                  |                        },
                                  |                        "lastName": {
                                  |                            "type": "string",
                                  |                            "pattern": "^[a-zA-Z &`\\-\\'^]{1,35}$"
                                  |                        },
                                  |                        "dateOfBirth": {
                                  |                            "$ref": "#/definitions/dateString"
                                  |                        }
                                  |                    },
                                  |                    "additionalProperties": false
                                  |                },
                                  |                "address": {
                                  |                    "oneOf": [{
                                  |                            "$ref": "#/definitions/foreignAddress"
                                  |                        },
                                  |                        {
                                  |                            "$ref": "#/definitions/ukAddress"
                                  |                        }
                                  |                    ]
                                  |                },
                                  |                "contactDetails": {
                                  |                    "$ref": "#/definitions/contactDetailsType"
                                  |                }
                                  |            },
                                  |            "required": [
                                  |                "acknowledgementReference",
                                  |                "isAnAgent",
                                  |                "isAGroup",
                                  |                "individual",
                                  |                "address",
                                  |                "contactDetails"
                                  |            ],
                                  |            "additionalProperties": false
                                  |        },
                                  |        "organisationRegistrant": {
                                  |            "type": "object",
                                  |            "properties": {
                                  |                "acknowledgementReference": {
                                  |                    "type": "string",
                                  |                    "pattern": "^[A-Za-z0-9 -]{1,32}$"
                                  |                },
                                  |                "isAnAgent": {
                                  |                    "type": "boolean"
                                  |                },
                                  |                "isAGroup": {
                                  |                    "type": "boolean"
                                  |                },
                                  |                "identification": {
                                  |                    "$ref": "#/definitions/identificationType"
                                  |                },
                                  |                "organisation": {
                                  |                    "type": "object",
                                  |                    "properties": {
                                  |                        "organisationName": {
                                  |                            "type": "string",
                                  |                            "pattern": "^[a-zA-Z0-9 '&\\/]{1,105}$"
                                  |                        }
                                  |                    },
                                  |                    "required": [
                                  |                        "organisationName"
                                  |                    ],
                                  |                    "additionalProperties": false
                                  |                },
                                  |                "address": {
                                  |                    "oneOf": [{
                                  |                            "$ref": "#/definitions/foreignAddress"
                                  |                        },
                                  |                        {
                                  |                            "$ref": "#/definitions/ukAddress"
                                  |                        }
                                  |                    ]
                                  |                },
                                  |                "contactDetails": {
                                  |                    "$ref": "#/definitions/contactDetailsType"
                                  |                }
                                  |            },
                                  |            "required": [
                                  |                "acknowledgementReference",
                                  |                "isAnAgent",
                                  |                "isAGroup",
                                  |                "organisation",
                                  |                "address",
                                  |                "contactDetails"
                                  |            ],
                                  |            "additionalProperties": false
                                  |        },
                                  |        "identificationType": {
                                  |            "type": "object",
                                  |            "properties": {
                                  |                "idNumber": {
                                  |                    "type": "string",
                                  |                    "description": "Non-UK ID Number",
                                  |                    "pattern": "^[a-zA-Z0-9 '&\\-]{1,60}$"
                                  |                },
                                  |                "issuingInstitution": {
                                  |                    "type": "string",
                                  |                    "description": "Issuing Institution",
                                  |                    "pattern": "^[a-zA-Z0-9 '&\\-\\/]{1,40}$",
                                  |                    "minLength": 1,
                                  |                    "maxLength": 40
                                  |                },
                                  |                "issuingCountryCode": {
                                  |                    "type": "string",
                                  |                    "pattern": "(?!^GB$)^[A-Z]{2}$"
                                  |                }
                                  |            },
                                  |            "required": [
                                  |                "idNumber",
                                  |                "issuingInstitution",
                                  |                "issuingCountryCode"
                                  |            ],
                                  |            "additionalProperties": false
                                  |        },
                                  |        "contactDetailsType": {
                                  |            "type": "object",
                                  |            "properties": {
                                  |                "phoneNumber": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "pattern": "^[A-Z0-9 )/(\\-*#]+$",
                                  |                    "minLength": 1,
                                  |                    "maxLength": 24
                                  |                },
                                  |                "mobileNumber": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "pattern": "^[A-Z0-9 )/(\\-*#]+$",
                                  |                    "minLength": 1,
                                  |                    "maxLength": 24
                                  |                },
                                  |                "faxNumber": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "pattern": "^[A-Z0-9 )/(\\-*#]+$",
                                  |                    "minLength": 1,
                                  |                    "maxLength": 24
                                  |                },
                                  |                "emailAddress": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "format": "email",
                                  |                    "maxLength": 132
                                  |                }
                                  |            },
                                  |            "additionalProperties": false
                                  |        },
                                  |        "ukAddress": {
                                  |            "type": "object",
                                  |            "properties": {
                                  |                "addressLine1": {
                                  |                    "type": "string",
                                  |                    "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$"
                                  |                },
                                  |                "addressLine2": {
                                  |                    "type": "string",
                                  |                    "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$"
                                  |                },
                                  |                "addressLine3": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$",
                                  |                    "minLength": 0,
                                  |                    "maxLength": 35
                                  |                },
                                  |                "addressLine4": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$",
                                  |                    "minLength": 0,
                                  |                    "maxLength": 35
                                  |                },
                                  |                "postalCode": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "minLength": 0,
                                  |                    "maxLength": 10
                                  |                },
                                  |                "countryCode": {
                                  |                    "type": "string",
                                  |                    "enum": [
                                  |                        "GB"
                                  |                    ]
                                  |                }
                                  |            },
                                  |            "required": [
                                  |                "addressLine1",
                                  |                "addressLine2",
                                  |                "postalCode",
                                  |                "countryCode"
                                  |            ],
                                  |            "additionalProperties": false
                                  |        },
                                  |        "foreignAddress": {
                                  |            "type": "object",
                                  |            "properties": {
                                  |                "addressLine1": {
                                  |                    "type": "string",
                                  |                    "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$"
                                  |                },
                                  |                "addressLine2": {
                                  |                    "type": "string",
                                  |                    "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$"
                                  |                },
                                  |                "addressLine3": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$",
                                  |                    "minLength": 0,
                                  |                    "maxLength": 35
                                  |                },
                                  |                "addressLine4": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$",
                                  |                    "minLength": 0,
                                  |                    "maxLength": 35
                                  |                },
                                  |                "postalCode": {
                                  |                    "type": [
                                  |                        "string",
                                  |                        "null"
                                  |                    ],
                                  |                    "minLength": 0,
                                  |                    "maxLength": 10
                                  |                },
                                  |                "countryCode": {
                                  |                    "type": "string",
                                  |                    "enum": [
                                  |                        "AD",
                                  |                        "AE",
                                  |                        "AF",
                                  |                        "AG",
                                  |                        "AI",
                                  |                        "AL",
                                  |                        "AM",
                                  |                        "AN",
                                  |                        "AO",
                                  |                        "AQ",
                                  |                        "AR",
                                  |                        "AS",
                                  |                        "AT",
                                  |                        "AU",
                                  |                        "AW",
                                  |                        "AX",
                                  |                        "AZ",
                                  |                        "BA",
                                  |                        "BB",
                                  |                        "BD",
                                  |                        "BE",
                                  |                        "BF",
                                  |                        "BG",
                                  |                        "BH",
                                  |                        "BI",
                                  |                        "BJ",
                                  |                        "BM",
                                  |                        "BN",
                                  |                        "BO",
                                  |                        "BQ",
                                  |                        "BR",
                                  |                        "BS",
                                  |                        "BT",
                                  |                        "BV",
                                  |                        "BW",
                                  |                        "BY",
                                  |                        "BZ",
                                  |                        "CA",
                                  |                        "CC",
                                  |                        "CD",
                                  |                        "CF",
                                  |                        "CG",
                                  |                        "CH",
                                  |                        "CI",
                                  |                        "CK",
                                  |                        "CL",
                                  |                        "CM",
                                  |                        "CN",
                                  |                        "CO",
                                  |                        "CR",
                                  |                        "CS",
                                  |                        "CU",
                                  |                        "CV",
                                  |                        "CW",
                                  |                        "CX",
                                  |                        "CY",
                                  |                        "CZ",
                                  |                        "DE",
                                  |                        "DJ",
                                  |                        "DK",
                                  |                        "DM",
                                  |                        "DO",
                                  |                        "DZ",
                                  |                        "EC",
                                  |                        "EE",
                                  |                        "EG",
                                  |                        "EH",
                                  |                        "ER",
                                  |                        "ES",
                                  |                        "ET",
                                  |                        "EU",
                                  |                        "FI",
                                  |                        "FJ",
                                  |                        "FK",
                                  |                        "FM",
                                  |                        "FO",
                                  |                        "FR",
                                  |                        "GA",
                                  |                        "GD",
                                  |                        "GE",
                                  |                        "GF",
                                  |                        "GG",
                                  |                        "GH",
                                  |                        "GI",
                                  |                        "GL",
                                  |                        "GM",
                                  |                        "GN",
                                  |                        "GP",
                                  |                        "GQ",
                                  |                        "GR",
                                  |                        "GS",
                                  |                        "GT",
                                  |                        "GU",
                                  |                        "GW",
                                  |                        "GY",
                                  |                        "HK",
                                  |                        "HM",
                                  |                        "HN",
                                  |                        "HR",
                                  |                        "HT",
                                  |                        "HU",
                                  |                        "ID",
                                  |                        "IE",
                                  |                        "IL",
                                  |                        "IM",
                                  |                        "IN",
                                  |                        "IO",
                                  |                        "IQ",
                                  |                        "IR",
                                  |                        "IS",
                                  |                        "IT",
                                  |                        "JE",
                                  |                        "JM",
                                  |                        "JO",
                                  |                        "JP",
                                  |                        "KE",
                                  |                        "KG",
                                  |                        "KH",
                                  |                        "KI",
                                  |                        "KM",
                                  |                        "KN",
                                  |                        "KP",
                                  |                        "KR",
                                  |                        "KW",
                                  |                        "KY",
                                  |                        "KZ",
                                  |                        "LA",
                                  |                        "LB",
                                  |                        "LC",
                                  |                        "LI",
                                  |                        "LK",
                                  |                        "LR",
                                  |                        "LS",
                                  |                        "LT",
                                  |                        "LU",
                                  |                        "LV",
                                  |                        "LY",
                                  |                        "MA",
                                  |                        "MC",
                                  |                        "MD",
                                  |                        "ME",
                                  |                        "MF",
                                  |                        "MG",
                                  |                        "MH",
                                  |                        "MK",
                                  |                        "ML",
                                  |                        "MM",
                                  |                        "MN",
                                  |                        "MO",
                                  |                        "MP",
                                  |                        "MQ",
                                  |                        "MR",
                                  |                        "MS",
                                  |                        "MT",
                                  |                        "MU",
                                  |                        "MV",
                                  |                        "MW",
                                  |                        "MX",
                                  |                        "MY",
                                  |                        "MZ",
                                  |                        "NA",
                                  |                        "NC",
                                  |                        "NE",
                                  |                        "NF",
                                  |                        "NG",
                                  |                        "NI",
                                  |                        "NL",
                                  |                        "NO",
                                  |                        "NP",
                                  |                        "NR",
                                  |                        "NT",
                                  |                        "NU",
                                  |                        "NZ",
                                  |                        "OM",
                                  |                        "OR",
                                  |                        "PA",
                                  |                        "PE",
                                  |                        "PF",
                                  |                        "PG",
                                  |                        "PH",
                                  |                        "PK",
                                  |                        "PL",
                                  |                        "PM",
                                  |                        "PN",
                                  |                        "PR",
                                  |                        "PS",
                                  |                        "PT",
                                  |                        "PW",
                                  |                        "PY",
                                  |                        "QA",
                                  |                        "RE",
                                  |                        "RO",
                                  |                        "RS",
                                  |                        "RU",
                                  |                        "RW",
                                  |                        "SA",
                                  |                        "SB",
                                  |                        "SC",
                                  |                        "SD",
                                  |                        "SE",
                                  |                        "SG",
                                  |                        "SH",
                                  |                        "SI",
                                  |                        "SJ",
                                  |                        "SK",
                                  |                        "SL",
                                  |                        "SM",
                                  |                        "SN",
                                  |                        "SO",
                                  |                        "SR",
                                  |                        "SS",
                                  |                        "ST",
                                  |                        "SV",
                                  |                        "SX",
                                  |                        "SY",
                                  |                        "SZ",
                                  |                        "TC",
                                  |                        "TD",
                                  |                        "TF",
                                  |                        "TG",
                                  |                        "TH",
                                  |                        "TJ",
                                  |                        "TK",
                                  |                        "TL",
                                  |                        "TM",
                                  |                        "TN",
                                  |                        "TO",
                                  |                        "TP",
                                  |                        "TR",
                                  |                        "TT",
                                  |                        "TV",
                                  |                        "TW",
                                  |                        "TZ",
                                  |                        "UA",
                                  |                        "UG",
                                  |                        "UM",
                                  |                        "UN",
                                  |                        "US",
                                  |                        "UY",
                                  |                        "UZ",
                                  |                        "VA",
                                  |                        "VC",
                                  |                        "VE",
                                  |                        "VG",
                                  |                        "VI",
                                  |                        "VN",
                                  |                        "VU",
                                  |                        "WF",
                                  |                        "WS",
                                  |                        "YE",
                                  |                        "YT",
                                  |                        "ZA",
                                  |                        "ZM",
                                  |                        "ZW"
                                  |                    ]
                                  |                }
                                  |            },
                                  |            "required": [
                                  |                "addressLine1",
                                  |                "addressLine2",
                                  |                "countryCode"
                                  |            ],
                                  |            "additionalProperties": false
                                  |        }
                                  |    }
                                  |}"""

}
