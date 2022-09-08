["^ ","~:resource-id",["~:shadow.build.classpath/resource","goog/positioning/menuanchoredposition.js"],"~:js","goog.provide(\"goog.positioning.MenuAnchoredPosition\");\ngoog.require(\"goog.positioning.AnchoredViewportPosition\");\ngoog.require(\"goog.positioning.Overflow\");\ngoog.positioning.MenuAnchoredPosition = function(anchorElement, corner, opt_adjust, opt_resize) {\n  goog.positioning.AnchoredViewportPosition.call(this, anchorElement, corner, opt_adjust || opt_resize);\n  if (opt_adjust || opt_resize) {\n    var overflowX = goog.positioning.Overflow.ADJUST_X_EXCEPT_OFFSCREEN;\n    var overflowY = opt_resize ? goog.positioning.Overflow.RESIZE_HEIGHT : goog.positioning.Overflow.ADJUST_Y_EXCEPT_OFFSCREEN;\n    this.setLastResortOverflow(overflowX | overflowY);\n  }\n};\ngoog.inherits(goog.positioning.MenuAnchoredPosition, goog.positioning.AnchoredViewportPosition);\n","~:source","// Copyright 2006 The Closure Library Authors. All Rights Reserved.\n//\n// Licensed under the Apache License, Version 2.0 (the \"License\");\n// you may not use this file except in compliance with the License.\n// You may obtain a copy of the License at\n//\n//      http://www.apache.org/licenses/LICENSE-2.0\n//\n// Unless required by applicable law or agreed to in writing, software\n// distributed under the License is distributed on an \"AS-IS\" BASIS,\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n// See the License for the specific language governing permissions and\n// limitations under the License.\n\n/**\n * @fileoverview Anchored viewport positioning class with both adjust and\n *     resize options for the popup.\n *\n * @author eae@google.com (Emil A Eklund)\n */\n\ngoog.provide('goog.positioning.MenuAnchoredPosition');\n\ngoog.require('goog.positioning.AnchoredViewportPosition');\ngoog.require('goog.positioning.Overflow');\n\n\n\n/**\n * Encapsulates a popup position where the popup is anchored at a corner of\n * an element.  The positioning behavior changes based on the values of\n * opt_adjust and opt_resize.\n *\n * When using this positioning object it's recommended that the movable element\n * be absolutely positioned.\n *\n * @param {Element} anchorElement Element the movable element should be\n *     anchored against.\n * @param {goog.positioning.Corner} corner Corner of anchored element the\n *     movable element should be positioned at.\n * @param {boolean=} opt_adjust Whether the positioning should be adjusted until\n *     the element fits inside the viewport even if that means that the anchored\n *     corners are ignored.\n * @param {boolean=} opt_resize Whether the positioning should be adjusted until\n *     the element fits inside the viewport on the X axis and its height is\n *     resized so if fits in the viewport. This take precedence over opt_adjust.\n * @constructor\n * @extends {goog.positioning.AnchoredViewportPosition}\n */\ngoog.positioning.MenuAnchoredPosition = function(\n    anchorElement, corner, opt_adjust, opt_resize) {\n  goog.positioning.AnchoredViewportPosition.call(\n      this, anchorElement, corner, opt_adjust || opt_resize);\n\n  if (opt_adjust || opt_resize) {\n    var overflowX = goog.positioning.Overflow.ADJUST_X_EXCEPT_OFFSCREEN;\n    var overflowY = opt_resize ?\n        goog.positioning.Overflow.RESIZE_HEIGHT :\n        goog.positioning.Overflow.ADJUST_Y_EXCEPT_OFFSCREEN;\n    this.setLastResortOverflow(overflowX | overflowY);\n  }\n};\ngoog.inherits(\n    goog.positioning.MenuAnchoredPosition,\n    goog.positioning.AnchoredViewportPosition);\n","~:compiled-at",1662647710519,"~:source-map-json","{\n\"version\":3,\n\"file\":\"goog.positioning.menuanchoredposition.js\",\n\"lineCount\":13,\n\"mappings\":\"AAqBAA,IAAA,CAAKC,OAAL,CAAa,uCAAb,CAAA;AAEAD,IAAA,CAAKE,OAAL,CAAa,2CAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,2BAAb,CAAA;AAyBAF,IAAA,CAAKG,WAAL,CAAiBC,oBAAjB,GAAwCC,QAAQ,CAC5CC,aAD4C,EAC7BC,MAD6B,EACrBC,UADqB,EACTC,UADS,CACG;AACjDT,MAAA,CAAKG,WAAL,CAAiBO,wBAAjB,CAA0CC,IAA1C,CACI,IADJ,EACUL,aADV,EACyBC,MADzB,EACiCC,UADjC,IAC+CC,UAD/C,CAAA;AAGA,MAAID,UAAJ,IAAkBC,UAAlB,CAA8B;AAC5B,QAAIG,YAAYZ,IAAZY,CAAiBT,WAAjBS,CAA6BC,QAA7BD,CAAsCE,yBAA1C;AACA,QAAIC,YAAYN,UAAA,GACZT,IADY,CACPG,WADO,CACKU,QADL,CACcG,aADd,GAEZhB,IAFY,CAEPG,WAFO,CAEKU,QAFL,CAEcI,yBAF9B;AAGA,QAAA,CAAKC,qBAAL,CAA2BN,SAA3B,GAAuCG,SAAvC,CAAA;AAL4B;AAJmB,CADnD;AAaAf,IAAA,CAAKmB,QAAL,CACInB,IADJ,CACSG,WADT,CACqBC,oBADrB,EAEIJ,IAFJ,CAESG,WAFT,CAEqBO,wBAFrB,CAAA;;\",\n\"sources\":[\"goog/positioning/menuanchoredposition.js\"],\n\"sourcesContent\":[\"// Copyright 2006 The Closure Library Authors. All Rights Reserved.\\n//\\n// Licensed under the Apache License, Version 2.0 (the \\\"License\\\");\\n// you may not use this file except in compliance with the License.\\n// You may obtain a copy of the License at\\n//\\n//      http://www.apache.org/licenses/LICENSE-2.0\\n//\\n// Unless required by applicable law or agreed to in writing, software\\n// distributed under the License is distributed on an \\\"AS-IS\\\" BASIS,\\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\n// See the License for the specific language governing permissions and\\n// limitations under the License.\\n\\n/**\\n * @fileoverview Anchored viewport positioning class with both adjust and\\n *     resize options for the popup.\\n *\\n * @author eae@google.com (Emil A Eklund)\\n */\\n\\ngoog.provide('goog.positioning.MenuAnchoredPosition');\\n\\ngoog.require('goog.positioning.AnchoredViewportPosition');\\ngoog.require('goog.positioning.Overflow');\\n\\n\\n\\n/**\\n * Encapsulates a popup position where the popup is anchored at a corner of\\n * an element.  The positioning behavior changes based on the values of\\n * opt_adjust and opt_resize.\\n *\\n * When using this positioning object it's recommended that the movable element\\n * be absolutely positioned.\\n *\\n * @param {Element} anchorElement Element the movable element should be\\n *     anchored against.\\n * @param {goog.positioning.Corner} corner Corner of anchored element the\\n *     movable element should be positioned at.\\n * @param {boolean=} opt_adjust Whether the positioning should be adjusted until\\n *     the element fits inside the viewport even if that means that the anchored\\n *     corners are ignored.\\n * @param {boolean=} opt_resize Whether the positioning should be adjusted until\\n *     the element fits inside the viewport on the X axis and its height is\\n *     resized so if fits in the viewport. This take precedence over opt_adjust.\\n * @constructor\\n * @extends {goog.positioning.AnchoredViewportPosition}\\n */\\ngoog.positioning.MenuAnchoredPosition = function(\\n    anchorElement, corner, opt_adjust, opt_resize) {\\n  goog.positioning.AnchoredViewportPosition.call(\\n      this, anchorElement, corner, opt_adjust || opt_resize);\\n\\n  if (opt_adjust || opt_resize) {\\n    var overflowX = goog.positioning.Overflow.ADJUST_X_EXCEPT_OFFSCREEN;\\n    var overflowY = opt_resize ?\\n        goog.positioning.Overflow.RESIZE_HEIGHT :\\n        goog.positioning.Overflow.ADJUST_Y_EXCEPT_OFFSCREEN;\\n    this.setLastResortOverflow(overflowX | overflowY);\\n  }\\n};\\ngoog.inherits(\\n    goog.positioning.MenuAnchoredPosition,\\n    goog.positioning.AnchoredViewportPosition);\\n\"],\n\"names\":[\"goog\",\"provide\",\"require\",\"positioning\",\"MenuAnchoredPosition\",\"goog.positioning.MenuAnchoredPosition\",\"anchorElement\",\"corner\",\"opt_adjust\",\"opt_resize\",\"AnchoredViewportPosition\",\"call\",\"overflowX\",\"Overflow\",\"ADJUST_X_EXCEPT_OFFSCREEN\",\"overflowY\",\"RESIZE_HEIGHT\",\"ADJUST_Y_EXCEPT_OFFSCREEN\",\"setLastResortOverflow\",\"inherits\"]\n}\n"]