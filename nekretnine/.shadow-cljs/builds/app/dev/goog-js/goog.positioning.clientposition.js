["^ ","~:resource-id",["~:shadow.build.classpath/resource","goog/positioning/clientposition.js"],"~:js","goog.provide(\"goog.positioning.ClientPosition\");\ngoog.require(\"goog.asserts\");\ngoog.require(\"goog.dom\");\ngoog.require(\"goog.math.Coordinate\");\ngoog.require(\"goog.positioning\");\ngoog.require(\"goog.positioning.AbstractPosition\");\ngoog.require(\"goog.style\");\ngoog.positioning.ClientPosition = function(arg1, opt_arg2) {\n  this.coordinate = arg1 instanceof goog.math.Coordinate ? arg1 : new goog.math.Coordinate(arg1, opt_arg2);\n};\ngoog.inherits(goog.positioning.ClientPosition, goog.positioning.AbstractPosition);\ngoog.positioning.ClientPosition.prototype.reposition = function(movableElement, movableElementCorner, opt_margin, opt_preferredSize) {\n  goog.asserts.assert(movableElement);\n  var viewportOffset = goog.style.getViewportPageOffset(goog.dom.getOwnerDocument(movableElement));\n  var x = this.coordinate.x + viewportOffset.x;\n  var y = this.coordinate.y + viewportOffset.y;\n  var movableParentTopLeft = goog.positioning.getOffsetParentPageOffset(movableElement);\n  x -= movableParentTopLeft.x;\n  y -= movableParentTopLeft.y;\n  goog.positioning.positionAtCoordinate(new goog.math.Coordinate(x, y), movableElement, movableElementCorner, opt_margin, null, null, opt_preferredSize);\n};\n","~:source","// Copyright 2006 The Closure Library Authors. All Rights Reserved.\n//\n// Licensed under the Apache License, Version 2.0 (the \"License\");\n// you may not use this file except in compliance with the License.\n// You may obtain a copy of the License at\n//\n//      http://www.apache.org/licenses/LICENSE-2.0\n//\n// Unless required by applicable law or agreed to in writing, software\n// distributed under the License is distributed on an \"AS-IS\" BASIS,\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n// See the License for the specific language governing permissions and\n// limitations under the License.\n\n/**\n * @fileoverview Client positioning class.\n *\n * @author eae@google.com (Emil A Eklund)\n * @author chrishenry@google.com (Chris Henry)\n */\n\ngoog.provide('goog.positioning.ClientPosition');\n\ngoog.require('goog.asserts');\ngoog.require('goog.dom');\ngoog.require('goog.math.Coordinate');\ngoog.require('goog.positioning');\ngoog.require('goog.positioning.AbstractPosition');\ngoog.require('goog.style');\n\n\n\n/**\n * Encapsulates a popup position where the popup is positioned relative to the\n * window (client) coordinates. This calculates the correct position to\n * use even if the element is relatively positioned to some other element. This\n * is for trying to position an element at the spot of the mouse cursor in\n * a MOUSEMOVE event. Just use the event.clientX and event.clientY as the\n * parameters.\n *\n * @param {number|goog.math.Coordinate} arg1 Left position or coordinate.\n * @param {number=} opt_arg2 Top position.\n * @constructor\n * @extends {goog.positioning.AbstractPosition}\n */\ngoog.positioning.ClientPosition = function(arg1, opt_arg2) {\n  /**\n   * Coordinate to position popup at.\n   * @type {!goog.math.Coordinate}\n   */\n  this.coordinate = arg1 instanceof goog.math.Coordinate ?\n      arg1 :\n      new goog.math.Coordinate(/** @type {number} */ (arg1), opt_arg2);\n};\ngoog.inherits(\n    goog.positioning.ClientPosition, goog.positioning.AbstractPosition);\n\n\n/**\n * Repositions the popup according to the current state\n *\n * @param {Element} movableElement The DOM element of the popup.\n * @param {goog.positioning.Corner} movableElementCorner The corner of\n *     the popup element that that should be positioned adjacent to\n *     the anchorElement.  One of the goog.positioning.Corner\n *     constants.\n * @param {goog.math.Box=} opt_margin A margin specified in pixels.\n * @param {goog.math.Size=} opt_preferredSize Preferred size of the element.\n * @override\n */\ngoog.positioning.ClientPosition.prototype.reposition = function(\n    movableElement, movableElementCorner, opt_margin, opt_preferredSize) {\n  goog.asserts.assert(movableElement);\n\n  // Translates the coordinate to be relative to the page.\n  var viewportOffset = goog.style.getViewportPageOffset(\n      goog.dom.getOwnerDocument(movableElement));\n  var x = this.coordinate.x + viewportOffset.x;\n  var y = this.coordinate.y + viewportOffset.y;\n\n  // Translates the coordinate to be relative to the offset parent.\n  var movableParentTopLeft =\n      goog.positioning.getOffsetParentPageOffset(movableElement);\n  x -= movableParentTopLeft.x;\n  y -= movableParentTopLeft.y;\n\n  goog.positioning.positionAtCoordinate(\n      new goog.math.Coordinate(x, y), movableElement, movableElementCorner,\n      opt_margin, null, null, opt_preferredSize);\n};\n","~:compiled-at",1662647710519,"~:source-map-json","{\n\"version\":3,\n\"file\":\"goog.positioning.clientposition.js\",\n\"lineCount\":22,\n\"mappings\":\"AAqBAA,IAAA,CAAKC,OAAL,CAAa,iCAAb,CAAA;AAEAD,IAAA,CAAKE,OAAL,CAAa,cAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,UAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,sBAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,kBAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,mCAAb,CAAA;AACAF,IAAA,CAAKE,OAAL,CAAa,YAAb,CAAA;AAiBAF,IAAA,CAAKG,WAAL,CAAiBC,cAAjB,GAAkCC,QAAQ,CAACC,IAAD,EAAOC,QAAP,CAAiB;AAKzD,MAAA,CAAKC,UAAL,GAAkBF,IAAA,YAAgBN,IAAhB,CAAqBS,IAArB,CAA0BC,UAA1B,GACdJ,IADc,GAEd,IAAIN,IAAJ,CAASS,IAAT,CAAcC,UAAd,CAAgDJ,IAAhD,EAAuDC,QAAvD,CAFJ;AALyD,CAA3D;AASAP,IAAA,CAAKW,QAAL,CACIX,IADJ,CACSG,WADT,CACqBC,cADrB,EACqCJ,IADrC,CAC0CG,WAD1C,CACsDS,gBADtD,CAAA;AAgBAZ,IAAA,CAAKG,WAAL,CAAiBC,cAAjB,CAAgCS,SAAhC,CAA0CC,UAA1C,GAAuDC,QAAQ,CAC3DC,cAD2D,EAC3CC,oBAD2C,EACrBC,UADqB,EACTC,iBADS,CACU;AACvEnB,MAAA,CAAKoB,OAAL,CAAaC,MAAb,CAAoBL,cAApB,CAAA;AAGA,MAAIM,iBAAiBtB,IAAA,CAAKuB,KAAL,CAAWC,qBAAX,CACjBxB,IAAA,CAAKyB,GAAL,CAASC,gBAAT,CAA0BV,cAA1B,CADiB,CAArB;AAEA,MAAIW,IAAI,IAAJA,CAASnB,UAATmB,CAAoBA,CAApBA,GAAwBL,cAAxBK,CAAuCA,CAA3C;AACA,MAAIC,IAAI,IAAJA,CAASpB,UAAToB,CAAoBA,CAApBA,GAAwBN,cAAxBM,CAAuCA,CAA3C;AAGA,MAAIC,uBACA7B,IAAA,CAAKG,WAAL,CAAiB2B,yBAAjB,CAA2Cd,cAA3C,CADJ;AAEAW,GAAA,IAAKE,oBAAL,CAA0BF,CAA1B;AACAC,GAAA,IAAKC,oBAAL,CAA0BD,CAA1B;AAEA5B,MAAA,CAAKG,WAAL,CAAiB4B,oBAAjB,CACI,IAAI/B,IAAJ,CAASS,IAAT,CAAcC,UAAd,CAAyBiB,CAAzB,EAA4BC,CAA5B,CADJ,EACoCZ,cADpC,EACoDC,oBADpD,EAEIC,UAFJ,EAEgB,IAFhB,EAEsB,IAFtB,EAE4BC,iBAF5B,CAAA;AAfuE,CADzE;;\",\n\"sources\":[\"goog/positioning/clientposition.js\"],\n\"sourcesContent\":[\"// Copyright 2006 The Closure Library Authors. All Rights Reserved.\\n//\\n// Licensed under the Apache License, Version 2.0 (the \\\"License\\\");\\n// you may not use this file except in compliance with the License.\\n// You may obtain a copy of the License at\\n//\\n//      http://www.apache.org/licenses/LICENSE-2.0\\n//\\n// Unless required by applicable law or agreed to in writing, software\\n// distributed under the License is distributed on an \\\"AS-IS\\\" BASIS,\\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\n// See the License for the specific language governing permissions and\\n// limitations under the License.\\n\\n/**\\n * @fileoverview Client positioning class.\\n *\\n * @author eae@google.com (Emil A Eklund)\\n * @author chrishenry@google.com (Chris Henry)\\n */\\n\\ngoog.provide('goog.positioning.ClientPosition');\\n\\ngoog.require('goog.asserts');\\ngoog.require('goog.dom');\\ngoog.require('goog.math.Coordinate');\\ngoog.require('goog.positioning');\\ngoog.require('goog.positioning.AbstractPosition');\\ngoog.require('goog.style');\\n\\n\\n\\n/**\\n * Encapsulates a popup position where the popup is positioned relative to the\\n * window (client) coordinates. This calculates the correct position to\\n * use even if the element is relatively positioned to some other element. This\\n * is for trying to position an element at the spot of the mouse cursor in\\n * a MOUSEMOVE event. Just use the event.clientX and event.clientY as the\\n * parameters.\\n *\\n * @param {number|goog.math.Coordinate} arg1 Left position or coordinate.\\n * @param {number=} opt_arg2 Top position.\\n * @constructor\\n * @extends {goog.positioning.AbstractPosition}\\n */\\ngoog.positioning.ClientPosition = function(arg1, opt_arg2) {\\n  /**\\n   * Coordinate to position popup at.\\n   * @type {!goog.math.Coordinate}\\n   */\\n  this.coordinate = arg1 instanceof goog.math.Coordinate ?\\n      arg1 :\\n      new goog.math.Coordinate(/** @type {number} */ (arg1), opt_arg2);\\n};\\ngoog.inherits(\\n    goog.positioning.ClientPosition, goog.positioning.AbstractPosition);\\n\\n\\n/**\\n * Repositions the popup according to the current state\\n *\\n * @param {Element} movableElement The DOM element of the popup.\\n * @param {goog.positioning.Corner} movableElementCorner The corner of\\n *     the popup element that that should be positioned adjacent to\\n *     the anchorElement.  One of the goog.positioning.Corner\\n *     constants.\\n * @param {goog.math.Box=} opt_margin A margin specified in pixels.\\n * @param {goog.math.Size=} opt_preferredSize Preferred size of the element.\\n * @override\\n */\\ngoog.positioning.ClientPosition.prototype.reposition = function(\\n    movableElement, movableElementCorner, opt_margin, opt_preferredSize) {\\n  goog.asserts.assert(movableElement);\\n\\n  // Translates the coordinate to be relative to the page.\\n  var viewportOffset = goog.style.getViewportPageOffset(\\n      goog.dom.getOwnerDocument(movableElement));\\n  var x = this.coordinate.x + viewportOffset.x;\\n  var y = this.coordinate.y + viewportOffset.y;\\n\\n  // Translates the coordinate to be relative to the offset parent.\\n  var movableParentTopLeft =\\n      goog.positioning.getOffsetParentPageOffset(movableElement);\\n  x -= movableParentTopLeft.x;\\n  y -= movableParentTopLeft.y;\\n\\n  goog.positioning.positionAtCoordinate(\\n      new goog.math.Coordinate(x, y), movableElement, movableElementCorner,\\n      opt_margin, null, null, opt_preferredSize);\\n};\\n\"],\n\"names\":[\"goog\",\"provide\",\"require\",\"positioning\",\"ClientPosition\",\"goog.positioning.ClientPosition\",\"arg1\",\"opt_arg2\",\"coordinate\",\"math\",\"Coordinate\",\"inherits\",\"AbstractPosition\",\"prototype\",\"reposition\",\"goog.positioning.ClientPosition.prototype.reposition\",\"movableElement\",\"movableElementCorner\",\"opt_margin\",\"opt_preferredSize\",\"asserts\",\"assert\",\"viewportOffset\",\"style\",\"getViewportPageOffset\",\"dom\",\"getOwnerDocument\",\"x\",\"y\",\"movableParentTopLeft\",\"getOffsetParentPageOffset\",\"positionAtCoordinate\"]\n}\n"]