["^ ","~:resource-id",["~:shadow.build.classpath/resource","goog/ui/menurenderer.js"],"~:js","goog.provide(\"goog.ui.MenuRenderer\");\ngoog.forwardDeclare(\"goog.ui.Menu\");\ngoog.require(\"goog.a11y.aria\");\ngoog.require(\"goog.a11y.aria.Role\");\ngoog.require(\"goog.a11y.aria.State\");\ngoog.require(\"goog.asserts\");\ngoog.require(\"goog.dom\");\ngoog.require(\"goog.dom.TagName\");\ngoog.require(\"goog.ui.ContainerRenderer\");\ngoog.require(\"goog.ui.Separator\");\ngoog.ui.MenuRenderer = function(opt_ariaRole) {\n  goog.ui.ContainerRenderer.call(this, opt_ariaRole || goog.a11y.aria.Role.MENU);\n};\ngoog.inherits(goog.ui.MenuRenderer, goog.ui.ContainerRenderer);\ngoog.addSingletonGetter(goog.ui.MenuRenderer);\ngoog.ui.MenuRenderer.CSS_CLASS = goog.getCssName(\"goog-menu\");\ngoog.ui.MenuRenderer.prototype.canDecorate = function(element) {\n  return element.tagName == goog.dom.TagName.UL || goog.ui.MenuRenderer.superClass_.canDecorate.call(this, element);\n};\ngoog.ui.MenuRenderer.prototype.getDecoratorForChild = function(element) {\n  return element.tagName == goog.dom.TagName.HR ? new goog.ui.Separator : goog.ui.MenuRenderer.superClass_.getDecoratorForChild.call(this, element);\n};\ngoog.ui.MenuRenderer.prototype.containsElement = function(menu, element) {\n  return goog.dom.contains(menu.getElement(), element);\n};\ngoog.ui.MenuRenderer.prototype.getCssClass = function() {\n  return goog.ui.MenuRenderer.CSS_CLASS;\n};\ngoog.ui.MenuRenderer.prototype.initializeDom = function(container) {\n  goog.ui.MenuRenderer.superClass_.initializeDom.call(this, container);\n  var element = container.getElement();\n  goog.asserts.assert(element, \"The menu DOM element cannot be null.\");\n  goog.a11y.aria.setState(element, goog.a11y.aria.State.HASPOPUP, \"true\");\n};\n","~:source","// Copyright 2008 The Closure Library Authors. All Rights Reserved.\n//\n// Licensed under the Apache License, Version 2.0 (the \"License\");\n// you may not use this file except in compliance with the License.\n// You may obtain a copy of the License at\n//\n//      http://www.apache.org/licenses/LICENSE-2.0\n//\n// Unless required by applicable law or agreed to in writing, software\n// distributed under the License is distributed on an \"AS-IS\" BASIS,\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n// See the License for the specific language governing permissions and\n// limitations under the License.\n\n/**\n * @fileoverview Renderer for {@link goog.ui.Menu}s.\n *\n * @author robbyw@google.com (Robby Walker)\n */\n\ngoog.provide('goog.ui.MenuRenderer');\n\ngoog.forwardDeclare('goog.ui.Menu');\ngoog.require('goog.a11y.aria');\ngoog.require('goog.a11y.aria.Role');\ngoog.require('goog.a11y.aria.State');\ngoog.require('goog.asserts');\ngoog.require('goog.dom');\ngoog.require('goog.dom.TagName');\ngoog.require('goog.ui.ContainerRenderer');\ngoog.require('goog.ui.Separator');\n\n\n\n/**\n * Default renderer for {@link goog.ui.Menu}s, based on {@link\n * goog.ui.ContainerRenderer}.\n * @param {string=} opt_ariaRole Optional ARIA role used for the element.\n * @constructor\n * @extends {goog.ui.ContainerRenderer}\n */\ngoog.ui.MenuRenderer = function(opt_ariaRole) {\n  goog.ui.ContainerRenderer.call(\n      this, opt_ariaRole || goog.a11y.aria.Role.MENU);\n};\ngoog.inherits(goog.ui.MenuRenderer, goog.ui.ContainerRenderer);\ngoog.addSingletonGetter(goog.ui.MenuRenderer);\n\n\n/**\n * Default CSS class to be applied to the root element of toolbars rendered\n * by this renderer.\n * @type {string}\n */\ngoog.ui.MenuRenderer.CSS_CLASS = goog.getCssName('goog-menu');\n\n\n/**\n * Returns whether the element is a UL or acceptable to our superclass.\n * @param {Element} element Element to decorate.\n * @return {boolean} Whether the renderer can decorate the element.\n * @override\n */\ngoog.ui.MenuRenderer.prototype.canDecorate = function(element) {\n  return element.tagName == goog.dom.TagName.UL ||\n      goog.ui.MenuRenderer.superClass_.canDecorate.call(this, element);\n};\n\n\n/**\n * Inspects the element, and creates an instance of {@link goog.ui.Control} or\n * an appropriate subclass best suited to decorate it.  Overrides the superclass\n * implementation by recognizing HR elements as separators.\n * @param {Element} element Element to decorate.\n * @return {goog.ui.Control?} A new control suitable to decorate the element\n *     (null if none).\n * @override\n */\ngoog.ui.MenuRenderer.prototype.getDecoratorForChild = function(element) {\n  return element.tagName == goog.dom.TagName.HR ?\n      new goog.ui.Separator() :\n      goog.ui.MenuRenderer.superClass_.getDecoratorForChild.call(this, element);\n};\n\n\n/**\n * Returns whether the given element is contained in the menu's DOM.\n * @param {goog.ui.Menu} menu The menu to test.\n * @param {Element} element The element to test.\n * @return {boolean} Whether the given element is contained in the menu.\n */\ngoog.ui.MenuRenderer.prototype.containsElement = function(menu, element) {\n  return goog.dom.contains(menu.getElement(), element);\n};\n\n\n/**\n * Returns the CSS class to be applied to the root element of containers\n * rendered using this renderer.\n * @return {string} Renderer-specific CSS class.\n * @override\n */\ngoog.ui.MenuRenderer.prototype.getCssClass = function() {\n  return goog.ui.MenuRenderer.CSS_CLASS;\n};\n\n\n/** @override */\ngoog.ui.MenuRenderer.prototype.initializeDom = function(container) {\n  goog.ui.MenuRenderer.superClass_.initializeDom.call(this, container);\n\n  var element = container.getElement();\n  goog.asserts.assert(element, 'The menu DOM element cannot be null.');\n  goog.a11y.aria.setState(element, goog.a11y.aria.State.HASPOPUP, 'true');\n};\n","~:compiled-at",1662647710600,"~:source-map-json","{\n\"version\":3,\n\"file\":\"goog.ui.menurenderer.js\",\n\"lineCount\":35,\n\"mappings\":\"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\",\n\"sources\":[\"goog/ui/menurenderer.js\"],\n\"sourcesContent\":[\"// Copyright 2008 The Closure Library Authors. All Rights Reserved.\\n//\\n// Licensed under the Apache License, Version 2.0 (the \\\"License\\\");\\n// you may not use this file except in compliance with the License.\\n// You may obtain a copy of the License at\\n//\\n//      http://www.apache.org/licenses/LICENSE-2.0\\n//\\n// Unless required by applicable law or agreed to in writing, software\\n// distributed under the License is distributed on an \\\"AS-IS\\\" BASIS,\\n// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\n// See the License for the specific language governing permissions and\\n// limitations under the License.\\n\\n/**\\n * @fileoverview Renderer for {@link goog.ui.Menu}s.\\n *\\n * @author robbyw@google.com (Robby Walker)\\n */\\n\\ngoog.provide('goog.ui.MenuRenderer');\\n\\ngoog.forwardDeclare('goog.ui.Menu');\\ngoog.require('goog.a11y.aria');\\ngoog.require('goog.a11y.aria.Role');\\ngoog.require('goog.a11y.aria.State');\\ngoog.require('goog.asserts');\\ngoog.require('goog.dom');\\ngoog.require('goog.dom.TagName');\\ngoog.require('goog.ui.ContainerRenderer');\\ngoog.require('goog.ui.Separator');\\n\\n\\n\\n/**\\n * Default renderer for {@link goog.ui.Menu}s, based on {@link\\n * goog.ui.ContainerRenderer}.\\n * @param {string=} opt_ariaRole Optional ARIA role used for the element.\\n * @constructor\\n * @extends {goog.ui.ContainerRenderer}\\n */\\ngoog.ui.MenuRenderer = function(opt_ariaRole) {\\n  goog.ui.ContainerRenderer.call(\\n      this, opt_ariaRole || goog.a11y.aria.Role.MENU);\\n};\\ngoog.inherits(goog.ui.MenuRenderer, goog.ui.ContainerRenderer);\\ngoog.addSingletonGetter(goog.ui.MenuRenderer);\\n\\n\\n/**\\n * Default CSS class to be applied to the root element of toolbars rendered\\n * by this renderer.\\n * @type {string}\\n */\\ngoog.ui.MenuRenderer.CSS_CLASS = goog.getCssName('goog-menu');\\n\\n\\n/**\\n * Returns whether the element is a UL or acceptable to our superclass.\\n * @param {Element} element Element to decorate.\\n * @return {boolean} Whether the renderer can decorate the element.\\n * @override\\n */\\ngoog.ui.MenuRenderer.prototype.canDecorate = function(element) {\\n  return element.tagName == goog.dom.TagName.UL ||\\n      goog.ui.MenuRenderer.superClass_.canDecorate.call(this, element);\\n};\\n\\n\\n/**\\n * Inspects the element, and creates an instance of {@link goog.ui.Control} or\\n * an appropriate subclass best suited to decorate it.  Overrides the superclass\\n * implementation by recognizing HR elements as separators.\\n * @param {Element} element Element to decorate.\\n * @return {goog.ui.Control?} A new control suitable to decorate the element\\n *     (null if none).\\n * @override\\n */\\ngoog.ui.MenuRenderer.prototype.getDecoratorForChild = function(element) {\\n  return element.tagName == goog.dom.TagName.HR ?\\n      new goog.ui.Separator() :\\n      goog.ui.MenuRenderer.superClass_.getDecoratorForChild.call(this, element);\\n};\\n\\n\\n/**\\n * Returns whether the given element is contained in the menu's DOM.\\n * @param {goog.ui.Menu} menu The menu to test.\\n * @param {Element} element The element to test.\\n * @return {boolean} Whether the given element is contained in the menu.\\n */\\ngoog.ui.MenuRenderer.prototype.containsElement = function(menu, element) {\\n  return goog.dom.contains(menu.getElement(), element);\\n};\\n\\n\\n/**\\n * Returns the CSS class to be applied to the root element of containers\\n * rendered using this renderer.\\n * @return {string} Renderer-specific CSS class.\\n * @override\\n */\\ngoog.ui.MenuRenderer.prototype.getCssClass = function() {\\n  return goog.ui.MenuRenderer.CSS_CLASS;\\n};\\n\\n\\n/** @override */\\ngoog.ui.MenuRenderer.prototype.initializeDom = function(container) {\\n  goog.ui.MenuRenderer.superClass_.initializeDom.call(this, container);\\n\\n  var element = container.getElement();\\n  goog.asserts.assert(element, 'The menu DOM element cannot be null.');\\n  goog.a11y.aria.setState(element, goog.a11y.aria.State.HASPOPUP, 'true');\\n};\\n\"],\n\"names\":[\"goog\",\"provide\",\"forwardDeclare\",\"require\",\"ui\",\"MenuRenderer\",\"goog.ui.MenuRenderer\",\"opt_ariaRole\",\"ContainerRenderer\",\"call\",\"a11y\",\"aria\",\"Role\",\"MENU\",\"inherits\",\"addSingletonGetter\",\"CSS_CLASS\",\"getCssName\",\"prototype\",\"canDecorate\",\"goog.ui.MenuRenderer.prototype.canDecorate\",\"element\",\"tagName\",\"dom\",\"TagName\",\"UL\",\"superClass_\",\"getDecoratorForChild\",\"goog.ui.MenuRenderer.prototype.getDecoratorForChild\",\"HR\",\"Separator\",\"containsElement\",\"goog.ui.MenuRenderer.prototype.containsElement\",\"menu\",\"contains\",\"getElement\",\"getCssClass\",\"goog.ui.MenuRenderer.prototype.getCssClass\",\"initializeDom\",\"goog.ui.MenuRenderer.prototype.initializeDom\",\"container\",\"asserts\",\"assert\",\"setState\",\"State\",\"HASPOPUP\"]\n}\n"]