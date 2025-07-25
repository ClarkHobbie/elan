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

Use the file named "whatever" as the trust store, and delete a principal named "three."

### Report
``
elan <truststore> report <principal name> 
``

A report is the heart of what elan does.  It prints out the level of trust for the principal and the path it takes back
to the root,

### Add Principal
``
elan <truststore> add principal <new pricipal name> <destination name> <trust> <direct or recommendation>
``

This command adds a principal to the database of principals.  Since the new principal must be related to an existing 
principal, the command also requires relation information.

### Add Relation
``
elan <truststore> add relation <source name> <destination name> <trust> <direct or recommendation>
``

This command adds a relation between two existing principals.  The two principals must already exist for the command to
work.  

### Remove Principal
``
elan <trustore> remove <principal name>
``

Removes the existing principal from the system.

### Remove Relation
``
elan <truststore> remove relation <principal nane> <destination name>
``

This command removes a relation from a principal in the system.

## Terminology
### Principal
In elan discussions, a Principal is a person who you want to know if you can trust, or a person who
has relationships with people who do have direct knowledge.

### Relation
In elan, relations tie two principals together.  This relation has a level of trust associated with it, from the source
principal's perspective, and whether the relation takes the form of direct experience or if it is, itself, a
recommendation.

### TrustStore
A truststore is where elan stores it's trust information.  It uses it in performing most operations.

A truststore takes the form of a JSON file that represents the list of all principals.  Please note that in a truststore
file, all principals (other than those in the list), are stored as names.  Thus a source for a principal is stored as
the name of the source, rather that the principal itself.
### Principal Names
The name of a principal must unique across all principals.  
