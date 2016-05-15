package pl.klimas7.calculatorPlugin;


public enum NumericalSystem {
    HEX(16), OCT(8), BIN(2), DEC(10);

    private final int base;

    NumericalSystem(int base) {
        this.base = base;
    }

    public int getBase() {
        return base;
    }
}
