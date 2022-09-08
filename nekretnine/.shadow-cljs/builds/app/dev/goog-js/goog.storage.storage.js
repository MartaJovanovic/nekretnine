["^ ","~:resource-id",["~:shadow.build.classpath/resource","goog/storage/storage.js"],"~:js","goog.provide(\"goog.storage.Storage\");\ngoog.forwardDeclare(\"goog.storage.mechanism.Mechanism\");\ngoog.require(\"goog.json\");\ngoog.require(\"goog.storage.ErrorCode\");\ngoog.storage.Storage = function(mechanism) {\n  this.mechanism = mechanism;\n};\ngoog.storage.Storage.prototype.set = function(key, value) {\n  if (value === undefined) {\n    this.mechanism.remove(key);\n    return;\n  }\n  this.mechanism.set(key, goog.json.serialize(value));\n};\ngoog.storage.Storage.prototype.get = function(key) {\n  var json;\n  try {\n    json = this.mechanism.get(key);\n  } catch (e) {\n    return undefined;\n  }\n  if (json === null) {\n    return undefined;\n  }\n  try {\n    return JSON.parse(json);\n  } catch (e$5) {\n    throw goog.storage.ErrorCode.INVALID_VALUE;\n  }\n};\ngoog.storage.Storage.prototype.remove = function(key) {\n  this.mechanism.remove(key);\n};\n","~:source","// Copyright 2011 The Closure Library Authors. All Rights Reserved.\n//\n// Licensed under the Apache License, Version 2.0 (the \"License\");\n// you may not use this file except in compliance with the License.\n// You may obtain a copy of the License at\n//\n//      http://www.apache.org/licenses/LICENSE-2.0\n//\n// Unless required by applicable law or agreed to in writing, software\n// distributed under the License is distributed on an \"AS-IS\" BASIS,\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n// See the License for the specific language governing permissions and\n// limitations under the License.\n\n/**\n * @fileoverview Provides a convenient API for data persistence using a selected\n * data storage mechanism.\n *\n */\n\ngoog.provide('goog.storage.Storage');\n\ngoog.forwardDeclare('goog.storage.mechanism.Mechanism');\ngoog.require('goog.json');\ngoog.require('goog.storage.ErrorCode');\n\n\n\n/**\n * The base implementation for all storage APIs.\n *\n * @param {!goog.storage.mechanism.Mechanism} mechanism The underlying\n *     storage mechanism.\n * @constructor\n * @struct\n */\ngoog.storage.Storage = function(mechanism) {\n  /**\n   * The mechanism used to persist key-value pairs.\n   *\n   * @protected {goog.storage.mechanism.Mechanism}\n   */\n  this.mechanism = mechanism;\n};\n\n\n/**\n * Sets an item in the data storage.\n *\n * @param {string} key The key to set.\n * @param {*} value The value to serialize to a string and save.\n */\ngoog.storage.Storage.prototype.set = function(key, value) {\n  if (value === undefined) {\n    this.mechanism.remove(key);\n    return;\n  }\n  this.mechanism.set(key, goog.json.serialize(value));\n};\n\n\n/**\n * Gets an item from the data storage.\n *\n * @param {string} key The key to get.\n * @return {*} Deserialized value or undefined if not found.\n */\ngoog.storage.Storage.prototype.get = function(key) {\n  var json;\n  try {\n    json = this.mechanism.get(key);\n  } catch (e) {\n    // If, for any reason, the value returned by a mechanism's get method is not\n    // a string, an exception is thrown.  In this case, we must fail gracefully\n    // instead of propagating the exception to clients.  See b/8095488 for\n    // details.\n    return undefined;\n  }\n  if (json === null) {\n    return undefined;\n  }\n\n  try {\n    return JSON.parse(json);\n  } catch (e) {\n    throw goog.storage.ErrorCode.INVALID_VALUE;\n  }\n};\n\n\n/**\n * Removes an item from the data storage.\n *\n * @param {string} key The key to remove.\n */\ngoog.storage.Storage.prototype.remove = function(key) {\n  this.mechanism.remove(key);\n};\n","~:compiled-at",1662647710473,"~:source-map-json","{\n\"version\":3,\n\"file\":\"goog.storage.storage.js\",\n\"lineCount\":34,\n\"mappings\":\"AAoBAA,IAAA,CAAKC,OAAL,CAAa,sBAAb,CAAA;AAEAD,IAAA,CAAKE,cAAL,CAAoB,kCAApB,CAAA;AACAF,IAAA,CAAKG,OAAL,CAAa,WAAb,CAAA;AACAH,IAAA,CAAKG,OAAL,CAAa,wBAAb,CAAA;AAYAH,IAAA,CAAKI,OAAL,CAAaC,OAAb,GAAuBC,QAAQ,CAACC,SAAD,CAAY;AAMzC,MAAA,CAAKA,SAAL,GAAiBA,SAAjB;AANyC,CAA3C;AAgBAP,IAAA,CAAKI,OAAL,CAAaC,OAAb,CAAqBG,SAArB,CAA+BC,GAA/B,GAAqCC,QAAQ,CAACC,GAAD,EAAMC,KAAN,CAAa;AACxD,MAAIA,KAAJ,KAAcC,SAAd,CAAyB;AACvB,QAAA,CAAKN,SAAL,CAAeO,MAAf,CAAsBH,GAAtB,CAAA;AACA;AAFuB;AAIzB,MAAA,CAAKJ,SAAL,CAAeE,GAAf,CAAmBE,GAAnB,EAAwBX,IAAA,CAAKe,IAAL,CAAUC,SAAV,CAAoBJ,KAApB,CAAxB,CAAA;AALwD,CAA1D;AAeAZ,IAAA,CAAKI,OAAL,CAAaC,OAAb,CAAqBG,SAArB,CAA+BS,GAA/B,GAAqCC,QAAQ,CAACP,GAAD,CAAM;AACjD,MAAII,IAAJ;AACA,KAAI;AACFA,QAAA,GAAO,IAAA,CAAKR,SAAL,CAAeU,GAAf,CAAmBN,GAAnB,CAAP;AADE,GAEF,QAAOQ,CAAP,CAAU;AAKV,WAAON,SAAP;AALU;AAOZ,MAAIE,IAAJ,KAAa,IAAb;AACE,WAAOF,SAAP;AADF;AAIA,KAAI;AACF,WAAOO,IAAA,CAAKC,KAAL,CAAWN,IAAX,CAAP;AADE,GAEF,QAAOI,GAAP,CAAU;AACV,UAAMnB,IAAN,CAAWI,OAAX,CAAmBkB,SAAnB,CAA6BC,aAA7B;AADU;AAjBqC,CAAnD;AA4BAvB,IAAA,CAAKI,OAAL,CAAaC,OAAb,CAAqBG,SAArB,CAA+BM,MAA/B,GAAwCU,QAAQ,CAACb,GAAD,CAAM;AACpD,MAAA,CAAKJ,SAAL,CAAeO,MAAf,CAAsBH,GAAtB,CAAA;AADoD,CAAtD;;\",\n\"sources\":[\"goog/storage/storage.js\"],\n\"sourcesContent\":[\"// Copyright 2011 The Closure Library Authors. All Rights Reserved.\\n//\\n// Licensed under the Apache License, Version 2.0 (the \\\"License\\\");\\n// you may not use this file except in compliance with the License.\\n// You may obtain a copy of the License at\\n//\\n//      http://www.apache.org/licenses/LICENSE-2.0\\n//\\n// Unless required by applicable law or agreed to in writing, software\\n// distributed under the License is distributed on an \\\"AS-IS\\\" BASIS,\\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\n// See the License for the specific language governing permissions and\\n// limitations under the License.\\n\\n/**\\n * @fileoverview Provides a convenient API for data persistence using a selected\\n * data storage mechanism.\\n *\\n */\\n\\ngoog.provide('goog.storage.Storage');\\n\\ngoog.forwardDeclare('goog.storage.mechanism.Mechanism');\\ngoog.require('goog.json');\\ngoog.require('goog.storage.ErrorCode');\\n\\n\\n\\n/**\\n * The base implementation for all storage APIs.\\n *\\n * @param {!goog.storage.mechanism.Mechanism} mechanism The underlying\\n *     storage mechanism.\\n * @constructor\\n * @struct\\n */\\ngoog.storage.Storage = function(mechanism) {\\n  /**\\n   * The mechanism used to persist key-value pairs.\\n   *\\n   * @protected {goog.storage.mechanism.Mechanism}\\n   */\\n  this.mechanism = mechanism;\\n};\\n\\n\\n/**\\n * Sets an item in the data storage.\\n *\\n * @param {string} key The key to set.\\n * @param {*} value The value to serialize to a string and save.\\n */\\ngoog.storage.Storage.prototype.set = function(key, value) {\\n  if (value === undefined) {\\n    this.mechanism.remove(key);\\n    return;\\n  }\\n  this.mechanism.set(key, goog.json.serialize(value));\\n};\\n\\n\\n/**\\n * Gets an item from the data storage.\\n *\\n * @param {string} key The key to get.\\n * @return {*} Deserialized value or undefined if not found.\\n */\\ngoog.storage.Storage.prototype.get = function(key) {\\n  var json;\\n  try {\\n    json = this.mechanism.get(key);\\n  } catch (e) {\\n    // If, for any reason, the value returned by a mechanism's get method is not\\n    // a string, an exception is thrown.  In this case, we must fail gracefully\\n    // instead of propagating the exception to clients.  See b/8095488 for\\n    // details.\\n    return undefined;\\n  }\\n  if (json === null) {\\n    return undefined;\\n  }\\n\\n  try {\\n    return JSON.parse(json);\\n  } catch (e) {\\n    throw goog.storage.ErrorCode.INVALID_VALUE;\\n  }\\n};\\n\\n\\n/**\\n * Removes an item from the data storage.\\n *\\n * @param {string} key The key to remove.\\n */\\ngoog.storage.Storage.prototype.remove = function(key) {\\n  this.mechanism.remove(key);\\n};\\n\"],\n\"names\":[\"goog\",\"provide\",\"forwardDeclare\",\"require\",\"storage\",\"Storage\",\"goog.storage.Storage\",\"mechanism\",\"prototype\",\"set\",\"goog.storage.Storage.prototype.set\",\"key\",\"value\",\"undefined\",\"remove\",\"json\",\"serialize\",\"get\",\"goog.storage.Storage.prototype.get\",\"e\",\"JSON\",\"parse\",\"ErrorCode\",\"INVALID_VALUE\",\"goog.storage.Storage.prototype.remove\"]\n}\n"]