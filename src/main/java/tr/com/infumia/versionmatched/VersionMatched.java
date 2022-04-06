package tr.com.infumia.versionmatched;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.reflection.RefConstructed;
import tr.com.infumia.reflection.cls.ClassOf;

/**
 * matches classes with your server version and choose the right class for instantiating instead of you.
 *
 * @param version the version of the server, pattern must be like that XXX1_14_R1 XXX1_13_R2.
 * @param versionClasses the version classes.
 * @param <T> the interface of classes.
 */
public record VersionMatched<T>(
  @NotNull String version,
  @NotNull Iterable<VersionClass<? extends T>> versionClasses
) {

  /**
   * ctor.
   *
   * @param version the version.
   * @param versionClasses the version classes.
   */
  @SafeVarargs
  public VersionMatched(@NotNull final String version, @NotNull final Class<? extends T>... versionClasses) {
    this(version,
      Stream.of(versionClasses)
        .map(VersionClass::new)
        .collect(Collectors.toSet()));
  }

  /**
   * ctor.
   *
   * @param versionClasses the version classes.
   */
  @SafeVarargs
  public VersionMatched(@NotNull final Class<? extends T>... versionClasses) {
    this(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1),
      Stream.of(versionClasses)
        .map(VersionClass::new)
        .collect(Collectors.toSet()));
  }

  /**
   * gets instantiated class.
   *
   * @param types the types to get.
   *
   * @return constructor of class of {@link T}.
   */
  @NotNull
  public RefConstructed<? extends T> of(@NotNull final Object... types) {
    final var match = this.match();
    return new ClassOf<>(match).getConstructor(types)
      .orElseThrow(() ->
        new IllegalStateException("Couldn't find any constructor on '%s' version!".formatted(
          match.getSimpleName())));
  }

  /**
   * gets instantiated class.
   *
   * @param types the types to get.
   *
   * @return constructor of class of {@link T}.
   */
  @NotNull
  public RefConstructed<? extends T> ofPrimitive(@NotNull final Object... types) {
    final var match = this.match();
    return new ClassOf<>(match).getPrimitiveConstructor(types)
      .orElseThrow(() ->
        new IllegalStateException("Couldn't find any constructor on '%s' version!".formatted(
          match.getSimpleName())));
  }

  /**
   * matches classes.
   *
   * @return class that match or throw exception.
   */
  @NotNull
  private Class<? extends T> match() {
    for (final var versionClass : this.versionClasses) {
      if (versionClass.test(this.version)) {
        return versionClass.versionClass();
      }
    }
    throw new IllegalStateException("Couldn't find any matched class on '%s' version!"
      .formatted(this.version));
  }
}
