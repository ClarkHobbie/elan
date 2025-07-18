# Introduction

elan is about trust.  The implicit question it asks is: should I trust this person?

# Details
## Commands
In elan, a command takes the form of

``
elan <trustore> <command> <arguments>
``

Where `truststore` is a file name of a trust store.

For example:

``
elan watever remove principal three
``

Uses the file named "whatever" as the trust store

### Report
``
elan report <principal name> 
``
## Principals
In elan discussions, a Principal is a person to be trusted or not.  This depends on whether you have direct experiance with
the person, or whether someone that you trust, trusts them.
