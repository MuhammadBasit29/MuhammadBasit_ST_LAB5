
package ObjectOriented;

public class Archmage extends Wizard {

    private boolean barrierActive = false;

    public Archmage(int level, int maxHp)
            throws HeroLevelException {
        super(level, maxHp);
    }

    @Override
    public double getPowerMultiplier() {
        return 2.0;
    }

    @Override
    public void receiveDamage(int damage) {
        if (barrierActive) {
            // Challenge 4 bug: mana can become negative.
            manaStore -= damage;
        } else {
            super.receiveDamage(damage);
        }
    }

    public void toggleBarrier() {
        barrierActive = !barrierActive;
    }

    @Override
    public int spellSurge(int manaInvested)
            throws WizardException {

        int result = super.spellSurge(manaInvested);

        // Challenge 5 bug: automatically rests after high output.
        if (result > 200) {
            this.rest();
        }

        return result;
    }
}
