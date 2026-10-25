package ObjectOriented;

public class Wizard extends Spellcaster {

    public enum Stance {
        IDLE, CHANNELING, EXHAUSTED
    }

    public static final int MIN_MANA_INVESTED = 1;
    public static final int SAFE_CAST_MOD = 10;
    public static final int OVERCHARGE_MOD = 12;
    public static final double OVERCHARGE_BONUS = 1.5;

    public final String negativeManaInvestedError =
            "Mana invested must be positive";

    public final String manaInvestedError =
            "Mana invested must be less than mana store";

    private Stance currentStance;
    private Wand equippedWand;
    private int consecutiveSurges = 0;

    public Wizard(int level, int maxHp)
            throws HeroLevelException {
        super(level, maxHp);
        this.currentStance = Stance.IDLE;
    }

    public void setWand(Wand wand) {
        this.equippedWand = wand;
    }

    public Stance getCurrentStance() {
        return currentStance;
    }

    public void beginChanneling()
            throws InvalidStateException {
        if (currentStance == Stance.EXHAUSTED) {
            throw new InvalidStateException(
                    "Exhausted wizards cannot channel.");
        }

        currentStance = Stance.CHANNELING;
    }

    public void rest() {
        manaStore = MAX_MANA_STORE;
        consecutiveSurges = 0;

        // Challenge 1 bug:
        // currentStance is not reset to IDLE.
    }

    public int spellSurge(int manaInvested)
            throws WizardException {

        if (currentStance != Stance.CHANNELING) {
            throw new InvalidStateException(
                    "Must begin channeling before surging.");
        }

        if (manaInvested < MIN_MANA_INVESTED) {
            throw new ManaInvestedException(
                    negativeManaInvestedError);
        }

        if (manaInvested > manaStore) {
            throw new ManaInvestedException(
                    manaInvestedError);
        }

        consecutiveSurges++;

        if (consecutiveSurges > 3) {
            currentStance = Stance.EXHAUSTED;
        }

        int effectiveLevel = level;

        if (equippedWand != null) {
            effectiveLevel += equippedWand.getPowerBonus();
        }

        int outputPower = 0;

        if (manaInvested <= SAFE_CAST_MOD * effectiveLevel) {
            outputPower = manaInvested;
        } else if (manaInvested <=
                OVERCHARGE_MOD * effectiveLevel) {
            outputPower =
                    (int) (manaInvested * OVERCHARGE_BONUS);
        }

        manaStore -= manaInvested;

        return (int) (outputPower * getPowerMultiplier());
    }

    @Override
    public double getPowerMultiplier() {
        return 1.0;
    }

    public void transferMana(
            Wizard recipient, int amount, int priorityTier)
            throws WizardException {

        if (amount > manaStore) {
            throw new ManaInvestedException(
                    "Insufficient mana to transfer.");
        }

        // Challenge 7 bug:
        // Subtracts priorityTier instead of amount.
        manaStore -= priorityTier;
        recipient.manaStore += amount;
    }

    public int castSpell(Spell spell)
            throws WizardException {

        if (level < spell.getMinLevelRequired()) {
            throw new HeroLevelException(
                    "Level too low for spell.");
        }

        // Challenge 3 bug:
        // No check that enough mana is available.
        manaStore -= spell.getBaseManaCost();

        return spell.getBaseManaCost() * 2;
    }

    public int castSpell(Object target) {
        // Challenge 8: different overload behavior.
        return 10;
    }
}
