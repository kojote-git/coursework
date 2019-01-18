## Book Library

#### What is this?

This is the key part of my coursework project. The topic of the coursework is a digital book library.

#### What it's used for?

Let's say, that it is a core module. This module contains all domain entities and it's direct responsibility is to 
provide means to do basic manipulations on these entities such as:
- query entities
- save them to database
- delete them from database
- update

So given that, it has classes for each domain entity located in `com.jkojote.library.domain.model` package and it also has
interfaces - different kind of repositories in `com.jkojote.library.domain.shared.domain` and, in the sampe package,
interfaces with classes that form the hierarchy of domain objects and entities. The implementations of these interfaces,
especially Repositories, can be found in `com.jkojote.library.persistence` package

#### What's the purpose ?

The main purpose of the module is to do mapping between domain objects and relational model having that no ORM has been used
in the project (I enjoy SQL).


#### Technologies
- Spring Framework (Core, JDBC)
- JUnit
- MySql database
