# ----------------------------------------
#    Assignment: A simple stack machine
# ----------------------------------------
#
# Implement a Python package that exports a single class called "Variable".
# You should be able to instantiate objects of this class either by a name (of type string)
# or a scalar (of type int/float).
# Symbolic Variables should support setting a property "value" to a scalar (of type int/float).
# Scalar Variables set this attribute in the constructor.
# Variables and scalars can be combined into objects of type "Expression" using
# basic binary arithmetic operations: +, - and *.
#
# type(Variable('a') + Variable(3)) == Expression
#
# An Expression should have an attribute "tree" with the binary expression tree as well as
# a way to dump out the tree to the console (simple ASCII representation that shows the different
# branches and levels is sufficient).
# There should also be a method (or property) to compile the tree into assembly instructions and
# dump these out.
# Assume that PUSH, ADD, SUB and MULT instructions are available and support both integers and
# floating point numbers.
# Write a function that implements a stack machine to evaluate the assembly instructions
# (e.g. by calling the method Expression.tree.assembly.eval()).
#
# Hint: You should not write a lexer/parser. Instead, the Python interpreter should be used for this
# purpose. This means that the order of execution of the various algebraic expressions is determined by
# Python.
#
# You can use the examples and assertions below to test your code.


from assignment import Variable

a = Variable('a')
b = Variable('b')
c = Variable('c')


z = (a + b) * c + 7

# The binary expression tree for this example would be (printed e.g. via print(z.tree)):
#                   +
#                   |\
#                   * 7
#                  /|
#                 + c
#                /|
#               a b
#
# The assembly would be (printed e.g. via print(z.tree.assembly)):
#   PUSH a
#   PUSH b
#   ADD
#   PUSH c
#   MULT
#   PUSH 7
#   ADD

a.value = 1
b.value = 2
c.value = 3


assert z.tree.assembly.eval() == 16

z = (a * b + (a + b + a)) * (a - 3)

# The assembly would be (printed e.g. via print(z.tree.assembly)):
#   PUSH a
#   PUSH b
#   MULT
#   PUSH a
#   PUSH b
#   ADD
#   PUSH a
#   ADD
#   ADD
#   PUSH a
#   PUSH 3
#   SUB
#   MULT

a.value = 0.5
b.value = 1.5

assert z.tree.assembly.eval() == -8.125

z = Variable(3) + a

# The assembly would be (printed e.g. via print(z.tree.assembly)):
#   PUSH 3
#   PUSH a
#   ADD

assert z.tree.assembly.eval() == 3.5

a.value = 4

assert z.tree.assembly.eval() == 7
