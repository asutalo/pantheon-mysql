# Pantheon MySQL

Started off as a utility library for building and executing queries. Over time it got expanded to integrate
with [Pantheon](https://github.com/asutalo/pantheon).

# Restrictions

Transactions are not exposed yet to allow you to execute multiple procedures. The generic DataAccessService
implementation does not support nesting yet. Therefore, only standard Java/SQL types are supported.

# Coming soon...

* Transaction control
* Generic service support for nesting

[![](https://jitpack.io/v/asutalo/pantheon-mysql.svg)](https://jitpack.io/#asutalo/pantheon-mysql)
