### Task description

Server parses a given json.  
User types in a search query. Client sends the query to the server.  
Server assembles a list of suggestions based on the weight of entries.  
JSON entries also have transitive weights. These transitive weights affect all children of the entry.  
Server sends the list of suggestions to the client.  
Client shows the list to the user.  


* Vue.js frontend,
* .NET backend

### Installation
#### Server

The server was developed in Visual Studio 2022.  

You need to install `Microsoft.EntityFrameworkCore.InMemory` from NuGet to build and launch the project.  
To do that, open `Tools` > `NuGet Package Manager` > `Manage NuGet Packages for Solution`.  
Select `Microsoft.EntityFrameworkCore.InMemory` to install.  


#### Client 

The client was developed in VS Code.

To build and launch the client, use the `npm run serve` command.


### Comments
There are many, many points where the program can be improved:
* Implement unit and integration tests.
* Make a proper frontend with a table.
* Make it that a user can actually specify the number of suggestions for the server to return. The backend is ready for that.
* Reduce time complexity of the algorithms, especially in the ResultsComposer class.

Overall, I had a lot of fun doing the Reflection part (Update[Non]TransitiveWeights). It was the first time when I used it that much.