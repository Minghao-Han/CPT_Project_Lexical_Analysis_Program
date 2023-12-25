package com.hmh;

import Exceptions.AccException;
import Exceptions.EofException;

import java.util.ArrayList;
import java.util.List;

public class Action {
    private static final List<String> needAddCharToSymbolName = new ArrayList<>();
    private static final List<String> needAddCharToSymbolValue = new ArrayList<>();
    private static final List<String> needAddConstToSymbolTable = new ArrayList<>();
    private static final List<String> needAddVariableToSymbolTable = new ArrayList<>();
    private static final List<String> needAddKeywordToSymbolTable = new ArrayList<>();
    static {
        needAddCharToSymbolName.add("s9");
        needAddCharToSymbolName.add("s10");

        needAddCharToSymbolValue.add("s5");//N
        needAddCharToSymbolValue.add("s7");//N
        needAddCharToSymbolValue.add("s42");//if
        needAddCharToSymbolValue.add("s13");//if

        needAddConstToSymbolTable.add("r18");

        needAddVariableToSymbolTable.add("r15");

        needAddKeywordToSymbolTable.add("s13");
    }
    protected String actionStr;
    public void execute() throws Exception{
        if (needAddCharToSymbolName.contains(actionStr)){
            SymbolManager.addBehindSymbolName(Main.input.peek().toString());
        }else if (needAddCharToSymbolValue.contains(actionStr)){
            SymbolManager.addBehindSymbolValue(Main.input.peek().toString());
        }
        if (needAddConstToSymbolTable.contains(actionStr)){
            SymbolManager.setSymbolType(SymbolType.CONST);
            SymbolManager.addToSymbolTable();
        } else if (needAddVariableToSymbolTable.contains(actionStr)) {
            SymbolManager.setSymbolType(SymbolType.VARIABLE);
            SymbolManager.addToSymbolTable();
        } else if (needAddKeywordToSymbolTable.contains(actionStr)) {
            SymbolManager.setSymbolType(SymbolType.KEYWORD);
            SymbolManager.addToSymbolTable();
        }
    }
    public static Action getAction(String rule) {
        if (rule==null || rule.isEmpty()){
            return new Eof();
        }
        if (rule.contains("r")){
            return new Regulation(rule);
        }
        if (rule.contains("s")){
            return new Shift(rule);
        }
        if (rule.equals("acc")){
            return new Accept();
        }
        return null;
    }
}

class Regulation extends Action{
    Integer indexOfRegulationRule;
    @Override
    public void execute() throws Exception {
        super.execute();
        ProductionRule productionRule = Main.productionRules.get(indexOfRegulationRule);
        int numOfBackEnd = productionRule.getNumOfBackEnd();
        for (int i=0;i<numOfBackEnd;i++){
            Main.accepted.pop();
            Main.states.pop();
        }
        Main.accepted.push(productionRule.getFrontEnd());
        String currentState = Main.states.peek();
        String nextState = RuleTableReader.getActionStr(currentState, productionRule.getFrontEnd());
        Main.states.push(nextState);
    }

    public Regulation(String actionStr) {
        super.actionStr = actionStr;
        this.indexOfRegulationRule = Integer.parseInt(actionStr.replace("r",""))-1;
    }
}

class Eof extends Action{

    @Override
    public void execute() throws EofException {
        throw new EofException();
    }
}
class Shift extends Action{
    private String nextState;
    @Override
    public void execute() throws Exception {
        super.execute();
        Main.accepted.push(Main.input.pop().toString());
        Main.states.push(nextState);
    }

    public Shift(String actionStr) {
        super.actionStr = actionStr;
        nextState=actionStr.replace("s","");
    }
}

//class Goto extends Action{
//    private String nextState;
//    @Override
//    public void execute() {
//        Main.states.push(nextState);
//    }
//
//    public Goto(String nextState) {
//        this.nextState = nextState;
//    }
//}

class Accept extends Action{

    @Override
    public void execute() throws AccException {
        throw new AccException();
    }
}
