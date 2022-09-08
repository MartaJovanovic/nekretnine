["^ ","~:resource-id",["~:shadow.build.classpath/resource","goog/positioning/anchoredviewportposition.js"],"~:js","goog.provide(\"goog.positioning.AnchoredViewportPosition\");\ngoog.require(\"goog.positioning\");\ngoog.require(\"goog.positioning.AnchoredPosition\");\ngoog.require(\"goog.positioning.Overflow\");\ngoog.require(\"goog.positioning.OverflowStatus\");\ngoog.positioning.AnchoredViewportPosition = function(anchorElement, corner, opt_adjust, opt_overflowConstraint) {\n  goog.positioning.AnchoredPosition.call(this, anchorElement, corner);\n  this.lastResortOverflow_ = opt_adjust ? goog.positioning.Overflow.ADJUST_X | goog.positioning.Overflow.ADJUST_Y : goog.positioning.Overflow.IGNORE;\n  this.overflowConstraint_ = opt_overflowConstraint || undefined;\n};\ngoog.inherits(goog.positioning.AnchoredViewportPosition, goog.positioning.AnchoredPosition);\ngoog.positioning.AnchoredViewportPosition.prototype.getOverflowConstraint = function() {\n  return this.overflowConstraint_;\n};\ngoog.positioning.AnchoredViewportPosition.prototype.setOverflowConstraint = function(overflowConstraint) {\n  this.overflowConstraint_ = overflowConstraint;\n};\ngoog.positioning.AnchoredViewportPosition.prototype.getLastResortOverflow = function() {\n  return this.lastResortOverflow_;\n};\ngoog.positioning.AnchoredViewportPosition.prototype.setLastResortOverflow = function(lastResortOverflow) {\n  this.lastResortOverflow_ = lastResortOverflow;\n};\ngoog.positioning.AnchoredViewportPosition.prototype.reposition = function(movableElement, movableCorner, opt_margin, opt_preferredSize) {\n  var status = goog.positioning.positionAtAnchor(this.element, this.corner, movableElement, movableCorner, null, opt_margin, goog.positioning.Overflow.FAIL_X | goog.positioning.Overflow.FAIL_Y, opt_preferredSize, this.overflowConstraint_);\n  if (status & goog.positioning.OverflowStatus.FAILED) {\n    var cornerFallback = this.adjustCorner(status, this.corner);\n    var movableCornerFallback = this.adjustCorner(status, movableCorner);\n    status = goog.positioning.positionAtAnchor(this.element, cornerFallback, movableElement, movableCornerFallback, null, opt_margin, goog.positioning.Overflow.FAIL_X | goog.positioning.Overflow.FAIL_Y, opt_preferredSize, this.overflowConstraint_);\n    if (status & goog.positioning.OverflowStatus.FAILED) {\n      cornerFallback = this.adjustCorner(status, cornerFallback);\n      movableCornerFallback = this.adjustCorner(status, movableCornerFallback);\n      goog.positioning.positionAtAnchor(this.element, cornerFallback, movableElement, movableCornerFallback, null, opt_margin, this.getLastResortOverflow(), opt_preferredSize, this.overflowConstraint_);\n    }\n  }\n};\ngoog.positioning.AnchoredViewportPosition.prototype.adjustCorner = function(status, corner) {\n  if (status & goog.positioning.OverflowStatus.FAILED_HORIZONTAL) {\n    corner = goog.positioning.flipCornerHorizontal(corner);\n  }\n  if (status & goog.positioning.OverflowStatus.FAILED_VERTICAL) {\n    corner = goog.positioning.flipCornerVertical(corner);\n  }\n  return corner;\n};\n","~:source","// Copyright 2006 The Closure Library Authors. All Rights Reserved.\n//\n// Licensed under the Apache License, Version 2.0 (the \"License\");\n// you may not use this file except in compliance with the License.\n// You may obtain a copy of the License at\n//\n//      http://www.apache.org/licenses/LICENSE-2.0\n//\n// Unless required by applicable law or agreed to in writing, software\n// distributed under the License is distributed on an \"AS-IS\" BASIS,\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n// See the License for the specific language governing permissions and\n// limitations under the License.\n\n/**\n * @fileoverview Anchored viewport positioning class.\n *\n * @author eae@google.com (Emil A Eklund)\n */\n\ngoog.provide('goog.positioning.AnchoredViewportPosition');\n\ngoog.require('goog.positioning');\ngoog.require('goog.positioning.AnchoredPosition');\ngoog.require('goog.positioning.Overflow');\ngoog.require('goog.positioning.OverflowStatus');\n\n\n\n/**\n * Encapsulates a popup position where the popup is anchored at a corner of\n * an element. The corners are swapped if dictated by the viewport. For instance\n * if a popup is anchored with its top left corner to the bottom left corner of\n * the anchor the popup is either displayed below the anchor (as specified) or\n * above it if there's not enough room to display it below.\n *\n * When using this positioning object it's recommended that the movable element\n * be absolutely positioned.\n *\n * @param {Element} anchorElement Element the movable element should be\n *     anchored against.\n * @param {goog.positioning.Corner} corner Corner of anchored element the\n *     movable element should be positioned at.\n * @param {boolean=} opt_adjust Whether the positioning should be adjusted until\n *     the element fits inside the viewport even if that means that the anchored\n *     corners are ignored.\n * @param {goog.math.Box=} opt_overflowConstraint Box object describing the\n *     dimensions in which the movable element could be shown.\n * @constructor\n * @extends {goog.positioning.AnchoredPosition}\n */\ngoog.positioning.AnchoredViewportPosition = function(\n    anchorElement, corner, opt_adjust, opt_overflowConstraint) {\n  goog.positioning.AnchoredPosition.call(this, anchorElement, corner);\n\n  /**\n   * The last resort algorithm to use if the algorithm can't fit inside\n   * the viewport.\n   *\n   * IGNORE = do nothing, just display at the preferred position.\n   *\n   * ADJUST_X | ADJUST_Y = Adjust until the element fits, even if that means\n   * that the anchored corners are ignored.\n   *\n   * @type {number}\n   * @private\n   */\n  this.lastResortOverflow_ = opt_adjust ? (goog.positioning.Overflow.ADJUST_X |\n                                           goog.positioning.Overflow.ADJUST_Y) :\n                                          goog.positioning.Overflow.IGNORE;\n\n  /**\n   * The dimensions in which the movable element could be shown.\n   * @type {goog.math.Box|undefined}\n   * @private\n   */\n  this.overflowConstraint_ = opt_overflowConstraint || undefined;\n};\ngoog.inherits(\n    goog.positioning.AnchoredViewportPosition,\n    goog.positioning.AnchoredPosition);\n\n\n/**\n * @return {goog.math.Box|undefined} The box object describing the\n *     dimensions in which the movable element will be shown.\n */\ngoog.positioning.AnchoredViewportPosition.prototype.getOverflowConstraint =\n    function() {\n  return this.overflowConstraint_;\n};\n\n\n/**\n * @param {goog.math.Box|undefined} overflowConstraint Box object describing the\n *     dimensions in which the movable element could be shown.\n */\ngoog.positioning.AnchoredViewportPosition.prototype.setOverflowConstraint =\n    function(overflowConstraint) {\n  this.overflowConstraint_ = overflowConstraint;\n};\n\n\n/**\n * @return {number} A bitmask for the \"last resort\" overflow.\n */\ngoog.positioning.AnchoredViewportPosition.prototype.getLastResortOverflow =\n    function() {\n  return this.lastResortOverflow_;\n};\n\n\n/**\n * @param {number} lastResortOverflow A bitmask for the \"last resort\" overflow,\n *     if we fail to fit the element on-screen.\n */\ngoog.positioning.AnchoredViewportPosition.prototype.setLastResortOverflow =\n    function(lastResortOverflow) {\n  this.lastResortOverflow_ = lastResortOverflow;\n};\n\n\n/**\n * Repositions the movable element.\n *\n * @param {Element} movableElement Element to position.\n * @param {goog.positioning.Corner} movableCorner Corner of the movable element\n *     that should be positioned adjacent to the anchored element.\n * @param {goog.math.Box=} opt_margin A margin specified in pixels.\n * @param {goog.math.Size=} opt_preferredSize The preferred size of the\n *     movableElement.\n * @override\n */\ngoog.positioning.AnchoredViewportPosition.prototype.reposition = function(\n    movableElement, movableCorner, opt_margin, opt_preferredSize) {\n  var status = goog.positioning.positionAtAnchor(\n      this.element, this.corner, movableElement, movableCorner, null,\n      opt_margin,\n      goog.positioning.Overflow.FAIL_X | goog.positioning.Overflow.FAIL_Y,\n      opt_preferredSize, this.overflowConstraint_);\n\n  // If the desired position is outside the viewport try mirroring the corners\n  // horizontally or vertically.\n  if (status & goog.positioning.OverflowStatus.FAILED) {\n    var cornerFallback = this.adjustCorner(status, this.corner);\n    var movableCornerFallback = this.adjustCorner(status, movableCorner);\n\n    status = goog.positioning.positionAtAnchor(\n        this.element, cornerFallback, movableElement, movableCornerFallback,\n        null, opt_margin,\n        goog.positioning.Overflow.FAIL_X | goog.positioning.Overflow.FAIL_Y,\n        opt_preferredSize, this.overflowConstraint_);\n\n    if (status & goog.positioning.OverflowStatus.FAILED) {\n      // If that also fails, pick the best corner from the two tries,\n      // and adjust the position until it fits.\n      cornerFallback = this.adjustCorner(status, cornerFallback);\n      movableCornerFallback = this.adjustCorner(status, movableCornerFallback);\n\n      goog.positioning.positionAtAnchor(\n          this.element, cornerFallback, movableElement, movableCornerFallback,\n          null, opt_margin, this.getLastResortOverflow(), opt_preferredSize,\n          this.overflowConstraint_);\n    }\n  }\n};\n\n\n/**\n * Adjusts the corner if X or Y positioning failed.\n * @param {number} status The status of the last positionAtAnchor call.\n * @param {goog.positioning.Corner} corner The corner to adjust.\n * @return {goog.positioning.Corner} The adjusted corner.\n * @protected\n */\ngoog.positioning.AnchoredViewportPosition.prototype.adjustCorner = function(\n    status, corner) {\n  if (status & goog.positioning.OverflowStatus.FAILED_HORIZONTAL) {\n    corner = goog.positioning.flipCornerHorizontal(corner);\n  }\n\n  if (status & goog.positioning.OverflowStatus.FAILED_VERTICAL) {\n    corner = goog.positioning.flipCornerVertical(corner);\n  }\n\n  return corner;\n};\n","~:compiled-at",1662647710519,"~:source-map-json","{\n\"version\":3,\n\"file\":\"goog.positioning.anchoredviewportposition.js\",\n\"lineCount\":46,\n\"mappings\":\"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\",\n\"sources\":[\"goog/positioning/anchoredviewportposition.js\"],\n\"sourcesContent\":[\"// Copyright 2006 The Closure Library Authors. All Rights Reserved.\\n//\\n// Licensed under the Apache License, Version 2.0 (the \\\"License\\\");\\n// you may not use this file except in compliance with the License.\\n// You may obtain a copy of the License at\\n//\\n//      http://www.apache.org/licenses/LICENSE-2.0\\n//\\n// Unless required by applicable law or agreed to in writing, software\\n// distributed under the License is distributed on an \\\"AS-IS\\\" BASIS,\\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\n// See the License for the specific language governing permissions and\\n// limitations under the License.\\n\\n/**\\n * @fileoverview Anchored viewport positioning class.\\n *\\n * @author eae@google.com (Emil A Eklund)\\n */\\n\\ngoog.provide('goog.positioning.AnchoredViewportPosition');\\n\\ngoog.require('goog.positioning');\\ngoog.require('goog.positioning.AnchoredPosition');\\ngoog.require('goog.positioning.Overflow');\\ngoog.require('goog.positioning.OverflowStatus');\\n\\n\\n\\n/**\\n * Encapsulates a popup position where the popup is anchored at a corner of\\n * an element. The corners are swapped if dictated by the viewport. For instance\\n * if a popup is anchored with its top left corner to the bottom left corner of\\n * the anchor the popup is either displayed below the anchor (as specified) or\\n * above it if there's not enough room to display it below.\\n *\\n * When using this positioning object it's recommended that the movable element\\n * be absolutely positioned.\\n *\\n * @param {Element} anchorElement Element the movable element should be\\n *     anchored against.\\n * @param {goog.positioning.Corner} corner Corner of anchored element the\\n *     movable element should be positioned at.\\n * @param {boolean=} opt_adjust Whether the positioning should be adjusted until\\n *     the element fits inside the viewport even if that means that the anchored\\n *     corners are ignored.\\n * @param {goog.math.Box=} opt_overflowConstraint Box object describing the\\n *     dimensions in which the movable element could be shown.\\n * @constructor\\n * @extends {goog.positioning.AnchoredPosition}\\n */\\ngoog.positioning.AnchoredViewportPosition = function(\\n    anchorElement, corner, opt_adjust, opt_overflowConstraint) {\\n  goog.positioning.AnchoredPosition.call(this, anchorElement, corner);\\n\\n  /**\\n   * The last resort algorithm to use if the algorithm can't fit inside\\n   * the viewport.\\n   *\\n   * IGNORE = do nothing, just display at the preferred position.\\n   *\\n   * ADJUST_X | ADJUST_Y = Adjust until the element fits, even if that means\\n   * that the anchored corners are ignored.\\n   *\\n   * @type {number}\\n   * @private\\n   */\\n  this.lastResortOverflow_ = opt_adjust ? (goog.positioning.Overflow.ADJUST_X |\\n                                           goog.positioning.Overflow.ADJUST_Y) :\\n                                          goog.positioning.Overflow.IGNORE;\\n\\n  /**\\n   * The dimensions in which the movable element could be shown.\\n   * @type {goog.math.Box|undefined}\\n   * @private\\n   */\\n  this.overflowConstraint_ = opt_overflowConstraint || undefined;\\n};\\ngoog.inherits(\\n    goog.positioning.AnchoredViewportPosition,\\n    goog.positioning.AnchoredPosition);\\n\\n\\n/**\\n * @return {goog.math.Box|undefined} The box object describing the\\n *     dimensions in which the movable element will be shown.\\n */\\ngoog.positioning.AnchoredViewportPosition.prototype.getOverflowConstraint =\\n    function() {\\n  return this.overflowConstraint_;\\n};\\n\\n\\n/**\\n * @param {goog.math.Box|undefined} overflowConstraint Box object describing the\\n *     dimensions in which the movable element could be shown.\\n */\\ngoog.positioning.AnchoredViewportPosition.prototype.setOverflowConstraint =\\n    function(overflowConstraint) {\\n  this.overflowConstraint_ = overflowConstraint;\\n};\\n\\n\\n/**\\n * @return {number} A bitmask for the \\\"last resort\\\" overflow.\\n */\\ngoog.positioning.AnchoredViewportPosition.prototype.getLastResortOverflow =\\n    function() {\\n  return this.lastResortOverflow_;\\n};\\n\\n\\n/**\\n * @param {number} lastResortOverflow A bitmask for the \\\"last resort\\\" overflow,\\n *     if we fail to fit the element on-screen.\\n */\\ngoog.positioning.AnchoredViewportPosition.prototype.setLastResortOverflow =\\n    function(lastResortOverflow) {\\n  this.lastResortOverflow_ = lastResortOverflow;\\n};\\n\\n\\n/**\\n * Repositions the movable element.\\n *\\n * @param {Element} movableElement Element to position.\\n * @param {goog.positioning.Corner} movableCorner Corner of the movable element\\n *     that should be positioned adjacent to the anchored element.\\n * @param {goog.math.Box=} opt_margin A margin specified in pixels.\\n * @param {goog.math.Size=} opt_preferredSize The preferred size of the\\n *     movableElement.\\n * @override\\n */\\ngoog.positioning.AnchoredViewportPosition.prototype.reposition = function(\\n    movableElement, movableCorner, opt_margin, opt_preferredSize) {\\n  var status = goog.positioning.positionAtAnchor(\\n      this.element, this.corner, movableElement, movableCorner, null,\\n      opt_margin,\\n      goog.positioning.Overflow.FAIL_X | goog.positioning.Overflow.FAIL_Y,\\n      opt_preferredSize, this.overflowConstraint_);\\n\\n  // If the desired position is outside the viewport try mirroring the corners\\n  // horizontally or vertically.\\n  if (status & goog.positioning.OverflowStatus.FAILED) {\\n    var cornerFallback = this.adjustCorner(status, this.corner);\\n    var movableCornerFallback = this.adjustCorner(status, movableCorner);\\n\\n    status = goog.positioning.positionAtAnchor(\\n        this.element, cornerFallback, movableElement, movableCornerFallback,\\n        null, opt_margin,\\n        goog.positioning.Overflow.FAIL_X | goog.positioning.Overflow.FAIL_Y,\\n        opt_preferredSize, this.overflowConstraint_);\\n\\n    if (status & goog.positioning.OverflowStatus.FAILED) {\\n      // If that also fails, pick the best corner from the two tries,\\n      // and adjust the position until it fits.\\n      cornerFallback = this.adjustCorner(status, cornerFallback);\\n      movableCornerFallback = this.adjustCorner(status, movableCornerFallback);\\n\\n      goog.positioning.positionAtAnchor(\\n          this.element, cornerFallback, movableElement, movableCornerFallback,\\n          null, opt_margin, this.getLastResortOverflow(), opt_preferredSize,\\n          this.overflowConstraint_);\\n    }\\n  }\\n};\\n\\n\\n/**\\n * Adjusts the corner if X or Y positioning failed.\\n * @param {number} status The status of the last positionAtAnchor call.\\n * @param {goog.positioning.Corner} corner The corner to adjust.\\n * @return {goog.positioning.Corner} The adjusted corner.\\n * @protected\\n */\\ngoog.positioning.AnchoredViewportPosition.prototype.adjustCorner = function(\\n    status, corner) {\\n  if (status & goog.positioning.OverflowStatus.FAILED_HORIZONTAL) {\\n    corner = goog.positioning.flipCornerHorizontal(corner);\\n  }\\n\\n  if (status & goog.positioning.OverflowStatus.FAILED_VERTICAL) {\\n    corner = goog.positioning.flipCornerVertical(corner);\\n  }\\n\\n  return corner;\\n};\\n\"],\n\"names\":[\"goog\",\"provide\",\"require\",\"positioning\",\"AnchoredViewportPosition\",\"goog.positioning.AnchoredViewportPosition\",\"anchorElement\",\"corner\",\"opt_adjust\",\"opt_overflowConstraint\",\"AnchoredPosition\",\"call\",\"lastResortOverflow_\",\"Overflow\",\"ADJUST_X\",\"ADJUST_Y\",\"IGNORE\",\"overflowConstraint_\",\"undefined\",\"inherits\",\"prototype\",\"getOverflowConstraint\",\"goog.positioning.AnchoredViewportPosition.prototype.getOverflowConstraint\",\"setOverflowConstraint\",\"goog.positioning.AnchoredViewportPosition.prototype.setOverflowConstraint\",\"overflowConstraint\",\"getLastResortOverflow\",\"goog.positioning.AnchoredViewportPosition.prototype.getLastResortOverflow\",\"setLastResortOverflow\",\"goog.positioning.AnchoredViewportPosition.prototype.setLastResortOverflow\",\"lastResortOverflow\",\"reposition\",\"goog.positioning.AnchoredViewportPosition.prototype.reposition\",\"movableElement\",\"movableCorner\",\"opt_margin\",\"opt_preferredSize\",\"status\",\"positionAtAnchor\",\"element\",\"FAIL_X\",\"FAIL_Y\",\"OverflowStatus\",\"FAILED\",\"cornerFallback\",\"adjustCorner\",\"movableCornerFallback\",\"goog.positioning.AnchoredViewportPosition.prototype.adjustCorner\",\"FAILED_HORIZONTAL\",\"flipCornerHorizontal\",\"FAILED_VERTICAL\",\"flipCornerVertical\"]\n}\n"]