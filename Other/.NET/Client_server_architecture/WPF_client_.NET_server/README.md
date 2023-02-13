# Number converter

A program which converts dollars from numbers into words.  
The maximum number is 999 999 999.  
The maximum number of cents is 99.  
The separator between dollars and cents is ‘,’ (comma).  

* Client-server architecture
* WPF client
* ASP.NET server
* Server-side converting

Examples:  
| Input | Expected output |
| ----- | --------------- |
| 0 | zero dollars |
| 1 | one dollar |
| 25,1 | twenty-five dollars and ten cents |
| 0,01 | zero dollars and one cent |
| 45 100 | forty-five thousand one hundred dollars |
| 999 999 999,99 | nine hundred ninety-nine million nine hundred ninety-nine thousand nine hundred ninety-nine dollars and ninety-nine cents |

# How to install
1. Clone a repository or download and extract a zip on your machine.
2. Locate the number_converter folder and launch both the client and server solutions.
3. Check that the server is doing fine with unit tests.
4. Launch both server and client project and test the functionality.
5. For the connection between the client and server to work, you will need to have localhost:5192 available.  
In other words, the port 5192 on your machine should be available to use.

I wrote some inline documentation for the server. It should explain why exactly a thing of interest was done in this or that way.

The project will be moved to .NET/Client_server_architecture/WPF_client_.NET_server after a while.
