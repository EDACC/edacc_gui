/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.filter;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 *
 * @author simon
 */
public class Parser {

    public boolean eval(String expression, HashMap<Integer, Boolean> arguments) throws Exception {
        return eval(expression, arguments, false);
    }

    private boolean eval(int op, boolean operand1, boolean operand2) {
        if (op == 0) {
            return operand1 && operand2;
        } else {
            return operand1 || operand2;
        }
    }

    public boolean eval(String expression, HashMap<Integer, Boolean> arguments, boolean test) throws Exception {
        //System.out.println("EXPRESSION = " + expression);
        expression = expression.replaceAll(" ", "");
        boolean res = true;
        boolean not = false;
        int op = 0;
        int i = 0;
        while (i < expression.length()) {
            //System.out.println("Trying to parse " + expression.substring(i, expression.length()));
            if (expression.charAt(i) == '(') {
                if (op == -1) {
                    throw new Exception("DBG( Syntax error at Position " + i);
                }
                int num = 1;
                int cur = i;
                i++;
                while (num > 0 && i < expression.length()) {
                    if (expression.charAt(i) == '(') {
                        num++;
                    } else if (expression.charAt(i) == ')') {
                        num--;
                    }
                    i++;
                }
                if (num != 0) {
                    throw new Exception("DBG (2 Syntax error at Position " + i);
                }
                boolean tmp = eval(expression.substring(cur + 1, i - 1), arguments, test);
                if (not) {
                    tmp = !tmp;
                }
                res = eval(op, res, tmp);
                not = false;
                op = -1;
            } else if (expression.charAt(i) == '&') {
                if (i + 1 >= expression.length() || expression.charAt(i + 1) != '&') {
                    throw new Exception("DBG& Syntax error at Position " + i);
                }
                i += 2;
                op = 0;
            } else if (expression.charAt(i) == '|') {
                if (i + 1 >= expression.length() || expression.charAt(i) != '|') {
                    throw new Exception("DBG| Syntax error at Position " + i);
                }
                i += 2;
                op = 1;
            } else if (expression.charAt(i) == '$') {
                if (op == -1) {
                    throw new Exception("DBGa Syntax error at Position " + i);
                }
                String numText = "";
                i++;
                while (i < expression.length() && expression.charAt(i) >= '0' && expression.charAt(i) <= '9') {
                    numText += expression.charAt(i);
                    i++;
                }
                if ("".equals(numText)) {
                    throw new Exception("DBGa2 Syntax error at Position " + i);
                }
                int argNum = Integer.parseInt(numText);
                Boolean tmp = arguments.get(argNum);
                if (test) {
                    tmp = true;
                }
                if (tmp == null) {
                    throw new Exception("Invalid argument " + argNum);
                }
                if (not) {
                    tmp = !tmp;
                }
                res = eval(op, res, tmp);
                not = false;
                op = -1;
            } else if (expression.charAt(i) == 't') {
                if (op == -1 || i + 3 >= expression.length() || !"true".equals(expression.substring(i, i + 4))) {
                    throw new Exception("Syntax error");
                }
                boolean tmp = true;
                if (not) {
                    tmp = !tmp;
                }
                res = eval(op, res, tmp);
                op = -1;
                not = false;
                i += 4;
            } else if (expression.charAt(i) == 'f') {
                if (op == -1 || i + 4 >= expression.length() || !"false".equals(expression.substring(i, i + 5))) {
                    throw new Exception("Syntax error");
                }
                boolean tmp = false;
                if (not) {
                    tmp = !tmp;
                }
                res = eval(op, res, tmp);
                op = -1;
                not = false;
                i += 5;
            } else if (expression.charAt(i) == '!') {
                not = true;
                i++;
            } else {
                throw new Exception("DBG!! Syntex error at Position " + i);
            }
        }
        return res;
    }
}
