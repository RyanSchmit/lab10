package org.example;

/**
 * Tiny math expression evaluator.
 *
 * Supports:
 *   +  -  *  /
 *   Parentheses ( )
 *   Unary + and -
 *   Decimal numbers
 *
 * Grammar:
 *   expr   := term (('+'|'-') term)*
 *   term   := factor (('*'|'/') factor)*
 *   factor := ('+'|'-') factor | number | '(' expr ')'
 */
public final class ExpressionEvaluator {

    private ExpressionEvaluator() {
        // Prevent instantiation
    }

    public static double eval(String input) {
        Parser p = new Parser(input);
        double value = p.parseExpr();
        p.skipWs();
        if (!p.isEof()) {
            throw p.error("Unexpected character '" + p.peek() + "'");
        }
        return value;
    }

    private static final class Parser {
        private final String s;
        private int i;

        Parser(String s) {
            this.s = s;
            this.i = 0;
        }

        double parseExpr() {
            double v = parseTerm();
            while (true) {
                skipWs();
                if (match('+')) {
                    v += parseTerm();
                } else if (match('-')) {
                    v -= parseTerm();
                } else {
                    return v;
                }
            }
        }

        double parseTerm() {
            double v = parseFactor();
            while (true) {
                skipWs();
                if (match('*')) {
                    v *= parseFactor();
                } else if (match('/')) {
                    double denom = parseFactor();
                    if (denom == 0.0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    v /= denom;
                } else {
                    return v;
                }
            }
        }

        double parseFactor() {
            skipWs();

            if (match('+')) return parseFactor();
            if (match('-')) return -parseFactor();

            if (match('(')) {
                double v = parseExpr();
                skipWs();
                if (!match(')')) {
                    throw error("Missing closing ')'");
                }
                return v;
            }

            return parseNumber();
        }

        double parseNumber() {
            skipWs();
            int start = i;

            boolean sawDigit = false;

            while (!isEof() && Character.isDigit(peek())) {
                i++;
                sawDigit = true;
            }

            if (!isEof() && peek() == '.') {
                i++;
                while (!isEof() && Character.isDigit(peek())) {
                    i++;
                    sawDigit = true;
                }
            }

            if (!sawDigit) {
                if (isEof()) {
                    throw error("Unexpected end of input; expected a number");
                }
                throw error("Expected a number but found '" + peek() + "'");
            }

            String token = s.substring(start, i);

            try {
                return Double.parseDouble(token);
            } catch (NumberFormatException e) {
                throw error("Invalid number '" + token + "'");
            }
        }

        void skipWs() {
            while (!isEof() && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
        }

        boolean match(char c) {
            if (!isEof() && s.charAt(i) == c) {
                i++;
                return true;
            }
            return false;
        }

        char peek() {
            return s.charAt(i);
        }

        boolean isEof() {
            return i >= s.length();
        }

        IllegalArgumentException error(String msg) {
            return new IllegalArgumentException(msg + " at position " + i);
        }
    }
}