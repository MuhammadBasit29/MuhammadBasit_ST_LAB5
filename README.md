# Object-Oriented Spellcasting Module — Unit Testing Lab

## 1. Overview

This project tests the supplied spellcasting module (`Spellcaster`, `Wizard`, `Archmage`, `Wand`, and `Spell`) using JUnit 5 and AssertJ.

The assignment requires unit tests to identify defects involving object state, class invariants, inheritance, polymorphism, parameter passing, and overloaded method resolution.

**The goal is to identify defects in the supplied implementation.** The production code does not need to be fixed unless the instructor separately requires bug fixes.

## 2. Technologies

- Java
- Maven
- JUnit 5
- AssertJ
- IntelliJ IDEA

## 3. Test Results

Latest full Maven run, October 25, 2026:

| Result | Count |
|---|---:|
| Tests run | 18 |
| Passed | 10 |
| Failed | 8 |
| Errors | 0 |
| Skipped | 0 |

**Build status: BUILD FAILURE**

Maven reports a build failure because eight test assertions failed. The tests did execute; zero errors were reported. These failures help identify defects where the expected behavior is correct according to the assignment specification.

## 4. Challenge-by-Challenge Results

### Challenge 1 — The Infinite Exhaustion Trap

**Expected behavior:** Calling `rest()` must restore mana to 600 and reset the wizard's stance from `EXHAUSTED` to `IDLE`.

**Test:** `restWhenExhaustedResetsStanceToIdle()`

**Test procedure:**
1. Create a wizard.
2. Begin channeling.
3. Execute four consecutive surges.
4. Verify that the wizard becomes exhausted.
5. Call `rest()`.
6. Assert that mana is 600 and stance is `IDLE`.

**Result:** Failed. Expected `IDLE`, but actual stance was `EXHAUSTED`.

**Conclusion:** The test identifies a defect: `rest()` restores mana but does not reset the stance as required.

### Challenge 2 — Unchannelled Surge Execution

**Expected behavior:** Calling `spellSurge()` while the wizard is `IDLE` must throw `InvalidStateException` without changing mana or consecutive surge state.

**Test:** `spellSurgeWhileIdleThrowsWithoutChangingState()`

**Test procedure:** Call `spellSurge(1)` without first calling `beginChanneling()`. Verify the exception and check that mana and stance remain unchanged.

**Result:** Passed in the reported test run.

**Conclusion:** The tested idle-state precondition behaves as expected. The assignment also names `consecutiveSurges`; verify that it is checked if a suitable getter is available.

### Challenge 3 — Mana Store Boundary Invariant Under Drain

**Expected behavior:** Mana must remain between 0 and 600. An unaffordable spell must not make mana negative.

**Tests:**
- `spellSurgePreservesManaInvariant()`
- `castSpellWithInsufficientManaPreservesMana()`
- `castSpellBelowRequiredLevelPreservesMana()`

**Results:**
- The parameterized surge boundary test passed for its tested investment values.
- The insufficient-mana test failed because no exception was thrown.
- The below-required-level test passed.

**Conclusion:** The insufficient-mana failure identifies a defect: `castSpell(Spell)` does not reject a spell costing 601 when the wizard has 600 mana. This can violate the mana invariant. The challenge is partially covered; a combined sequence involving both `spellSurge()` and `castSpell()` would provide stronger coverage.

### Challenge 4 — Liskov Substitution Principle Violation

**Expected behavior:** An `Archmage` must never have negative mana, including when accessed through a `Spellcaster` reference.

**Test:** `barrierDamageDoesNotMakeManaNegative()`

**Test procedure:** Create `Spellcaster caster = new Archmage(10, 100)`, activate the barrier, and apply damage of 601.

**Result:** Failed. Expected mana to be at least 0, but actual mana was -1.

**Conclusion:** The overridden `Archmage.receiveDamage()` violates the mana invariant when the barrier absorbs more damage than the available mana.

### Challenge 5 — The “Yo-Yo Effect” Cascade

**Expected behavior:** Test the state changes caused when `Archmage.spellSurge()` calls `super.spellSurge()` and then automatically invokes `rest()` for output above 200.

**Tests:**
- `highPowerSurgeDoesNotAutomaticallyRest()`
- `highPowerSurgeDoesNotAutomaticallyRest2()`
- `highPowerSurgeRevealsSubclassStateDifference()`

**Results:** The high-power tests expected mana to be 480 but observed 600, indicating that the automatic rest refilled mana.

One test also expected output of 120 but observed 180. That output expectation must be checked and corrected: for the described level-10 Archmage and investment of 120, the base output is 180 and the Archmage multiplier doubles it to **360**.

