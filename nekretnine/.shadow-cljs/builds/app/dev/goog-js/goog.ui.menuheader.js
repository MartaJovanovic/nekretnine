["^ ","~:resource-id",["~:shadow.build.classpath/resource","goog/ui/menuheader.js"],"~:js","goog.provide(\"goog.ui.MenuHeader\");\ngoog.require(\"goog.ui.Component\");\ngoog.require(\"goog.ui.Control\");\ngoog.require(\"goog.ui.MenuHeaderRenderer\");\ngoog.require(\"goog.ui.registry\");\ngoog.ui.MenuHeader = function(content, opt_domHelper, opt_renderer) {\n  goog.ui.Control.call(this, content, opt_renderer || goog.ui.MenuHeaderRenderer.getInstance(), opt_domHelper);\n  this.setSupportedState(goog.ui.Component.State.DISABLED, false);\n  this.setSupportedState(goog.ui.Component.State.HOVER, false);\n  this.setSupportedState(goog.ui.Component.State.ACTIVE, false);\n  this.setSupportedState(goog.ui.Component.State.FOCUSED, false);\n  this.setStateInternal(goog.ui.Component.State.DISABLED);\n};\ngoog.inherits(goog.ui.MenuHeader, goog.ui.Control);\ngoog.ui.registry.setDecoratorByClassName(goog.ui.MenuHeaderRenderer.CSS_CLASS, function() {\n  return new goog.ui.MenuHeader(null);\n});\n","~:source","// Copyright 2007 The Closure Library Authors. All Rights Reserved.\n//\n// Licensed under the Apache License, Version 2.0 (the \"License\");\n// you may not use this file except in compliance with the License.\n// You may obtain a copy of the License at\n//\n//      http://www.apache.org/licenses/LICENSE-2.0\n//\n// Unless required by applicable law or agreed to in writing, software\n// distributed under the License is distributed on an \"AS-IS\" BASIS,\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n// See the License for the specific language governing permissions and\n// limitations under the License.\n\n/**\n * @fileoverview A class for representing menu headers.\n * @see goog.ui.Menu\n *\n */\n\ngoog.provide('goog.ui.MenuHeader');\n\ngoog.require('goog.ui.Component');\ngoog.require('goog.ui.Control');\ngoog.require('goog.ui.MenuHeaderRenderer');\ngoog.require('goog.ui.registry');\n\n\n\n/**\n * Class representing a menu header.\n * @param {goog.ui.ControlContent} content Text caption or DOM structure to\n *     display as the content of the item (use to add icons or styling to\n *     menus).\n * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM helper used for\n *     document interactions.\n * @param {goog.ui.MenuHeaderRenderer=} opt_renderer Optional renderer.\n * @constructor\n * @extends {goog.ui.Control}\n */\ngoog.ui.MenuHeader = function(content, opt_domHelper, opt_renderer) {\n  goog.ui.Control.call(\n      this, content, opt_renderer || goog.ui.MenuHeaderRenderer.getInstance(),\n      opt_domHelper);\n\n  this.setSupportedState(goog.ui.Component.State.DISABLED, false);\n  this.setSupportedState(goog.ui.Component.State.HOVER, false);\n  this.setSupportedState(goog.ui.Component.State.ACTIVE, false);\n  this.setSupportedState(goog.ui.Component.State.FOCUSED, false);\n\n  // Headers are always considered disabled.\n  this.setStateInternal(goog.ui.Component.State.DISABLED);\n};\ngoog.inherits(goog.ui.MenuHeader, goog.ui.Control);\n\n\n// Register a decorator factory function for goog.ui.MenuHeaders.\ngoog.ui.registry.setDecoratorByClassName(\n    goog.ui.MenuHeaderRenderer.CSS_CLASS, function() {\n      // MenuHeader defaults to using MenuHeaderRenderer.\n      return new goog.ui.MenuHeader(null);\n    });\n","~:compiled-at",1662647710600,"~:source-map-json","{\n\"version\":3,\n\"file\":\"goog.ui.menuheader.js\",\n\"lineCount\":18,\n\"mappings\":\"AAoBAA,IAAA,CAAKC,OAAL,CAAa,oBAAb,CAAA;AAEAD,IAAA,CAAKE,OAAL,CAAa,mBAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,iBAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,4BAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,kBAAb,CAAA;AAeAF,IAAA,CAAKG,EAAL,CAAQC,UAAR,GAAqBC,QAAQ,CAACC,OAAD,EAAUC,aAAV,EAAyBC,YAAzB,CAAuC;AAClER,MAAA,CAAKG,EAAL,CAAQM,OAAR,CAAgBC,IAAhB,CACI,IADJ,EACUJ,OADV,EACmBE,YADnB,IACmCR,IAAA,CAAKG,EAAL,CAAQQ,kBAAR,CAA2BC,WAA3B,EADnC,EAEIL,aAFJ,CAAA;AAIA,MAAA,CAAKM,iBAAL,CAAuBb,IAAvB,CAA4BG,EAA5B,CAA+BW,SAA/B,CAAyCC,KAAzC,CAA+CC,QAA/C,EAAyD,KAAzD,CAAA;AACA,MAAA,CAAKH,iBAAL,CAAuBb,IAAvB,CAA4BG,EAA5B,CAA+BW,SAA/B,CAAyCC,KAAzC,CAA+CE,KAA/C,EAAsD,KAAtD,CAAA;AACA,MAAA,CAAKJ,iBAAL,CAAuBb,IAAvB,CAA4BG,EAA5B,CAA+BW,SAA/B,CAAyCC,KAAzC,CAA+CG,MAA/C,EAAuD,KAAvD,CAAA;AACA,MAAA,CAAKL,iBAAL,CAAuBb,IAAvB,CAA4BG,EAA5B,CAA+BW,SAA/B,CAAyCC,KAAzC,CAA+CI,OAA/C,EAAwD,KAAxD,CAAA;AAGA,MAAA,CAAKC,gBAAL,CAAsBpB,IAAtB,CAA2BG,EAA3B,CAA8BW,SAA9B,CAAwCC,KAAxC,CAA8CC,QAA9C,CAAA;AAXkE,CAApE;AAaAhB,IAAA,CAAKqB,QAAL,CAAcrB,IAAd,CAAmBG,EAAnB,CAAsBC,UAAtB,EAAkCJ,IAAlC,CAAuCG,EAAvC,CAA0CM,OAA1C,CAAA;AAIAT,IAAA,CAAKG,EAAL,CAAQmB,QAAR,CAAiBC,uBAAjB,CACIvB,IADJ,CACSG,EADT,CACYQ,kBADZ,CAC+Ba,SAD/B,EAC0C,QAAQ,EAAG;AAE/C,SAAO,IAAIxB,IAAJ,CAASG,EAAT,CAAYC,UAAZ,CAAuB,IAAvB,CAAP;AAF+C,CADrD,CAAA;;\",\n\"sources\":[\"goog/ui/menuheader.js\"],\n\"sourcesContent\":[\"// Copyright 2007 The Closure Library Authors. All Rights Reserved.\\n//\\n// Licensed under the Apache License, Version 2.0 (the \\\"License\\\");\\n// you may not use this file except in compliance with the License.\\n// You may obtain a copy of the License at\\n//\\n//      http://www.apache.org/licenses/LICENSE-2.0\\n//\\n// Unless required by applicable law or agreed to in writing, software\\n// distributed under the License is distributed on an \\\"AS-IS\\\" BASIS,\\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\n// See the License for the specific language governing permissions and\\n// limitations under the License.\\n\\n/**\\n * @fileoverview A class for representing menu headers.\\n * @see goog.ui.Menu\\n *\\n */\\n\\ngoog.provide('goog.ui.MenuHeader');\\n\\ngoog.require('goog.ui.Component');\\ngoog.require('goog.ui.Control');\\ngoog.require('goog.ui.MenuHeaderRenderer');\\ngoog.require('goog.ui.registry');\\n\\n\\n\\n/**\\n * Class representing a menu header.\\n * @param {goog.ui.ControlContent} content Text caption or DOM structure to\\n *     display as the content of the item (use to add icons or styling to\\n *     menus).\\n * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM helper used for\\n *     document interactions.\\n * @param {goog.ui.MenuHeaderRenderer=} opt_renderer Optional renderer.\\n * @constructor\\n * @extends {goog.ui.Control}\\n */\\ngoog.ui.MenuHeader = function(content, opt_domHelper, opt_renderer) {\\n  goog.ui.Control.call(\\n      this, content, opt_renderer || goog.ui.MenuHeaderRenderer.getInstance(),\\n      opt_domHelper);\\n\\n  this.setSupportedState(goog.ui.Component.State.DISABLED, false);\\n  this.setSupportedState(goog.ui.Component.State.HOVER, false);\\n  this.setSupportedState(goog.ui.Component.State.ACTIVE, false);\\n  this.setSupportedState(goog.ui.Component.State.FOCUSED, false);\\n\\n  // Headers are always considered disabled.\\n  this.setStateInternal(goog.ui.Component.State.DISABLED);\\n};\\ngoog.inherits(goog.ui.MenuHeader, goog.ui.Control);\\n\\n\\n// Register a decorator factory function for goog.ui.MenuHeaders.\\ngoog.ui.registry.setDecoratorByClassName(\\n    goog.ui.MenuHeaderRenderer.CSS_CLASS, function() {\\n      // MenuHeader defaults to using MenuHeaderRenderer.\\n      return new goog.ui.MenuHeader(null);\\n    });\\n\"],\n\"names\":[\"goog\",\"provide\",\"require\",\"ui\",\"MenuHeader\",\"goog.ui.MenuHeader\",\"content\",\"opt_domHelper\",\"opt_renderer\",\"Control\",\"call\",\"MenuHeaderRenderer\",\"getInstance\",\"setSupportedState\",\"Component\",\"State\",\"DISABLED\",\"HOVER\",\"ACTIVE\",\"FOCUSED\",\"setStateInternal\",\"inherits\",\"registry\",\"setDecoratorByClassName\",\"CSS_CLASS\"]\n}\n"]