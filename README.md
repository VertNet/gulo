# What is Gulo?

![](http://3.bp.blogspot.com/-s1vAPdg_zZM/TZ3bnzUZgVI/AAAAAAAACKo/Mk-Tu-Nil74/s1600/animalangry.jpg)

Gulo is the genus for wolverine, the biggest land-dwelling species of weasel on the planet. It is a stocky and muscular carnivore, resembling a small bear. The wolverine has a reputation for endurance, ferocity, and strength out of proportion to its size, with the capacity to battle with competitors many times its size.

Gulo is also a VertNet project designed for harvesting Darwin Core Archives, shredding them into small pieces, and loading them into [CartoDB](http://cartodb.com). It's written in the [Clojure](http://clojure.org) programming language and rides on [Cascading](http://www.cascading.org) and [Cascalog](https://github.com/nathanmarz/cascalog) for processing "Big Data" on top of [Hadoop](http://hadoop.apache.org) using [MapReduce](http://research.google.com/archive/mapreduce.html).

# Developing
## AWS credentials

Running Gulo queries with Elastic MapReduce requires adding the following to the file `credentials.json` in the project root:

```json
{
   "access-id": "your_aws_access_id",
   "private-key":"your_aws_private_key",
   "key-pair-file":"~/.ssh/vertnet.pem",
   "key-pair":"vertnet"
}
```

This needs to be stored in `resources/s3.json`:

```json
{
    "access-key": "your_aws_access_id",
    "secret-key": "your_aws_private_key"
}
```

## CartoDB OAuth credentials

Gulo depends on an authenticated connection to CartoDB. This requires adding the following file in `resources/creds.json`:

```json
{
  "key": "your_cartodb_oauth_key",
  "secret": "your_cartodb_oauth_secret",
  "user": "your_cartodb_username",
  "password": "your_cartodb_password"
}
```

## Dependencies

For adding BOM bytes to UTF-8 files, so that CartoDB can detect the encoding, we use the `uconv` program which can be installed on Ubuntu like this:

```bash
$ sudo apt-get install apt-file
$ sudo apt-file update
$ apt-file search bin/uconv
$ sudo apt-get install libicu-dev
```
