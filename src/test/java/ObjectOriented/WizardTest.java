package ObjectOriented;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class WizardTest {

    // Challenge 1:
    // Rest must restore mana and reset stance to IDLE.
    @Test
    void restWhenExhaustedResetsStanceToIdle()
            throws Exception {

        Wizard wizard = new Wizard(1, 100);
        wizard.beginChanneling();

        for (int i = 0; i < 4; i++) {
            wizard.spellSurge(1);
        }

        assertThat(wizard.getCurrentStance())
                .isEqualTo(Wizard.Stance.EXHAUSTED);

        wizard.rest();

        assertAll(
                () -> assertThat(wizard.getManaStore())
                        .isEqualTo(600),
                () -> assertThat(wizard.getCurrentStance())
                        .isEqualTo(Wizard.Stance.IDLE)
        );
    }

    // Challenge 2:
    // Surging while IDLE must throw and preserve observable state.
    @Test
    void spellSurgeWhileIdleThrowsWithoutChangingState()
            throws Exception {

        Wizard wizard = new Wizard(1, 100);
        int initialMana = wizard.getManaStore();

        assertThatThrownBy(() -> wizard.spellSurge(1))
                .isInstanceOf(InvalidStateException.class);

        assertAll(
                () -> assertThat(wizard.getManaStore())
                        .isEqualTo(initialMana),
                () -> assertThat(wizard.getCurrentStance())
                        .isEqualTo(Wizard.Stance.IDLE)
        );
    }

    // Challenge 3:
    // Successful surges must preserve mana in the range 0..600.
    @ParameterizedTest
    @ValueSource(ints = {1, 100, 200, 600})
    void spellSurgePreservesManaInvariant(int investment)
            throws Exception {

        Wizard wizard = new Wizard(1, 100);
        wizard.beginChanneling();

        for (int i = 0; i < 3; i++) {
            if (wizard.getManaStore() < investment) {
                break;
            }

            wizard.spellSurge(investment);

            assertThat(wizard.getManaStore())
                    .isBetween(0, Spellcaster.MAX_MANA_STORE);
        }

        int manaBefore = wizard.getManaStore();

        assertThatThrownBy(
                () -> wizard.spellSurge(Spellcaster.MAX_MANA_STORE + 1))
                .isInstanceOf(ManaInvestedException.class);

        assertThat(wizard.getManaStore()).isEqualTo(manaBefore);
    }

    // Challenge 3:
    // Casting a spell with insufficient mana must not make mana negative.
    // Expected to fail: current castSpell subtracts 601 from 600.
    @Test
    void castSpellWithInsufficientManaPreservesMana()
            throws Exception {

        Wizard wizard = new Wizard(10, 100);
        Spell expensiveSpell = new Spell("Expensive", 601, 1);
        int initialMana = wizard.getManaStore();

        assertThatThrownBy(() -> wizard.castSpell(expensiveSpell))
                .isInstanceOf(ManaInvestedException.class);

        assertThat(wizard.getManaStore()).isEqualTo(initialMana);
    }

    // Challenge 3:
    // A spell requiring a higher level must be rejected without
    // changing the wizard's mana.
    @Test
    void castSpellBelowRequiredLevelPreservesMana()
            throws Exception {

        Wizard wizard = new Wizard(2, 100);
        Spell spell = new Spell("Advanced Spell", 50, 5);
        int initialMana = wizard.getManaStore();

        assertThatThrownBy(() -> wizard.castSpell(spell))
                .isInstanceOf(HeroLevelException.class);

        assertThat(wizard.getManaStore()).isEqualTo(initialMana);
    }

    // Challenge 7:
    // Sender should lose exactly the amount transferred.
    @Test
    void transferManaDeductsExactAmount()
            throws Exception {

        Wizard sender = new Wizard(1, 100);
        Wizard recipient = new Wizard(1, 100);

        sender.transferMana(recipient, 100, 1);

        assertAll(
                () -> assertThat(sender.getManaStore())
                        .isEqualTo(500),
                () -> assertThat(recipient.getManaStore())
                        .isEqualTo(700)
        );
    }
}
