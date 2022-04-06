package tr.com.infumia.versionmatched;

import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

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
) implements Predicate<String> {

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

  @Override
  public boolean test(final String s) {
    return this.version().equals(s);
  }

  /**
   * gets index of the first number.
   *
   * @return index of the first number.
   */
  private int indexOfFirstNumber() {
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

  /**
   * obtains the version.
   *
   * @return version.
   */
  @NotNull
  private String version() {
    final var sub = this.indexOfFirstNumber();
    Preconditions.checkState(sub != -1,
      "version() -> Invalid name for \"%s\"", this.rawClassName);
    return this.rawClassName.substring(sub);
  }
}
