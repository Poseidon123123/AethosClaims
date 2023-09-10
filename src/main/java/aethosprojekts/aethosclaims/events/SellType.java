package aethosprojekts.aethosclaims.events;

public enum SellType {
    Buy(1),
    Claim(2),
    Nonne(3);

    private final int number;

    SellType(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
