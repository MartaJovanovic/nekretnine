(ns nekretnine.core)
(-> (.getElementById js/document "content")
    (.-innerHTML)
    (set! "Hello, World!"))