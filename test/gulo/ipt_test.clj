(ns gulo.ipt-test
  "Unit test the gulo.ipt namespace."
  (:use gulo.ipt
        [cartodb.core :as cartodb]
        [midje sweet]))

(def sample-resource
  {:creator "The Creator<creator@vertnet.org>",
   :author "author@vernet.org",
   :emlUrl "http://ipt.vertnet.org:8080/ipt/eml.do?r=tester",
   :guid "b6015b60-6f96-43a9-88e5-2f41854e8f07",
   :title "Test Title",
   :url "http://ipt.vertnet.org:8080/ipt/resource.do?r=tester",
   :pubDate "Thu, 02 Aug 2012 12:28:24 -0500",
   :publisher "Publisher<publisher@vernet.org>",
   :description "Description goes here <a href=\"http://ipt.vertnet.org:8080/ipt/logo.do?r=tester\">Resource Logo</a> <a href=\"http://ipt.vertnet.org:8080/ipt/eml.do?r=tester\">EML</a>",
   :dwcaUrl "http://ipt.vertnet.org:8080/ipt/archive.do?r=tester"})

(defn bootstrap-test-tables
  "Add resources to test tables that will get caught by the new, delete
   and update queries."
  [api-key]
  (let [table "test_ipt_resources"
        tmp "test_ipt_resources_tmp"]

    ;; inserting sample-resource into table
    (cartodb/query (insert-ipt-resources table [sample-resource]) "vertnet" :api-key api-key)

    ;; inserting sample-resource into tmp - never updated, deleted or new
    (cartodb/query (insert-ipt-resources tmp [sample-resource]) "vertnet" :api-key api-key)
    
    (let [older-sample-resource (assoc sample-resource
                                  :creator "Older creator<creator@vertnet.org>",
                                  :author "older_author@vernet.org",
                                  :guid "f2918c90-6f96-43a9-88e5-129387847382"
                                  :pubDate "Thu, 01 Dec 2000 12:28:24 -0500",)]

      ;; inserting older-sample-resource into table - will be updated
      (cartodb/query (insert-ipt-resources table [older-sample-resource]) "vertnet" :api-key api-key)
      
      ;; inserting update-tester into tmp - is in table with older pubdate
      (let [update-tester (assoc older-sample-resource :pubDate "Thu, 01 Jan 2013 12:28:24 -0500")]
        (cartodb/query (insert-ipt-resources tmp [update-tester]) "vertnet" :api-key api-key)))

    ;; inserting new-tester into tmp - isn't in table
    (let [new-tester (assoc sample-resource :guid "BRAND NEW GUID" :author "Such a new author!")]
      (cartodb/query (insert-ipt-resources tmp [new-tester]) "vertnet" :api-key api-key))

    ;; inserting delete-tester into table - never appears in tmp
    (let [delete-tester (assoc sample-resource :creator "TO DELETE<TO_DELETE@vertnet.org" :guid "TO DELETE GUID")]
      (cartodb/query (insert-ipt-resources table [delete-tester]) "vertnet" :api-key api-key))))

(fact "Test insert-ipt-resources function."
  (let [table "ipt_resources_tmp"]
    (insert-ipt-resources table [sample-resource])) =>
    "INSERT INTO ipt_resources_tmp (creator, author, emlUrl, guid, title, url, pubDate, publisher, description, dwcaUrl) VALUES ('The Creator<creator@vertnet.org>', 'author@vernet.org', 'http://ipt.vertnet.org:8080/ipt/eml.do?r=tester', 'b6015b60-6f96-43a9-88e5-2f41854e8f07', 'Test Title', 'http://ipt.vertnet.org:8080/ipt/resource.do?r=tester', 'Thu, 02 Aug 2012 12:28:24 -0500', 'Publisher<publisher@vernet.org>', 'Description goes here <a href=\"http://ipt.vertnet.org:8080/ipt/logo.do?r=tester\">Resource Logo</a> <a href=\"http://ipt.vertnet.org:8080/ipt/eml.do?r=tester\">EML</a>', 'http://ipt.vertnet.org:8080/ipt/archive.do?r=tester')")
