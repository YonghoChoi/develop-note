package oop.inheritance2;

public class 참새 extends 조류 implements 날수있는{
    public 참새() {
        this.className = "참새";
    }


    @Override
    public void fly() {
        System.out.println(className + "가 날고 있음..");
    }
}
