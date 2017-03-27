package oop.inheritance2;

public class 박쥐 extends 포유류 implements 날수있는{
    public 박쥐() {
        this.className = "박쥐";
    }

    @Override
    public void fly() {
        System.out.println(className + "가 날고 있음..");
    }
}
