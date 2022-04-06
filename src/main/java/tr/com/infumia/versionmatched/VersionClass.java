package tr.com.infumia.versionmatched;

import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitversion.BukkitVersion;

/**
 * a class that represents version classes.
 *
 * @param rawClassName the raw class name.
 * @param versionClass the version class.
 * @param <T> type of the class.
 */
record VersionClass<T>(
  @NotNull String rawClassName,
  @NotNull Class<? extends T> versionClass
) {

  /**
   * the numbers.
   */
  private static final char[] NUMBERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

  /**
   * ctor.
   *
   * @param versionClass the version class.
   */
  VersionClass(@NotNull final Class<? extends T> versionClass) {
    this(versionClass.getSimpleName(), versionClass);
  }

  /**
   * matches the given version.
   *
   * @param version the version to match.
   *
   * @return {@code true} if the version matched.
   */
  boolean match(@NotNull final BukkitVersion version) {
    return this.version().equals(version);
  }

  /**
   * obtains the version.
   *
   * @return version.
   */
  @NotNull
  private BukkitVersion version() {
    final var sub = this.versionSubString();
    Preconditions.checkState(sub != -1, "version() -> Invalid name for \"%s\"", this.rawClassName);
    return new BukkitVersion(this.rawClassName.substring(sub));
  }

  /**
   * obtains the version sub string.
   *
   * @return version sub string.
   */
  private int versionSubString() {
    final var subString = new AtomicInteger();
    finalBreak:
    for (final var name : this.rawClassName.toCharArray()) {
      for (final var number : VersionClass.NUMBERS) {
        if (name == number) {
          break finalBreak;
        }
      }
      subString.incrementAndGet();
    }
    return subString.get();
  }
}
