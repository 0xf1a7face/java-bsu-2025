package lab2;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a fixed-precision monetary amount (2 decimal places).
 */
public class Amount {
    private final BigDecimal value;

    public static Amount zero() {
        return new Amount(0.0);
    }

    public Amount(double amount) {
        this.value = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_EVEN);
    }

    public Amount(BigDecimal amount) {
        if (amount.scale() > 2) {
            this.value = amount.setScale(2, RoundingMode.HALF_EVEN);
        } else {
            this.value = amount;
        }
    }

    public Amount add(Amount other) {
        return new Amount(this.value.add(other.value));
    }

    public Amount subtract(Amount other) {
        return new Amount(this.value.subtract(other.value));
    }

    public boolean isNegative() {
        return value.signum() < 0;
    }

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Amount amount = (Amount) o;
        return value.compareTo(amount.value) == 0;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
