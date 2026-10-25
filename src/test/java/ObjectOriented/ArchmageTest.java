
package ObjectOriented;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ArchmageTest {

    // Challenge 4:
    // The barrier must never reduce mana below zero.
    @Test
    void barrierDamageDoesNotMakeManaNegative()
            throws Exception {

        Spellcaster caster = new Archmage(10, 100);
        ((Archmage) caster).toggleBarrier();

        caster.receiveDamage(601);

        assertThat(caster.getManaStore())
                .isGreaterThanOrEqualTo(0);
    }

    // Challenge 5:
    // High power output must not trigger an automatic rest.
    @Test
    void highPowerSurgeDoesNotAutomaticallyRest()
            throws Exception {

        Archmage archmage = new Archmage(10, 100);
        archmage.beginChanneling();

        // 120 mana produces 180 base power, doubled to 360.
        int result = archmage.spellSurge(120);

        assertThat(result).isEqualTo(360);

        assertAll(
                () -> assertThat(archmage.getManaStore())
                        .isEqualTo(480),
                () -> assertThat(archmage.getCurrentStance())
                        .isEqualTo(Wizard.Stance.CHANNELING)
        );
    }
    @Test
    void highPowerSurgeDoesNotAutomaticallyRest2()
            throws Exception {

        Archmage archmage = new Archmage(10, 100);
        archmage.beginChanneling();

        int result = archmage.spellSurge(120);

        assertAll(
                () -> assertThat(result).isEqualTo(360),
                () -> assertThat(archmage.getManaStore())
                        .isEqualTo(480),
                () -> assertThat(archmage.getCurrentStance())
                        .isEqualTo(Wizard.Stance.CHANNELING)
        );
    }
    @Test
    void highPowerSurgeRevealsSubclassStateDifference()
            throws Exception {

        Wizard wizard = new Wizard(10, 100);
        Archmage archmage = new Archmage(10, 100);

        wizard.beginChanneling();
        archmage.beginChanneling();

        int wizardPower = wizard.spellSurge(120);
        int archmagePower = archmage.spellSurge(120);

        assertAll(
                () -> assertThat(wizardPower).isEqualTo(120),
                () -> assertThat(archmagePower).isEqualTo(360),
                () -> assertThat(wizard.getManaStore()).isEqualTo(480),
                () -> assertThat(archmage.getManaStore()).isEqualTo(480),
                () -> assertThat(archmage.getCurrentStance())
                        .isEqualTo(wizard.getCurrentStance())
        );
    }
    // Challenge 6:
    // Compare Wizard and Archmage using the same low-power action.
    // Power differs by design; mana and stance should match.
    @Test
    void wizardAndArchmagePreserveSharedStateBehavior()
            throws Exception {

        Wizard wizard = new Wizard(10, 100);
        Archmage archmage = new Archmage(10, 100);

        wizard.beginChanneling();
        archmage.beginChanneling();

        int wizardPower = wizard.spellSurge(1);
        int archmagePower = archmage.spellSurge(1);

        assertAll(
                () -> assertThat(wizardPower).isEqualTo(1),
                () -> assertThat(archmagePower).isEqualTo(2),
                () -> assertThat(archmage.getManaStore())
                        .isEqualTo(wizard.getManaStore()),
                () -> assertThat(archmage.getCurrentStance())
                        .isEqualTo(wizard.getCurrentStance())
        );
    }
}
