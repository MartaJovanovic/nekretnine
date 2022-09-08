["^ ","~:resource-id",["~:shadow.build.classpath/resource","goog/ui/componentutil.js"],"~:js","goog.provide(\"goog.ui.ComponentUtil\");\ngoog.require(\"goog.events.MouseAsMouseEventType\");\ngoog.require(\"goog.events.MouseEvents\");\ngoog.require(\"goog.events.PointerAsMouseEventType\");\ngoog.ui.ComponentUtil.getMouseEventType = function(component) {\n  return component.pointerEventsEnabled() ? goog.events.PointerAsMouseEventType : goog.events.MouseAsMouseEventType;\n};\n","~:source","// Copyright 2018 The Closure Library Authors. All Rights Reserved.\n//\n// Licensed under the Apache License, Version 2.0 (the \"License\");\n// you may not use this file except in compliance with the License.\n// You may obtain a copy of the License at\n//\n//      http://www.apache.org/licenses/LICENSE-2.0\n//\n// Unless required by applicable law or agreed to in writing, software\n// distributed under the License is distributed on an \"AS-IS\" BASIS,\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n// See the License for the specific language governing permissions and\n// limitations under the License.\n\n/**\n * @fileoverview Static utility methods for UI components.\n */\n\ngoog.provide('goog.ui.ComponentUtil');\n\ngoog.require('goog.events.MouseAsMouseEventType');\ngoog.require('goog.events.MouseEvents');\ngoog.require('goog.events.PointerAsMouseEventType');\n\n\n\n/**\n * @param {!goog.ui.Component} component\n * @return {!goog.events.MouseEvents} The browser events that should be listened\n *     to for the given mouse events.\n */\ngoog.ui.ComponentUtil.getMouseEventType = function(component) {\n  return component.pointerEventsEnabled() ?\n      goog.events.PointerAsMouseEventType :\n      goog.events.MouseAsMouseEventType;\n};\n","~:compiled-at",1662647710569,"~:source-map-json","{\n\"version\":3,\n\"file\":\"goog.ui.componentutil.js\",\n\"lineCount\":8,\n\"mappings\":\"AAkBAA,IAAA,CAAKC,OAAL,CAAa,uBAAb,CAAA;AAEAD,IAAA,CAAKE,OAAL,CAAa,mCAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,yBAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,qCAAb,CAAA;AASAF,IAAA,CAAKG,EAAL,CAAQC,aAAR,CAAsBC,iBAAtB,GAA0CC,QAAQ,CAACC,SAAD,CAAY;AAC5D,SAAOA,SAAA,CAAUC,oBAAV,EAAA,GACHR,IADG,CACES,MADF,CACSC,uBADT,GAEHV,IAFG,CAEES,MAFF,CAESE,qBAFhB;AAD4D,CAA9D;;\",\n\"sources\":[\"goog/ui/componentutil.js\"],\n\"sourcesContent\":[\"// Copyright 2018 The Closure Library Authors. All Rights Reserved.\\n//\\n// Licensed under the Apache License, Version 2.0 (the \\\"License\\\");\\n// you may not use this file except in compliance with the License.\\n// You may obtain a copy of the License at\\n//\\n//      http://www.apache.org/licenses/LICENSE-2.0\\n//\\n// Unless required by applicable law or agreed to in writing, software\\n// distributed under the License is distributed on an \\\"AS-IS\\\" BASIS,\\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\n// See the License for the specific language governing permissions and\\n// limitations under the License.\\n\\n/**\\n * @fileoverview Static utility methods for UI components.\\n */\\n\\ngoog.provide('goog.ui.ComponentUtil');\\n\\ngoog.require('goog.events.MouseAsMouseEventType');\\ngoog.require('goog.events.MouseEvents');\\ngoog.require('goog.events.PointerAsMouseEventType');\\n\\n\\n\\n/**\\n * @param {!goog.ui.Component} component\\n * @return {!goog.events.MouseEvents} The browser events that should be listened\\n *     to for the given mouse events.\\n */\\ngoog.ui.ComponentUtil.getMouseEventType = function(component) {\\n  return component.pointerEventsEnabled() ?\\n      goog.events.PointerAsMouseEventType :\\n      goog.events.MouseAsMouseEventType;\\n};\\n\"],\n\"names\":[\"goog\",\"provide\",\"require\",\"ui\",\"ComponentUtil\",\"getMouseEventType\",\"goog.ui.ComponentUtil.getMouseEventType\",\"component\",\"pointerEventsEnabled\",\"events\",\"PointerAsMouseEventType\",\"MouseAsMouseEventType\"]\n}\n"]