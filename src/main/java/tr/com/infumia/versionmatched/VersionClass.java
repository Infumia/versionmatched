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
    final var index = new AtomicInteger();
    for (final var c : this.rawClassName.toCharArray()) {
      if (Character.isDigit(c)) {
        break;
      }
      index.incrementAndGet();
    }
    return index.get();
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
