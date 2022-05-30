# Description

Make a python package that exports a single class called "Variable".  
You should be able to instantiate objects of this class either by a name (string) or a scalar (int/float).  
Symbolic variables should support setting a property "value" to a scalar (int/float).  
Scalar variables set this attribute in the constructor.  
Variables and scalars can be combined into objects of type "Expression" using basic binary arithmetic operations: +, - and *.

type(variable('a') + variable(3)) == Expression

An expression should have an attribute "tree" with the binary expression tree as well as a way to dump out the tree to the console (simple ascii representation that shows the different branches and levels).  
There should also be a method or property to compile the tree into assembly instructions and dump these out.  
Assume that PUSH, ADD, SUB and MULT instructions are available and support both integers and floating point numbers.  
Write a function that implements a stack machine to evaluate the assembly instructions (like expression.tree.assembly.eval()).

Don't write a lexical parser. Python should decide itself on the order of execution of the various algebraic expressions.

# Installation

Run my_test.py to see how the solution works.

# Comments to development

It was a fun task with recursion, although I haven't done the ASCII printing of the tree - this part by itself would take more than a day to write and test.
