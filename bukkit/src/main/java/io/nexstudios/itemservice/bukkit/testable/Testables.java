package io.nexstudios.itemservice.bukkit.testable;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public final class Testables {

  private Testables() {
  }

  public static Testable not(Testable t) {
    Objects.requireNonNull(t, "t must not be null");
    return stack -> !t.test(stack);
  }

  public static Testable and(Testable... tests) {
    Objects.requireNonNull(tests, "tests must not be null");
    return stack -> {
      for (Testable t : tests) {
        if (t == null) continue;
        if (!t.test(stack)) return false;
      }
      return true;
    };
  }

  public static Testable or(Testable... tests) {
    Objects.requireNonNull(tests, "tests must not be null");
    return stack -> {
      for (Testable t : tests) {
        if (t == null) continue;
        if (t.test(stack)) return true;
      }
      return false;
    };
  }

  public static Testable anyOf(List<? extends Testable> tests) {
    Objects.requireNonNull(tests, "tests must not be null");
    return stack -> {
      for (Testable t : tests) {
        if (t == null) continue;
        if (t.test(stack)) return true;
      }
      return false;
    };
  }

  public static Testable allOf(List<? extends Testable> tests) {
    Objects.requireNonNull(tests, "tests must not be null");
    return stack -> {
      for (Testable t : tests) {
        if (t == null) continue;
        if (!t.test(stack)) return false;
      }
      return true;
    };
  }

  public static Testable nonAir() {
    return stack -> stack != null && !stack.getType().isAir();
  }

  public static Testable material(org.bukkit.Material material) {
    Objects.requireNonNull(material, "material must not be null");
    return stack -> stack != null && stack.getType() == material;
  }

  public static Testable sameItem(ItemStack reference) {
    Objects.requireNonNull(reference, "reference must not be null");
    return stack -> reference.isSimilar(stack);
  }
}