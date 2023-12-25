package com.hmh;

import Exceptions.AccException;
import Exceptions.EofException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Main {
    static Stack<String> accepted = new Stack<>();//移进后的
    static Stack<String> states = new Stack<>();
    static Stack<Character> input = new Stack<>();
    static List<ProductionRule> productionRules = new ArrayList<>();

    //将输入字符串放入栈中
    public static void handleInput(String inputString){
        char[] charArray = inputString.toCharArray();

        input.push('#');
        // 从后向前遍历字符数组，将字符逐个输入到Stack中
        for (int i = charArray.length - 1; i >= 0; i--) {
            input.push(charArray[i]);
        }
    }
    //移进
//    public static void moveIn(String toBeMoveIn,String state){
//        if (toBeMoveIn!=null){  //移进的是规约生成的非终结符
//            accepted.push(toBeMoveIn);
//            String nextState = Utils.getActionStr(states.peek(),toBeMoveIn);
//            states.push(nextState);
//        }else {  //移进的是输入的终结符
//            accepted.push(input.pop().toString());
//            states.push(state);
//        }
//
//    }

    public static void main(String[] args) {
        //读取产生式
        String productionRuleFilePath = "src/main/resources/production_rule.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(productionRuleFilePath))) {
            String prStr;
            while ((prStr= br.readLine())!=null) {//prStr stands for production rule str
                String[] prFrontAndBack = prStr.split(" ");
                productionRules.add(new ProductionRule(prFrontAndBack[0],prFrontAndBack[1]));
            }
        } catch (IOException e) {
            System.err.println("Error reading the Production Rule file: " + e.getMessage());
        }
        //读取输入
        String inputFilePath = "src/main/resources/input.txt";
        String inputString=null;
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            inputString = br.readLine();
            if (inputString == null){
                System.out.println("The file is empty.");
                return;
            }
        } catch (IOException e) {
            System.err.println("Error reading the input file: " + e.getMessage());
        }
        handleInput(inputString);
        states.push("0");
        try{
            while (!input.empty()){
                Character nextChar = input.peek();
                String currentState = states.peek();
                String ch = nextChar.toString();
                String str = RuleTableReader.getActionStr(currentState, ch);
                Action action = Action.getAction(str);
                action.execute();
            }
        }catch (EofException eofException){
            System.out.println("invalid input");
        }catch (AccException accException){
            System.out.println("input accepted");
        }catch (Exception e){
            throw new RuntimeException(e);
//            System.out.println(e.getMessage());
        }
        finally {
            System.out.println(states.toString());
            RuleTableReader.closeReader();
            SymbolManager.close();
        }
    }
}
