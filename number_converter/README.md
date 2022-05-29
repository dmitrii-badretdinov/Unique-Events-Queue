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