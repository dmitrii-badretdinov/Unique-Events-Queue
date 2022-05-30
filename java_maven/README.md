# Task description

Make a CI/CD-compatible Java program that calculates the amount of CO2 emitted.

The data is given as a small table.

Parameters:
* distance - distance traveled.
* unit-of-distance - m or km.
* transportation-method - entry from the given data table.
* output - g or kg.

The solution should be unit-tested.

Time limit for the task: 8 hours.

Usage examples:
```
$ ./travel-emission-calculator --transportation-method diesel-car-medium --
distance 15 --unit-of-distance km
Your trip caused 2.7kg of CO2-equivalent.
```

```
$ ./travel-emission-calculator --distance 1800.5 --transportation-method petrol-
car-large
Your trip caused 507.8kg of CO2-equivalent.
```

```
$ ./travel-emission-calculator --unit-of-distance=km --distance 15 --
transportation-method=diesel-car-medium
Your trip caused 2.7kg of CO2-equivalent.
```

# Installation

Use IntelliJ to open the project.

I wish to say that in CI/CD environment, you need to just `mvn install`, but I only had time to `install` it in IntelliJ, and there will definitely be bugs to fix before it launches in real CI/CD. 

In order to install it in IntelliJ, import the project as Java+Maven, then go through the whole Maven lifecycle starting from `clean` up to `install`.  
Now you can select the created JAR file in the Project tree and run it. Well, you could have, if it had worked, which it doesn't because there was not enough time.

# Comments to development

Maven is a menace to those who only start to work with it. Each error sends you on a world tour of trying to find how to fix it.  
I also might have spent an embarrassing amount of time trying to make an executable jar.

Overall, I finished the coding part of the actual solution but didn't have time to fight with Maven again for java to see my other classes aside from the main one.  
I also know how to write unit tests and made the class structure that would be comfortable to tests, but I could not test things before I fixed Maven.  
Over the whole span of 8 hours, I was able to make an executable Hello World and then managed to link the needed libs, but I ran out of time on the error of java not seeing my ArgumentParser class.