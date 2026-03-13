package io.nexstudios.itemservice.bukkit.builder.lore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class NexLoreBuilder {

  private sealed interface Entry permits Entry.Raw, Entry.Template {
    record Raw(Component component) implements Entry {}
    record Template(String miniMessage) implements Entry {}
  }

  private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final List<Entry> entries = new ArrayList<>();
  private TagResolver resolver = TagResolver.empty();

  /**
   * Sets a global TagResolver for template-based lore lines.
   */
  public NexLoreBuilder tagResolver(TagResolver resolver) {
    this.resolver = Objects.requireNonNull(resolver, "resolver must not be null");
    return this;
  }

  /**
   * Adds a raw Adventure Component line (no parsing).
   */
  public NexLoreBuilder line(Component line) {
    entries.add(new Entry.Raw(Objects.requireNonNull(line, "line must not be null")));
    return this;
  }

  /**
   * Adds a line that will be parsed at build() time.
   * Supports MiniMessage and legacy & color codes (e.g. &5, &l, &r).
   */
  public NexLoreBuilder line(String line) {
    Objects.requireNonNull(line, "line must not be null");
    entries.add(new Entry.Template(legacyAmpersandToMiniMessage(line)));
    return this;
  }

  public NexLoreBuilder lines(Component... lines) {
    Objects.requireNonNull(lines, "lines must not be null");
    Arrays.stream(lines).filter(Objects::nonNull).forEach(this::line);
    return this;
  }

  public NexLoreBuilder lines(String... lines) {
    Objects.requireNonNull(lines, "lines must not be null");
    Arrays.stream(lines).filter(Objects::nonNull).forEach(this::line);
    return this;
  }

  /**
   * Replaces a single token line (by plain text match) with multiple new lines.
   * Example token: "#rewards#"
   */
  public NexLoreBuilder replaceToken(String token, List<Component> replacement) {
    Objects.requireNonNull(token, "token must not be null");
    Objects.requireNonNull(replacement, "replacement must not be null");

    List<Entry> out = new ArrayList<>(entries.size() + replacement.size());

    for (Entry e : entries) {
      Component rendered = renderEntry(e);
      String plain = PLAIN.serialize(rendered == null ? Component.empty() : rendered);

      if (token.equals(plain)) {
        for (Component r : replacement) {
          out.add(new Entry.Raw(Objects.requireNonNull(r, "replacement line must not be null")));
        }
      } else {
        out.add(e);
      }
    }

    entries.clear();
    entries.addAll(out);
    return this;
  }

  public List<Component> build() {
    List<Component> out = new ArrayList<>(entries.size());
    for (Entry e : entries) {
      Component c = renderEntry(e);
      out.add(c == null ? Component.empty() : c);
    }
    return List.copyOf(out);
  }

  private Component renderEntry(Entry e) {
    return switch (e) {
      case Entry.Raw raw -> raw.component();
      case Entry.Template t -> MINI.deserialize(t.miniMessage(), resolver);
    };
  }

  /**
   * Converts legacy ampersand codes into MiniMessage tags.
   * Supports: &0-9, &a-f, &k, &l, &m, &n, &o, &r
   * Also supports hex: &#RRGGBB (converted to <#RRGGBB>).
   */
  private static String legacyAmpersandToMiniMessage(String in) {
    String s = in;

    // Hex: &#RRGGBB -> <#RRGGBB>
    s = s.replaceAll("(?i)&#([0-9a-f]{6})", "<#$1>");

    StringBuilder out = new StringBuilder(s.length() + 16);
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '&' && i + 1 < s.length()) {
        char code = Character.toLowerCase(s.charAt(i + 1));
        String tag = switch (code) {
          case '0' -> "<black>";
          case '1' -> "<dark_blue>";
          case '2' -> "<dark_green>";
          case '3' -> "<dark_aqua>";
          case '4' -> "<dark_red>";
          case '5' -> "<dark_purple>";
          case '6' -> "<gold>";
          case '7' -> "<gray>";
          case '8' -> "<dark_gray>";
          case '9' -> "<blue>";
          case 'a' -> "<green>";
          case 'b' -> "<aqua>";
          case 'c' -> "<red>";
          case 'd' -> "<light_purple>";
          case 'e' -> "<yellow>";
          case 'f' -> "<white>";
          case 'k' -> "<obfuscated>";
          case 'l' -> "<bold>";
          case 'm' -> "<strikethrough>";
          case 'n' -> "<underlined>";
          case 'o' -> "<italic>";
          case 'r' -> "<reset>";
          default -> null;
        };

        if (tag != null) {
          out.append(tag);
          i++; // skip code char
          continue;
        }
      }

      out.append(c);
    }

    return out.toString();
  }
}