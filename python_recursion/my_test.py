from assignment import Variable


a = Variable('a')
b = Variable('b')
c = Variable('c')
three = Variable(3.33333)

z = (a + b) * c + 7

a.value = 1
b.value = 2
c.value = 3

assert z.tree_assembly_eval() == 16

z = (a * b + (a + b + a)) * (a - 3)

a.value = 0.5
b.value = 1.5

assert z.tree_assembly_eval() == -8.125

z = Variable(3) + a
print(z.tree_assembly)

assert z.tree_assembly_eval() == 3.5

a.value = 4

assert z.tree_assembly_eval() == 7