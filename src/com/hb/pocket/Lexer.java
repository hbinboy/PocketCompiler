package com.hb.pocket;

import com.hb.utils.log.MyLog;
import java.util.Scanner;

/**
 * Created by hb on 12/08/2018.
 */
public class Lexer {

    private static String TAG = Lexer.class.getSimpleName();

    /**
     * Statement end.
     */
    public static final int EOI = 0;
    /**
     * The ';' symbol.
     */
    public static final int SEMI = 1;
    /**
     * The '+' symbol.
     */
    public static final int PLUS = 2;
    /**
     * The '-' symbol.
     */
    public static final int SUB = 3;
    /**
     * The '*' symbol.
     */
    public static final int TIMES = 4;
    /**
     * The '/' symbol.
     */
    public static final int DIV = 5;
    /**
     * The left bracket '(' symbol.
     */
    public static final int LP = 6;
    /**
     * The right bracket ')' symbol.
     */
    public static final int RP = 7;
    /**
     * Numberic symbol.
     */
    public static final int NUM_OR_ID = 8;
    /**
     * Unknown symbol.
     */
    public static final int UNKNOWN_SYMBOL = 9;

    private int lookAhead = -1;

    public String yytext = "";

    public int yyleng = 0;

    /**
     * The line number.
     */
    public int yylineno = 0;

    private String inputBuffer = "";
    private String current = "";

    /**
     * Check the char is alpha or digit.
     * @param c
     * @return
     */
    private boolean isAlnum(char c) {
        if (Character.isAlphabetic(c) || Character.isDigit(c)) {
            return true;
        }
        return false;
    }

    private int lex() {
        while (true) {
            while (current == "") {
                Scanner s = new Scanner(System.in);
                while (true) {
                    String line = s.nextLine();
                    if (line.toLowerCase().equals("end".toLowerCase())) {
                        break;
                    }
                    inputBuffer += line + '\n';
                }
                s.close();
                if (inputBuffer.length() == 0) {
                    current = "";
                    return EOI;
                }
                current = inputBuffer;
                ++yylineno;
                current.trim();
            }
            if (current.isEmpty()) {
                return EOI;
            }
            for (int i = 0; i < current.length(); i++) {
                yyleng = 0;
                yytext = current.substring(0, 1);
                switch (current.charAt(i)) {
                    case ';' :
                    {
                        current = current.substring(1);
                        return SEMI;
                    }
                    case '+' :
                    {
                        current = current.substring(1);
                        return PLUS;
                    }
                    case '-' :
                    {
                        current = current.substring(1);
                        return SUB;
                    }
                    case '*' :
                    {
                        current = current.substring(1);
                        return TIMES;
                    }
                    case '/' :
                    {
                        current = current.substring(1);
                        return DIV;
                    }
                    case '(' :
                    {
                        current = current.substring(1);
                        return LP;
                    }
                    case ')' :
                    {
                        current = current.substring(1);
                        return RP;
                    }
                    case '\n' :
                    case '\t' :
                    case ' ' :
                    {
                        current = current.substring(1);
                        yylineno++;
                        break;
                    }
                    default :
                    {
                        if (isAlnum(current.charAt(i)) == false) {
                            return UNKNOWN_SYMBOL;
                        } else {
                            while (i < current.length() && isAlnum(current.charAt(i))) {
                                i++;
                                yyleng++;
                            }
                            yytext = current.substring(0, yyleng);
                            current = current.substring(yyleng);
                            return NUM_OR_ID;
                        }
                    }
                }
            }
        }
    }

    public boolean match(int token) {
        if (lookAhead == -1) {
            lookAhead = lex();
        }
        return token == lookAhead;
    }

    public void advance() {
        lookAhead = lex();
    }

    public void runLexer() {
        while (!match(EOI)) {
            MyLog.i(TAG, "Token: " + token() + "\t\t Symbol: " + yytext + "\t\t LineNo: " + yylineno);
            advance();
        }
    }

    /**
     * Get the token by lookAhead.
     * @return
     */
    private String token() {
        String token = "";
        switch (lookAhead) {
            case EOI :
            {
                token = "EOI";
                break;
            }
            case PLUS :
            {
                token = "PLUS";
                break;
            }
            case SUB :
            {
                token = "SUB";
                break;
            }
            case TIMES :
            {
                token = "TIMES";
                break;
            }
            case DIV :
            {
                token = "DIV";
                break;
            }
            case NUM_OR_ID :
            {
                token = "NUM_OR_ID";
                break;
            }
            case SEMI :
            {
                token = "SEMI";
                break;
            }
            case LP :
            {
                token = "LP";
                break;
            }
            case RP :
            {
                token = "RP";
                break;
            }
        }
        return token;
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        lexer.runLexer();
//        BasicParser basic_parser = new BasicParser(lexer);
//        basic_parser.statements();
    }

}
