
package ObjectOriented;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SpellDispatchTest {

    // Challenge 8:
    // The declared reference type determines which overload is called.
    @Test
    void spellAndObjectReferencesSelectDifferentOverloads()
            throws Exception {

        Wizard wizard = new Wizard(10, 100);
        Spell spell = new Spell("Fireball", 20, 5);
        Object spellAsObject = spell;

        int initialMana = wizard.getManaStore();

        int spellResult = wizard.castSpell(spell);
        int manaAfterSpellCall = wizard.getManaStore();

        int objectResult = wizard.castSpell(spellAsObject);
        int manaAfterObjectCall = wizard.getManaStore();

        assertAll(
                () -> assertThat(spellResult).isEqualTo(40),
                () -> assertThat(manaAfterSpellCall)
                        .isEqualTo(initialMana - 20),
                () -> assertThat(objectResult).isEqualTo(10),
                () -> assertThat(manaAfterObjectCall)
                        .isEqualTo(manaAfterSpellCall)
        );
    }

    // Challenge 8:
    // Desired behavior: an Object reference holding a Spell should
    // use the Spell-handling behavior.
    // Expected to fail against the current Object overload.
    @Test
    void objectReferenceHoldingSpellUsesSpellHandling()
            throws Exception {

        Wizard wizard = new Wizard(10, 100);
        Object spellAsObject = new Spell("Fireball", 20, 5);

        int result = wizard.castSpell(spellAsObject);

        assertThat(result).isEqualTo(40);
    }

    // Example of safe runtime type checking and explicit dispatch.
    private int castSpellSafely(Wizard wizard, Object target)
            throws WizardException {

        if (!(target instanceof Spell)) {
            throw new IllegalArgumentException(
                    "Target must be a Spell");
        }

        return wizard.castSpell((Spell) target);
    }

    @Test
    void safeWrapperDispatchesSpellCorrectly()
            throws Exception {

        Wizard wizard = new Wizard(10, 100);
        Object spellAsObject = new Spell("Fireball", 20, 5);

        int result = castSpellSafely(wizard, spellAsObject);

        assertThat(result).isEqualTo(40);
    }

    @Test
    void safeWrapperRejectsNonSpellObject()
            throws Exception {

        Wizard wizard = new Wizard(10, 100);

        assertThatThrownBy(
                () -> castSpellSafely(wizard, "not a spell"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Target must be a Spell");
    }
}
