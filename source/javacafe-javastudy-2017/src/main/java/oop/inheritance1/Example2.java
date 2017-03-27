package oop.inheritance1;

public class Example2 {
    public static void main(String[] args) {
        동물 animal = new 동물();
        동물 mammal = new 포유류();
        동물 whale = new 고래();
        동물 bat = new 박쥐();

        animal.showMe();
        mammal.showMe();
        whale.showMe();
        bat.showMe();
    }
}
