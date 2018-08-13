package com.hb.pocket;

import com.hb.utils.log.MyLog;

/**
 * Created by hb on 12/08/2018.
 */
public class Parser {

    private static String TAG = Lexer.class.getSimpleName();

    private Lexer lexer;

    private String[] names = {"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"};

    private int nameP = -1;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Parser the statements.
     */
    public void statements() {
        String tempvar = newRegisterName();
        expression(tempvar);

        while (lexer.match(Lexer.EOI) != false) {
            expression (tempvar);
            freeNames(tempvar);

            if (lexer.match(Lexer.SEMI)) {
                lexer.advance();
            }
            else {
                MyLog.i(TAG,"Inserting missing semicolon: " + lexer.yylineno);
            }
        }
    }

    /**
     * Create new register name.
     * @return
     */
    private String newRegisterName() {
        if (nameP > names.length) {
            MyLog.i(TAG, "Expression too complex:" + lexer.yylineno);
            System.exit(1);
        }
        nameP++;
        String reg = names[nameP];
        return reg;
    }

    /**
     * Free the register name.
     * @param s
     */
    private void freeNames(String s) {
        if (nameP > 0) {
            names[nameP] = s;
            nameP--;
        } else {
            MyLog.i(TAG, "(Internal error) Name stack underflow: " + lexer.yylineno);
        }
    }

    /**
     * Parse the expression.
     * @param tempVar
     */
    private void expression(String tempVar) {
        String tempVar2;
        term(tempVar);
        while (lexer.match(Lexer.PLUS)) {
            lexer.advance();
            tempVar2 = newRegisterName();
            term(tempVar2);
            MyLog.i(TAG,tempVar + " += " + tempVar2);
            freeNames(tempVar2);
        }
        while (lexer.match(Lexer.SUB)) {
            lexer.advance();
            tempVar2 = newRegisterName();
            term(tempVar2);
            MyLog.i(TAG,tempVar + " -= " + tempVar2);
            freeNames(tempVar2);
        }
    }

    /**
     * Parse the term.
     * @param tempVar
     */
    private void term(String tempVar) {
        String tempVar2;

        factor (tempVar);
        while (lexer.match(Lexer.TIMES)) {
            lexer.advance();
            tempVar2 = newRegisterName();
            factor(tempVar2);
            MyLog.i(TAG, tempVar + " *= " + tempVar2);
            freeNames(tempVar2);
        }
        while (lexer.match(Lexer.DIV)) {
            lexer.advance();
            tempVar2 = newRegisterName();
            factor(tempVar2);
            MyLog.i(TAG, tempVar + " /= " + tempVar2);
            freeNames(tempVar2);
        }
    }

    /**
     * Parse the factor.
     * @param tempVar
     */
    private void factor(String tempVar) {
        if (lexer.match(Lexer.NUM_OR_ID)) {
            MyLog.i(TAG,tempVar + " = " + lexer.yytext);
            lexer.advance();
        } else if (lexer.match(Lexer.LP)) {
            lexer.advance();
            expression(tempVar);
            if (lexer.match(Lexer.RP)) {
                lexer.advance();
            } else {
                MyLog.i(TAG, "Missmatched parenthesis: " + lexer.yylineno);
            }
        } else {
            MyLog.i(TAG,"Number or identifier expected: " + lexer.yylineno);
        }
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser(lexer);
        parser.statements();
    }
}
