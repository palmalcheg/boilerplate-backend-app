# boilerplate-backend-app

build an run
```
mvn clean install exec:java
```

Api available :
* `/github` - all public repositories
* `/github/users/<username>/repos` - User repositories
* `/github/users/<page start>/<on page>` - All Users with paging (0/30 by default) 