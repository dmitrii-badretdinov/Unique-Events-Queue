# Classes are in the same file because of the circular import issue otherwise.

from numbers import Number

class ExpressionAlgebra:
    def __add__(self, other):
        return Expression('+', self, other)

    def __sub__(self, other):
        return Expression('-', self, other)

    def __mul__(self, other):
        return Expression('*', self, other)

    def __radd__(self, other):
        return Expression('+', self, other)

    def __rsub__(self, other):
        return Expression('-', self, other)

    def __rmul__(self, other):
        return Expression('*', self, other)

class Expression(ExpressionAlgebra):
    def __init__(self, operation = None, left = None, right = None):
        self.operation = operation
        self.left = left
        self.right = right

        allowed_operations = "+-*"
        if operation not in allowed_operations or len(operation) > 1:
            raise Exception('Unrecognized operation {op}.'.format(op = operation))

        for object in (left, right):
            if not (isinstance(object, (Variable, Expression, Number))):
                raise Exception('Unrecognized object type when initializing an Expression. Got {var}.'.format(var = type(object)))

    # Used tree_assembly instead of tree.assembly because doing it with the dot notation is a pain.
    # I haven't found the examples where the dot notation was used like that. 
    # For a property like obj.X? Yes, but not something like obj.X.Y.Z.
    # It would make sense if tree and assembly were separate classes. 
    # Then tree would be an object in Expression and assembly was an object in Tree.
    # Then assembly would have an eval() method.
    # In the future, if these parts were to be expanded, then sure, let's put the tree and assembly 
    # logic into separate classes, but as it is, there is no need to create additional objects 
    # because we don't do anything else to the tree or to its assembly.

    @property
    def tree_assembly(self):
        result = ''
        for object in [self.left, self.right]:
            if isinstance(object, Expression):
                result += object.tree_assembly
            elif isinstance(object, Variable):
                if(object.name != None):
                    result += "PUSH {name}\n".format(name = object.name)
                else:
                    result += "PUSH {value}\n".format(value = object.value)
            elif isinstance(object, Number):
                result += "PUSH {value}\n".format(value = object)
            else:
                raise TypeError(
                    'Expression in node ({node_left}, {node_operation}, {node_right}) is of unrecognized type {node_type}'.format(
                    node_left = self.left,
                    node_operation = self.operation,
                    node_right = self.right,
                    node_type = type(object)))
        result += operation_dictionary[self.operation]+'\n'
        return result

    def tree_assembly_eval(self):
        left_result = self.eval_leaf(self.left)
        right_result = self.eval_leaf(self.right)
        
        
        match self.operation:
            case '+':
                return left_result + right_result
            case '-':
                return left_result - right_result
            case '*':
                return left_result * right_result
            case _:
                raise TypeError('Unrecognized operator {node_operation} in the Expression ({node_left}, {node_operation}, {node_right})'.format(
                    node_left = self.left,
                    node_operation = self.operation,
                    node_right = self.right))

    def eval_leaf(self, object):
        if isinstance(object, Expression):
            return object.tree_assembly_eval()
        elif isinstance(object, Variable):
            if object.value != None:
                return object.value
            else:
                raise ValueError('Variable {var_name} has no assigned value.'.format(var_name = object.name))
        elif isinstance(object, Number):
            return object
        else:
            raise TypeError('Cannot evaluate an object of type {obj_type}'.format(obj_type = type(object)))

    @property
    def tree(self, depth = 0):
        raise NotImplementedError()
    # Programming a vertical ASCII view of a binary tree is hard and very time-consuming.
    # You need additional information to be passed through the whole tree to calculate the indents and gaps. 
    # Printing it also requires non-obvious walks on the tree.
    # If you want for some nodes to have different decoration like you showed with the root node, 
    # it adds an additional layer of hell.
    # Let's take a look at your example
    #                   +
    #                   |\
    #                   * 7
    #                  /|
    #                 + c
    #                /|
    #               a b
    # What if instead of "c" there was an expression? 
    # In a simple version, the view would be corrupted because there are two variables at the same place.
    #                   +
    #                   |\
    #                   * 7
    #                  /|
    #                 + +
    #                /|/|
    #               a ? 1
    # Let's assume we use a fancy version where this issue is fixed.
    #                   +
    #                   |\
    #                   * 7
    #                  /|
    #                 + +
    #                /| |\
    #               a b c d
    # What would happen if there were expressions instead of abcd?
    # We would run out of space even if all numbers were 1-character-long.
    # Solving this issue requires to spread out the parent expressions, which can affect all 
    # other parts of the graph.
    # The feat of figuring out all the indents requires more than a full day to program and test.
    # Therefore, I haven't done this part of the task.

class Variable(ExpressionAlgebra):
    def __init__(self, item):
        self.name = None
        self.value = None
        if type(item) in (int, float):
            self.value = item
        else:
            self.name = item

operation_dictionary = {
    '+': 'ADD',
    '-': 'SUB',
    '*': 'MULT'
}