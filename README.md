# What is Gulo?

Gulo is a project designed for harvesting Darwin Core Archives and loading them into [CartoDB](http://cartodb.com). It's written in the [Clojure](http://clojure.org) programming language and rides on [Cascading](http://www.cascading.org) and [Cascalog](https://github.com/nathanmarz/cascalog) for processing "Big Data" on top of [Hadoop](http://hadoop.apache.org) using [MapReduce](http://research.google.com/archive/mapreduce.html).

# Developing locally

Gulo depends on an authenticated connection to CartoDB. This requires adding the following file in `resources/creds.json`:

```json
{
  "key": "your_cartodb_oauth_key",
  "secret": "your_cartodb_oauth_secret",
  "user": "your_cartodb_username",
  "password": "your_cartodb_password"
}
```

