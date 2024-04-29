
import java.io.*;

 /*
    I - 73  V - 86  X - 88
    0-9 - 48-57
    * - 42  + - 43  '-' - 45    / - 47
    ' ' - 32
    (Знак окончания строки) - 10
 */

public class Main {
    public static void main(String[] args) throws IOException
    {
        byte [] b_data = new byte[100];
        int i_FirstOperand, i_SecondOperand, i_CountArithmeticOperators = 0;
        String str_Result = "unknown";
        boolean isMathOperationExist = false, isDecimalExist = false, isRomeExist = false, isNotError = true;

        System.out.println("input:");
        // Читаем строку с консоли
        System.in.read(b_data);

        // Проверяем есть ли арифметический оператор в строке, если нет ошибка
        for (byte b: b_data) {
            if (isArithmeticOperator(b)) {
                isMathOperationExist = true;
                str_Result = "Изначально арифметическая операция присутствует.";
                break;
            }
            if (b == 10) break; // Если это ENTER значит окончание строки.
        }
        isNotError = isMathOperationExist;

        // Если арифметических операторов больше 1-го тоже ошибка
        for (byte b: b_data) {
            if (isArithmeticOperator(b)) i_CountArithmeticOperators++;
        }
        if (i_CountArithmeticOperators > 1) isNotError = false;

        // Если в строке присутствуют и десятичные цифры и латинские значит ошибка.
        for (byte b: b_data) {
            if (isNumberDecimal(b)) isDecimalExist = true;
            if (isNumberRome(b)) isRomeExist = true;
        }
        if (isDecimalExist && isRomeExist) isNotError = false;

        /* Теперь проверяем что в строке только десятичный цифра и какая-нибудь арифметическая операция
           или только римские цифры и тоже какая-нибудь арифметическая операция.
           Если нет - тогда ошибка. Там же могут быть другие буквы или символы.
         */
        boolean isOnlyDecimal = true, isOnlyRome = true;
        for (byte b: b_data) {
            if (b == 10) break;
            if (!isNumberDecimal(b)) {
                if (!isArithmeticOperator(b)) {
                    if (b != 32) {
                        isOnlyDecimal = false;
                        break;
                    }
                }
            }
        }
        for (byte b: b_data) {
            if (b == 10) break;
            if (!isNumberRome(b)){
                if (!isArithmeticOperator(b)) {
                    if (b != 32) {
                        isOnlyRome = false;
                        break;
                    }
                }
            }
        }
        //if (isOnlyDecimal) System.out.println("Все десятичные");
        //if (isOnlyRome) System.out.println("Все латинские");

        if (isNotError) isNotError = isOnlyDecimal | isOnlyRome;

        if (isNotError) {

            byte[] b_Data_NoSpace = new byte[100];
            // Работаем с десятичными
            StringBuilder str_Work = new StringBuilder();

            // Копируем в другой массив без пробелов (символа пробела)
            int i = 0;
            for (byte b: b_data) {
                if (b != 32) {
                    b_Data_NoSpace[i] = b;
                    i++;
                }
            }
            // Выводим полученный беспробельный массив
            for (byte b: b_Data_NoSpace){
                if (b == 10) break;
                // System.out.print((char) b);
            }
            //System.out.println();

            if (isOnlyDecimal){
                int i_Result = calculateDecimalExpression(b_Data_NoSpace, str_Work);
                if (str_Work.isEmpty()) {
                    str_Work.append(i_Result);
                }
            } // isOnlyDecimal
            if (isOnlyRome){
                String str_Result_Rome = new String();
                str_Result_Rome = calculateRomeExpression(b_Data_NoSpace);
                str_Work.append(str_Result_Rome);
            } // isOnlyRome


            str_Result = str_Work.toString();
        } // isNotError
        else GenerateException();   // str_Result =  "throws Exception";

        System.out.println("Output:");
        if (str_Result.contains("throws Exception")) GenerateException();
        else System.out.println(str_Result);
    }

