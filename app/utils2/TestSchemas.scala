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

package utils2

import play.api.libs.json.Json

object TestSchemas {

  lazy val subscriptionCreateIndvOrgSchema = Json.parse("""{
    "$schema": "http://json-schema.org/draft-04/schema#",
    "title": "Subscription Create Schema",
    "description": "JSON representation of Subscription Create Payload",
    "type": "object",
    "properties": {
      "addressDetail": {
      "oneOf": [
    {
      "type": "object",
      "properties": {
      "line1": {
      "type": "string",
      "pattern": "^[A-Za-z0-9 \\-,.&'\\/]{1,35}$"
    },
      "line2": {
      "type": "string",
      "pattern": "^[A-Za-z0-9 \\-,.&'\\/]{1,35}$"
    },
      "line3": {
      "type": "string",
      "pattern": "^[A-Za-z0-9 \\-,.&'\\/]{1,35}$"
    },
      "line4": {
      "type": "string",
      "pattern": "^[A-Za-z0-9 \\-,.&'\\/]{1,35}$"
    },
      "postalCode": {
      "type": "string",
      "pattern": "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$|BFPO\\s?[0-9]{1,5}$"
    },
      "countryCode": {
      "type": "string",
      "enum": [
      "GB"
      ]
    }
    },
      "additionalProperties": false,
      "required": [
      "line1",
      "line2",
      "countryCode",
      "postalCode"
      ]
    },
    {
      "type": "object",
      "properties": {
      "line1": {
      "type": "string",
      "pattern": "^[A-Za-z0-9 \\-,.&'\\/]{1,35}$"
    },
      "line2": {
      "type": "string",
      "pattern": "^[A-Za-z0-9 \\-,.&'\\/]{1,35}$"
    },
      "line3": {
      "type": "string",
      "pattern": "^[A-Za-z0-9 \\-,.&'\\/]{1,35}$"
    },
      "line4": {
      "type": "string",
      "pattern": "^[A-Za-z0-9 \\-,.&'\\/]{1,35}$"
    },
      "postalCode": {
      "type": "string",
      "pattern": "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$|BFPO\\s?[0-9]{1,5}$"
    },
      "countryCode": {
      "$ref": "#/definitions/countryCodes"
    }
    },
      "additionalProperties": false,
      "required": [
      "line1",
      "line2",
      "countryCode"
      ]
    }
      ]
    }
    },
    "additionalProperties": false,
    "definitions": {
      "countryCodes": {
      "type": "string",
      "enum": [
      "AD",
      "AE",
      "AF",
      "AG",
      "AI",
      "AL",
      "AM",
      "AN",
      "AO",
      "AQ",
      "AR",
      "AS",
      "AT",
      "AU",
      "AW",
      "AX",
      "AZ",
      "BA",
      "BB",
      "BD",
      "BE",
      "BF",
      "BG",
      "BH",
      "BI",
      "BJ",
      "BL",
      "BM",
      "BN",
      "BO",
      "BQ",
      "BR",
      "BS",
      "BT",
      "BV",
      "BW",
      "BY",
      "BZ",
      "CA",
      "CC",
      "CD",
      "CF",
      "CG",
      "CH",
      "CI",
      "CK",
      "CL",
      "CM",
      "CN",
      "CO",
      "CR",
      "CS",
      "CU",
      "CV",
      "CW",
      "CX",
      "CY",
      "CZ",
      "DE",
      "DJ",
      "DK",
      "DM",
      "DO",
      "DZ",
      "EC",
      "EE",
      "EG",
      "EH",
      "ER",
      "ES",
      "ET",
      "EU",
      "FC",
      "FI",
      "FJ",
      "FK",
      "FM",
      "FO",
      "FR",
      "GA",
      "GB",
      "GD",
      "GE",
      "GF",
      "GG",
      "GH",
      "GI",
      "GL",
      "GM",
      "GN",
      "GP",
      "GQ",
      "GR",
      "GS",
      "GT",
      "GU",
      "GW",
      "GY",
      "HK",
      "HM",
      "HN",
      "HR",
      "HT",
      "HU",
      "ID",
      "IE",
      "IL",
      "IM",
      "IN",
      "IO",
      "IQ",
      "IR",
      "IS",
      "IT",
      "JE",
      "JM",
      "JO",
      "JP",
      "KE",
      "KG",
      "KH",
      "KI",
      "KM",
      "KN",
      "KP",
      "KR",
      "KW",
      "KY",
      "KZ",
      "LA",
      "LB",
      "LC",
      "LI",
      "LK",
      "LR",
      "LS",
      "LT",
      "LU",
      "LV",
      "LY",
      "MA",
      "MC",
      "MD",
      "ME",
      "MF",
      "MG",
      "MH",
      "MK",
      "ML",
      "MM",
      "MN",
      "MO",
      "MP",
      "MQ",
      "MR",
      "MS",
      "MT",
      "MU",
      "MV",
      "MW",
      "MX",
      "MY",
      "MZ",
      "NA",
      "NC",
      "NE",
      "NF",
      "NG",
      "NI",
      "NL",
      "NO",
      "NP",
      "NR",
      "NT",
      "NU",
      "NZ",
      "OM",
      "OR",
      "PA",
      "PE",
      "PF",
      "PG",
      "PH",
      "PK",
      "PL",
      "PM",
      "PN",
      "PR",
      "PS",
      "PT",
      "PW",
      "PY",
      "QA",
      "RE",
      "RO",
      "RS",
      "RU",
      "RW",
      "SA",
      "SB",
      "SC",
      "SD",
      "SE",
      "SG",
      "SH",
      "SI",
      "SJ",
      "SK",
      "SL",
      "SM",
      "SN",
      "SO",
      "SR",
      "SS",
      "ST",
      "SV",
      "SX",
      "SY",
      "SZ",
      "TC",
      "TD",
      "TF",
      "TG",
      "TH",
      "TJ",
      "TK",
      "TL",
      "TM",
      "TN",
      "TO",
      "TP",
      "TR",
      "TT",
      "TV",
      "TW",
      "TZ",
      "UA",
      "UG",
      "UM",
      "UN",
      "US",
      "UY",
      "UZ",
      "VA",
      "VC",
      "VE",
      "VG",
      "VI",
      "VN",
      "VU",
      "WF",
      "WS",
      "YE",
      "YT",
      "ZA",
      "ZM",
      "ZW"
      ]
    }
    }
  }""")
  lazy val registrationGhostSchema = Json.parse("""{
                                      "$schema": "http://json-schema.org/draft-04/schema#",
                                      "title": "Register without UTR",
                                      "description": "Register without UTR",
                                      "type": "object",
                                      "oneOf": [{
                                              "$ref": "#/definitions/organisationRegistrant"
                                          },
                                          {
                                              "$ref": "#/definitions/individualRegistrant"
                                         }
                                     ],
                                      "definitions": {
                                          "dateString": {
                                              "type": [
                                                  "string"
                                              ],
                                              "description": "Format CCYY-MM-DD"
                                          },
                                         "individualRegistrant": {
                                              "type": "object",
                                             "properties": {
                                                  "acknowledgementReference": {
                                                      "type": "string",
                                                      "pattern": "^[A-Za-z0-9 -]{1,32}$"
                                                 },
                                                  "isAnAgent": {
                                                      "type": "boolean"
                                                  },
                                                  "isAGroup": {
                                                      "type": "boolean"
                                                  },
                                                  "identification": {
                                                      "$ref": "#/definitions/identificationType"
                                                  },
                                                  "individual": {
                                                      "type": "object",
                                                      "properties": {
                                                          "firstName": {
                                                              "type": "string",
                                                              "pattern": "^[a-zA-Z &`\\-\\'^]{1,35}$"
                                                          },
                                                          "middleName": {
                                                              "type": "string",
                                                              "pattern": "^[a-zA-Z &`\\-\\'^]{1,35}$"
                                                          },
                                                          "lastName": {
                                                              "type": "string",
                                                              "pattern": "^[a-zA-Z &`\\-\\'^]{1,35}$"
                                                          },
                                                          "dateOfBirth": {
                                                              "$ref": "#/definitions/dateString"
                                                          }
                                                      },
                                                      "additionalProperties": false
                                                  },
                                                  "address": {
                                                      "oneOf": [{
                                                              "$ref": "#/definitions/foreignAddress"
                                                          },
                                                          {
                                                              "$ref": "#/definitions/ukAddress"
                                                          }
                                                      ]
                                                  },
                                                  "contactDetails": {
                                                      "$ref": "#/definitions/contactDetailsType"
                                                  }
                                              },
                                              "required": [
                                                  "acknowledgementReference",
                                                  "isAnAgent",
                                                  "isAGroup",
                                                  "individual",
                                                  "address",
                                                  "contactDetails"
                                              ],
                                              "additionalProperties": false
                                          },
                                          "organisationRegistrant": {
                                              "type": "object",
                                              "properties": {
                                                  "acknowledgementReference": {
                                                      "type": "string",
                                                      "pattern": "^[A-Za-z0-9 -]{1,32}$"
                                                  },
                                                  "isAnAgent": {
                                                      "type": "boolean"
                                                  },
                                                  "isAGroup": {
                                                      "type": "boolean"
                                                  },
                                                  "identification": {
                                                      "$ref": "#/definitions/identificationType"
                                                  },
                                                  "organisation": {
                                                      "type": "object",
                                                      "properties": {
                                                          "organisationName": {
                                                              "type": "string",
                                                              "pattern": "^[a-zA-Z0-9 '&\\/]{1,105}$"
                                                          }
                                                      },
                                                      "required": [
                                                          "organisationName"
                                                      ],
                                                      "additionalProperties": false
                                                  },
                                                  "address": {
                                                      "oneOf": [{
                                                              "$ref": "#/definitions/foreignAddress"
                                                          },
                                                          {
                                                              "$ref": "#/definitions/ukAddress"
                                                          }
                                                      ]
                                                  },
                                                  "contactDetails": {
                                                      "$ref": "#/definitions/contactDetailsType"
                                                  }
                                              },
                                              "required": [
                                                  "acknowledgementReference",
                                                  "isAnAgent",
                                                  "isAGroup",
                                                  "organisation",
                                                  "address",
                                                  "contactDetails"
                                              ],
                                              "additionalProperties": false
                                          },
                                          "identificationType": {
                                              "type": "object",
                                              "properties": {
                                                  "idNumber": {
                                                      "type": "string",
                                                      "description": "Non-UK ID Number",
                                                      "pattern": "^[a-zA-Z0-9 '&\\-]{1,60}$"
                                                  },
                                                  "issuingInstitution": {
                                                      "type": "string",
                                                      "description": "Issuing Institution",
                                                      "pattern": "^[a-zA-Z0-9 '&\\-\\/]{1,40}$",
                                                      "minLength": 1,
                                                      "maxLength": 40
                                                  },
                                                  "issuingCountryCode": {
                                                      "type": "string",
                                                      "pattern": "(?!^GB$)^[A-Z]{2}$"
                                                  }
                                              },
                                              "required": [
                                                  "idNumber",
                                                  "issuingInstitution",
                                                  "issuingCountryCode"
                                              ],
                                              "additionalProperties": false
                                          },
                                          "contactDetailsType": {
                                              "type": "object",
                                              "properties": {
                                                  "phoneNumber": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "pattern": "^[A-Z0-9 )/(\\-*#]+$",
                                                      "minLength": 1,
                                                      "maxLength": 24
                                                  },
                                                  "mobileNumber": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "pattern": "^[A-Z0-9 )/(\\-*#]+$",
                                                      "minLength": 1,
                                                      "maxLength": 24
                                                  },
                                                  "faxNumber": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "pattern": "^[A-Z0-9 )/(\\-*#]+$",
                                                      "minLength": 1,
                                                      "maxLength": 24
                                                  },
                                                  "emailAddress": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "format": "email",
                                                      "maxLength": 132
                                                  }
                                              },
                                              "additionalProperties": false
                                          },
                                          "ukAddress": {
                                              "type": "object",
                                              "properties": {
                                                  "addressLine1": {
                                                      "type": "string",
                                                      "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$"
                                                  },
                                                  "addressLine2": {
                                                      "type": "string",
                                                      "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$"
                                                  },
                                                  "addressLine3": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$",
                                                      "minLength": 0,
                                                      "maxLength": 35
                                                  },
                                                  "addressLine4": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$",
                                                      "minLength": 0,
                                                      "maxLength": 35
                                                  },
                                                  "postalCode": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "minLength": 0,
                                                      "maxLength": 10
                                                  },
                                                  "countryCode": {
                                                      "type": "string",
                                                      "enum": [
                                                          "GB"
                                                      ]
                                                  }
                                              },
                                              "required": [
                                                  "addressLine1",
                                                  "addressLine2",
                                                  "postalCode",
                                                  "countryCode"
                                              ],
                                              "additionalProperties": false
                                          },
                                          "foreignAddress": {
                                              "type": "object",
                                              "properties": {
                                                  "addressLine1": {
                                                      "type": "string",
                                                      "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$"
                                                  },
                                                  "addressLine2": {
                                                      "type": "string",
                                                      "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$"
                                                  },
                                                  "addressLine3": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$",
                                                      "minLength": 0,
                                                      "maxLength": 35
                                                  },
                                                  "addressLine4": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "pattern": "^[A-Za-z0-9 \\-,.&']{1,35}$",
                                                      "minLength": 0,
                                                      "maxLength": 35
                                                  },
                                                  "postalCode": {
                                                      "type": [
                                                          "string",
                                                          "null"
                                                      ],
                                                      "minLength": 0,
                                                      "maxLength": 10
                                                  },
                                                  "countryCode": {
                                                      "type": "string",
                                                      "enum": [
                                                          "AD",
                                                          "AE",
                                                          "AF",
                                                          "AG",
                                                          "AI",
                                                          "AL",
                                                          "AM",
                                                          "AN",
                                                          "AO",
                                                          "AQ",
                                                          "AR",
                                                          "AS",
                                                          "AT",
                                                          "AU",
                                                          "AW",
                                                          "AX",
                                                          "AZ",
                                                          "BA",
                                                          "BB",
                                                          "BD",
                                                          "BE",
                                                          "BF",
                                                          "BG",
                                                          "BH",
                                                          "BI",
                                                          "BJ",
                                                          "BM",
                                                          "BN",
                                                          "BO",
                                                          "BQ",
                                                          "BR",
                                                          "BS",
                                                          "BT",
                                                          "BV",
                                                          "BW",
                                                          "BY",
                                                          "BZ",
                                                          "CA",
                                                          "CC",
                                                          "CD",
                                                          "CF",
                                                          "CG",
                                                          "CH",
                                                          "CI",
                                                          "CK",
                                                          "CL",
                                                          "CM",
                                                          "CN",
                                                          "CO",
                                                          "CR",
                                                          "CS",
                                                          "CU",
                                                          "CV",
                                                          "CW",
                                                          "CX",
                                                          "CY",
                                                          "CZ",
                                                          "DE",
                                                          "DJ",
                                                          "DK",
                                                          "DM",
                                                          "DO",
                                                          "DZ",
                                                          "EC",
                                                          "EE",
                                                          "EG",
                                                          "EH",
                                                          "ER",
                                                          "ES",
                                                          "ET",
                                                          "EU",
                                                          "FI",
                                                          "FJ",
                                                          "FK",
                                                          "FM",
                                                          "FO",
                                                          "FR",
                                                          "GA",
                                                          "GD",
                                                          "GE",
                                                          "GF",
                                                          "GG",
                                                          "GH",
                                                          "GI",
                                                          "GL",
                                                          "GM",
                                                          "GN",
                                                          "GP",
                                                          "GQ",
                                                          "GR",
                                                          "GS",
                                                          "GT",
                                                          "GU",
                                                          "GW",
                                                          "GY",
                                                          "HK",
                                                          "HM",
                                                          "HN",
                                                          "HR",
                                                          "HT",
                                                          "HU",
                                                          "ID",
                                                          "IE",
                                                          "IL",
                                                          "IM",
                                                          "IN",
                                                          "IO",
                                                          "IQ",
                                                          "IR",
                                                          "IS",
                                                          "IT",
                                                          "JE",
                                                          "JM",
                                                          "JO",
                                                          "JP",
                                                          "KE",
                                                          "KG",
                                                          "KH",
                                                          "KI",
                                                          "KM",
                                                          "KN",
                                                          "KP",
                                                          "KR",
                                                          "KW",
                                                          "KY",
                                                          "KZ",
                                                          "LA",
                                                          "LB",
                                                          "LC",
                                                          "LI",
                                                          "LK",
                                                          "LR",
                                                          "LS",
                                                          "LT",
                                                          "LU",
                                                          "LV",
                                                          "LY",
                                                          "MA",
                                                          "MC",
                                                          "MD",
                                                          "ME",
                                                          "MF",
                                                          "MG",
                                                          "MH",
                                                          "MK",
                                                          "ML",
                                                          "MM",
                                                          "MN",
                                                          "MO",
                                                          "MP",
                                                          "MQ",
                                                          "MR",
                                                          "MS",
                                                          "MT",
                                                          "MU",
                                                          "MV",
                                                          "MW",
                                                          "MX",
                                                          "MY",
                                                          "MZ",
                                                          "NA",
                                                          "NC",
                                                          "NE",
                                                          "NF",
                                                          "NG",
                                                          "NI",
                                                          "NL",
                                                          "NO",
                                                          "NP",
                                                          "NR",
                                                          "NT",
                                                          "NU",
                                                          "NZ",
                                                          "OM",
                                                          "OR",
                                                          "PA",
                                                          "PE",
                                                          "PF",
                                                          "PG",
                                                          "PH",
                                                          "PK",
                                                          "PL",
                                                          "PM",
                                                          "PN",
                                                          "PR",
                                                          "PS",
                                                          "PT",
                                                          "PW",
                                                          "PY",
                                                          "QA",
                                                          "RE",
                                                          "RO",
                                                          "RS",
                                                          "RU",
                                                          "RW",
                                                          "SA",
                                                          "SB",
                                                          "SC",
                                                          "SD",
                                                          "SE",
                                                          "SG",
                                                          "SH",
                                                          "SI",
                                                          "SJ",
                                                          "SK",
                                                          "SL",
                                                          "SM",
                                                          "SN",
                                                          "SO",
                                                          "SR",
                                                          "SS",
                                                          "ST",
                                                          "SV",
                                                          "SX",
                                                          "SY",
                                                          "SZ",
                                                          "TC",
                                                          "TD",
                                                          "TF",
                                                          "TG",
                                                          "TH",
                                                          "TJ",
                                                          "TK",
                                                          "TL",
                                                          "TM",
                                                          "TN",
                                                          "TO",
                                                          "TP",
                                                          "TR",
                                                          "TT",
                                                          "TV",
                                                          "TW",
                                                          "TZ",
                                                          "UA",
                                                          "UG",
                                                          "UM",
                                                          "UN",
                                                          "US",
                                                          "UY",
                                                          "UZ",
                                                          "VA",
                                                          "VC",
                                                          "VE",
                                                          "VG",
                                                          "VI",
                                                          "VN",
                                                          "VU",
                                                          "WF",
                                                          "WS",
                                                          "YE",
                                                          "YT",
                                                          "ZA",
                                                          "ZM",
                                                          "ZW"
                                                      ]
                                                  }
                                              },
                                              "required": [
                                                  "addressLine1",
                                                  "addressLine2",
                                                  "countryCode"
                                              ],
                                              "additionalProperties": false
                                          }
                                      }
                                  }""")

