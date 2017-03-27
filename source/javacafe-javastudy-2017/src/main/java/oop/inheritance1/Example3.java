package oop.inheritance1;

public class Example3 {
    public static void main(String[] args) {
        동물[] animals = new 동물[4];

        animals[0] = new 동물();
        animals[1] = new 포유류();
        animals[2] = new 고래();
        animals[3] = new 박쥐();

        for(int i = 0; i < animals.length; i++) {
            animals[i].showMe();
        }
    }
}