**Conclusion:** The unexpected mana refill identifies the automatic-rest side effect. Correct the output expectation to 360, then retain assertions for mana, stance, and consecutive surge count so the test checks the intended defect rather than an incorrect expected value.

### Challenge 6 — Dynamic Binding and Polymorphic Substitution

**Expected behavior:** Compare equivalent operations on `Wizard` and `Archmage` and identify unintended differences in state behavior. Power output may differ because the classes have different multipliers.

**Test:** `wizardAndArchmagePreserveSharedStateBehavior()`

**Test procedure:** Begin channeling on both objects, execute the same low-power surge, and compare mana and stance.

**Result:** The low-power comparison passed. The high-power tests showed that `Archmage` refills mana after a high-power surge.

**Conclusion:** The low-power test is a useful baseline, but it does not prove full behavioral equivalence. A matching high-power sequence on both objects should explicitly demonstrate the unintended state difference.

### Challenge 7 — Parameter Swap in Mana Transfer

**Expected behavior:** Transferring 100 mana with priority tier 1 must reduce the sender's mana from 600 to 500 and increase the recipient's mana from 600 to 700.

**Test:** `transferManaDeductsExactAmount()`

**Result:** Failed. Expected sender mana to be 500, but actual mana was 599.

**Conclusion:** The implementation deducts the priority tier (1) instead of the transfer amount (100). The test identifies the parameter-use defect.

### Challenge 8 — Static Overload Resolution vs. Dynamic Dispatch

**Expected behavior:** Demonstrate how the declared reference type determines which overloaded `castSpell()` method Java selects. Include a safe wrapper that performs runtime type checking.

**Tests:**
- `spellAndObjectReferencesSelectDifferentOverloads()`
- `objectReferenceHoldingSpellUsesSpellHandling()`
- `safeWrapperDispatchesSpellCorrectly()`
- `safeWrapperRejectsNonSpellObject()`

**Results:**
- The comparison test passed: a `Spell` reference returned 40 and deducted 20 mana, while an `Object` reference returned 10 without changing mana.
- The test expecting the `Object` reference to use spell handling failed: expected 40, actual 10.
- Both safe-wrapper tests passed.

**Conclusion:** Java selects an overload using the argument's compile-time type. The wrapper safely routes a `Spell` object to `castSpell(Spell)` and rejects non-`Spell` objects. The differing overload behavior is a defect only if the intended contract requires spell-specific handling for an `Object` reference containing a `Spell`; the overload selection itself is normal Java behavior.

## 5. Summary of the Eight Failures

| Failing test | Expected | Actual | Challenge and finding |
|---|---|---|---|
| `barrierDamageDoesNotMakeManaNegative` | Mana ≥ 0 | -1 | Challenge 4: barrier can make mana negative |
| `highPowerSurgeDoesNotAutomaticallyRest` | Mana = 480 | 600 | Challenge 5: automatic rest refills mana |
| `highPowerSurgeDoesNotAutomaticallyRest2` | Mana = 480 | 600 | Challenge 5: same automatic-rest behavior |
| `highPowerSurgeRevealsSubclassStateDifference` | Output = 120; mana = 480 | Output = 180; mana = 600 | Challenge 5: mana refill is a defect; output expectation needs correction to 360 |
| `objectReferenceHoldingSpellUsesSpellHandling` | Return = 40 | 10 | Challenge 8: `Object` overload is selected |
| `castSpellWithInsufficientManaPreservesMana` | Exception thrown | No exception | Challenge 3: insufficient mana is not rejected |
| `restWhenExhaustedResetsStanceToIdle` | `IDLE` | `EXHAUSTED` | Challenge 1: rest does not reset stance |
| `transferManaDeductsExactAmount` | Sender mana = 500 | 599 | Challenge 7: wrong parameter is deducted |


## 7. How to Run the Tests

From the project directory containing `pom.xml`, run:

```bash
mvn test
```

To run each test class individually:

```bash
mvn -Dtest=WizardTest test
mvn -Dtest=ArchmageTest test
mvn -Dtest=SpellDispatchTest test
```

After correcting test expectations or adding coverage, rerun the complete suite and update the results above to match the latest Maven output.

## 8. Conclusion

The test suite identifies defects involving stance restoration, mana boundary enforcement, barrier damage, automatic rest after a high-power surge, mana transfer, and overloaded method behavior.

A `BUILD FAILURE` is expected when defect-revealing assertions fail against the supplied implementation. However, each expected value must be correct according to the assignment. If you will run the test one by one, you will see the failed testcases if expected ad actual outputs