  lazy val agentRelationshipCreateSchema = Json.parse(
    """{
      "$schema": "http://json-schema.org/draft-04/schema#",
      "title": "Digital to ETMP ROSM Agent Relationship Create / Update ",
      "description": "Digital to ETMP ROSM Agent Relationship Create / Update ",
      "type": "object",
      "properties": {
        "acknowledgmentReference": {
        "type": "string",
        "pattern": "^\\S{1,32}$"
      },
        "refNumber": {
        "type": "string",
        "pattern": "^[0-9A-Za-z]{15}$"
      },
        "agentReferenceNumber": {
        "type": "string",
        "pattern": "^[A-Z](ARN)[0-9]{7}$"
      },
        "regime": {
        "type": "string",
        "pattern": "^[A-Z]{3,10}$"
      },
        "authorisation": {
        "oneOf": [{
        "$ref": "#/definitions/authorise"
      },
      {
        "$ref": "#/definitions/deauthorise"
      }
        ]
      }
      },
      "required": [
      "refNumber",
      "agentReferenceNumber",
      "regime",
      "authorisation"
      ],
      "definitions": {
        "authorise": {
        "type": "object",
        "properties": {
        "action": {
        "type": "string",
        "enum": [
        "Authorise"
        ]
      },
        "isExclusiveAgent": {
        "type": "boolean"
      }
      },
        "required": [
        "action",
        "isExclusiveAgent"
        ]
      },
        "deauthorise": {
        "type": "object",
        "properties": {
        "action": {
        "type": "string",
        "enum": [
        "De-Authorise"
        ]
      }
      },
        "required": [
        "action"
        ]
      }
      }
    }"""
  )

}
