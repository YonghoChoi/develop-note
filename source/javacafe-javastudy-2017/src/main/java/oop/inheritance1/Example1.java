package oop.inheritance1;

public class Example1 {
    public static void main(String[] args) {
        동물 animal = new 동물();
        포유류 mammal = new 포유류();
        고래 whale = new 고래();
        박쥐 bat = new 박쥐();

        animal.showMe();
        mammal.showMe();
        whale.showMe();
        bat.showMe();
    }
}
