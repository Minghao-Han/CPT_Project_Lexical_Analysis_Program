package com.hmh;

public class ProductionRule {
    private String frontEnd;
    private String backEnd;
    private Integer numOfBackEnd;

    public ProductionRule(String frontEnd, String backEnd) {
        this.frontEnd = frontEnd;
        this.backEnd = backEnd;
        //如果产生式右部是ε，则不需要弹出
        if (backEnd.equals("ε")) {
            numOfBackEnd=0;
            return;
        }
        //由于N'应算一个符，所以要去掉'来算产生式的右侧有多少个符号，作为从accepted栈弹出多少个符号的依据
        numOfBackEnd = backEnd.replace("'","").length();
    }

    public String getFrontEnd() {
        return frontEnd;
    }

    public void setFrontEnd(String frontEnd) {
        this.frontEnd = frontEnd;
    }

    public String getBackEnd() {
        return backEnd;
    }

    public void setBackEnd(String backEnd) {
        this.backEnd = backEnd;
    }

    public Integer getNumOfBackEnd() {
        return numOfBackEnd;
    }

    public void setNumOfBackEnd(Integer numOfBackEnd) {
        this.numOfBackEnd = numOfBackEnd;
    }
}
