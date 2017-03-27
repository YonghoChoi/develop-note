package oop.inheritance2;

public class Example2 {
    public static void main(String[] args) {
        날수있는[] animals = new 날수있는[2];

        animals[0] = new 박쥐();
        animals[1] = new 참새();

        for(int i=0; i<animals.length; i++) {
            animals[i].fly();
        }
    }
}