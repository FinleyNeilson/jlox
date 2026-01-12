package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final Token name;  // null for lambdas
    private final List<Token> params;
    private final List<Stmt> body;
    private final Environment closure;

    LoxFunction(Stmt.Function declaration, Environment closure) {
        this.name = declaration.name;
        this.params = declaration.params;
        this.body = declaration.body;
        this.closure = closure;
    }

    LoxFunction(Expr.Lambda declaration, Environment closure) {
        this.name = null;
        this.params = declaration.params;
        this.body = declaration.body;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // Create a new environment for each call and pass the global environment
        Environment environment = new Environment(closure);
        // For each parameter in the function add the argument to the identifier
        for (int i = 0; i < params.size(); i++) {
            environment.define(params.get(i).lexeme(),
                    arguments.get(i));
        }

        // Execute the block of code and use the Return exception to unwind back to here
        // if the interpreter hits a return
        try {
            interpreter.executeBlock(body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }

    @Override
    public int arity() {
        return params.size();
    }

    @Override
    public String toString() {
        if (name != null) {
            return "<fn " + name.lexeme() + ">";
        }
        return "<lambda>";
    }
}