    //................................ ФУНКЦИИ ...................................................................
    // Тестирует является ли переданное значение байта десятичной цифрой 0-9
    public static boolean isNumberDecimal(byte n) {
        return switch (n) {
            case 48, 49, 50, 51, 52, 53, 54, 55, 56, 57 -> true;
            default -> false;
        };
    }
    // Тестирует является ли переданное значение байта римской цифрой, а точнее латинской буквой: I, V, X
    public static boolean isNumberRome(byte n) {
        return switch (n) {
            case 73, 86, 88 -> true;
            default -> false;
        };
    }
    // Возвращает десятичную цифру соответствующую переданному значению кода unicode.
    public static int decimalFigure(byte n){
        int i_Return = switch (n) {
            case 48 -> 0;
            case 49 -> 1;
            case 50 -> 2;
            case 51 -> 3;
            case 52 -> 4;
            case 53 -> 5;
            case 54 -> 6;
            case 55 -> 7;
            case 56 -> 8;
            case 57 -> 9;
            default -> -1;
        };
        return i_Return;
    }
    // То же что и предыдущий, только для латинских
    public static char romeFigure(byte n) {
        char ch_Return = switch(n){
            case 73 -> 'I';
            case 86 -> 'V';
            case 88 -> 'X';
            default -> '-';
        };

        return ch_Return;
    }
    public static boolean isArithmeticOperator(byte n) {
        return switch (n){
            case 42, 43, 45, 47 -> true;
            default -> false;
        };
    }
    public static int calculateDecimalExpression(byte b_expr[], StringBuilder str_Result) {
        int dw_Return = 0;
        // Ищем оператор, вернее его позицию. До позиции i - это десятичное число - 1 операнд, После - 2 операнд.
        boolean bool_SecondOperand = false;
        String str_FirstOperand = new String();
        String str_SecondOperand = new String();
        String str_Operator = new String();
        for (byte b: b_expr) {
            if (b == 10) break;
            if (isArithmeticOperator(b)) {
                bool_SecondOperand = true;
                str_Operator += (char) b;
                continue;
            }
            if (!bool_SecondOperand) str_FirstOperand += (char) b;
            else str_SecondOperand += (char) b;
        }

        //System.out.println(str_FirstOperand);
        //System.out.println(str_SecondOperand);
        //System.out.println(str_Operator);

        int i_FirstOperand;
        if (!str_FirstOperand.isEmpty()) i_FirstOperand = Integer.parseInt(str_FirstOperand);
        else {
            str_Result.append("throws Exception");
            return 0;
        }
        int i_SecondOperand;
        if (!str_SecondOperand.isEmpty()) i_SecondOperand = Integer.parseInt(str_SecondOperand);
        else {
            str_Result.append("throws Exception");
            return 0;
        }

        if (i_SecondOperand < 1 || i_SecondOperand > 10 || i_FirstOperand < 1 || i_FirstOperand > 10) {
                str_Result.append("throws Exception");
                return 0;
        }

        switch (str_Operator) {
            case "+":
                dw_Return = i_FirstOperand + i_SecondOperand;
                break;
            case "-":
                dw_Return = i_FirstOperand - i_SecondOperand;
                break;
            case "*":
                dw_Return = i_FirstOperand * i_SecondOperand;
                break;
            case "/":
                dw_Return = i_FirstOperand / i_SecondOperand;
                break;
            default:
                dw_Return = 0;
        }
        return dw_Return;
    }
    public static String calculateRomeExpression(byte b_expr[]) {
        String str_Return = new String();
        /*
            Недопустимые латинские числа для обоих операндов VV, VVV, XVV, IIII, IIX, IIV,
            I - 73  V - 86  X - 88
            пробел - 32, перенос, 10.
            Первый раз с латинскими цифрами в программировании работаю !!!
         */
        boolean bool_SecondOperand = false;

        String str_FirstOperand = new String();
        String str_SecondOperand = new String();
        String str_Operator = new String();
        for (byte b: b_expr) {
            if (b == 10) break;
            if (isArithmeticOperator(b)) {
                bool_SecondOperand = true;
                str_Operator += (char) b;
                continue;
            }
            if (!bool_SecondOperand) str_FirstOperand += (char) b;
            else str_SecondOperand += (char) b;
        }

        // System.out.println("Первый операнд: " + str_FirstOperand);
        // System.out.println("Второй операнд: " + str_SecondOperand);
        // System.out.println("Оператор: " + str_Operator);

        // Ни первый ни второй операнд не могут содержать более 4 символов латинских цифр
        if (str_FirstOperand.length() > 4 || str_SecondOperand.length() > 4) GenerateException(); // return "throws Exception";
        // Операнды не должны быть пустыми
        if (str_FirstOperand.isEmpty() || str_SecondOperand.isEmpty()) GenerateException(); // return "throws Exception";

        int i_FirstOperand = convertRomeToDecimal(str_FirstOperand);
        int i_SecondOperand = convertRomeToDecimal(str_SecondOperand);
        if (i_FirstOperand == -1 || i_SecondOperand == -1) GenerateException(); // return "throws Exception";
        int i_Result;
        switch (str_Operator){
            case "+":
                i_Result = i_FirstOperand + i_SecondOperand;
                break;
            case "-":
                i_Result = i_FirstOperand - i_SecondOperand;
                break;
            case "*":
                i_Result = i_FirstOperand * i_SecondOperand;
                break;
            case "/":
                i_Result = i_FirstOperand / i_SecondOperand;
                break;
            default:
                i_Result = -1;
        }
        if (i_Result <= 0) GenerateException(); // return "throws Exception";
        str_Return = convertDecimalToRome(i_Result);


        return str_Return;
    }
    // Конвертирует римское число в десятичное для основного набора римских чисел
    public static int convertRomeToDecimalGeneralSymbols(String str_Rome){
        return switch (str_Rome) {
            case "I" -> 1;
            case "V" -> 5;
            case "X" -> 10;
            case "L" -> 50;
            case "C" -> 100;
            case "D" -> 500;
            case "M" -> 1000;
            default -> -1;
        };
    }
    // Конвертирует десятичные числа в римские для основного набора римских чисел.
    public static String convertDecimalToRomeGeneralSymbols(int i_num) {
        return switch (i_num){
            case 1 -> "I";
            case 5 -> "V";
            case 10 -> "X";
            case 50 -> "L";
            case 100 -> "C";
            case 500 -> "D";
            case 1000 -> "M";
            default -> "-";
        };
    }
    // Конвертирует римские числа в десятичные
    public static int convertRomeToDecimal (String str_Rome){
        return switch (str_Rome){
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            case "VI" -> 6;
            case "VII" -> 7;
            case "VIII" -> 8;
            case "IX" -> 9;
            case "X" -> 10;
            default -> -1;
        };
    }
    // Конвертирует десятичное число в римское
    public static String convertDecimalToRome(int i_Num) {
        String str_Return = new String();
        String str_Work = new String();
        int i_Work;
        if (i_Num > 0 && i_Num < 4) {
            for (int i = 0; i < i_Num; i++) {
                str_Work += convertDecimalToRomeGeneralSymbols(1);
            }
        }
        if (i_Num == 4) str_Work = "IV";
        if (i_Num == 5) str_Work = "V";
        if (i_Num > 5 && i_Num < 9){
            str_Work = "V" + convertDecimalToRome(i_Num - 5);
        }
        if (i_Num == 9) str_Work = "IX";
        if (i_Num == 10) str_Work = "X";
        if (i_Num > 10 && i_Num < 20) str_Work = "X" + convertDecimalToRome(i_Num % 10);
        if (i_Num > 19 && i_Num < 40){
            int i_CountOfDozens = (i_Num - (i_Num % 10)) / 10;
            for (int i = 0; i < i_CountOfDozens; i++) str_Work += "X";
            str_Work += convertDecimalToRome(i_Num % 10);
        }       
        if (i_Num > 39 && i_Num < 50){
            str_Work = "XL" + convertDecimalToRome(i_Num - 40);
        }
        if (i_Num > 49 && i_Num < 90){
            str_Work = convertDecimalToRomeGeneralSymbols(50);
            i_Work = i_Num - 50;
            str_Work += convertDecimalToRome(i_Work);
        }
        if (i_Num > 89 && i_Num < 100){
            str_Work = convertDecimalToRomeGeneralSymbols(1) +
                    convertDecimalToRomeGeneralSymbols(100);
            if ((90 - i_Num) > 0) str_Work += convertDecimalToRomeGeneralSymbols(90 - i_Num);
        }
        if (i_Num == 100) str_Work = convertDecimalToRomeGeneralSymbols(100);
        str_Return = str_Work;

        return str_Return;
    }
    public static void GenerateException(){
        throw new ArithmeticException();
    }
}