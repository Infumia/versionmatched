package tr.com.infumia.versionmatched;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitversion.BukkitVersion;
import tr.com.infumia.reflection.RefConstructed;
import tr.com.infumia.reflection.cls.ClassOf;

/**
 * matches classes with your server version and choose the right class for instantiating instead of you.
 *
 * @param version the version of the server, pattern must be like that 1_14_R1 1_13_R2.
 * @param versionClasses the version classes.
 * @param <T> the interface of classes.
 */
public record VersionMatched<T>(
  @NotNull BukkitVersion version,
  @NotNull Collection<VersionClass<T>> versionClasses
) {

  /**
   * ctor.
   *
   * @param version the version.
   * @param versionClasses the version classes.
   */
  public VersionMatched(@NotNull final String version, @NotNull final Collection<VersionClass<T>> versionClasses) {
    this(new BukkitVersion(version), versionClasses);
  }

  /**
   * ctor.
   *
   * @param version the version.
   * @param versionClasses the version classes.
   */
  @SafeVarargs
  public VersionMatched(@NotNull final String version, @NotNull final Class<? extends T>... versionClasses) {
    this(version,
      Arrays.stream(versionClasses)
        .map((Function<Class<? extends T>, VersionClass<T>>) VersionClass::new)
        .collect(Collectors.toList()));
  }

  /**
   * ctor.
   *
   * @param versionClasses the version classes.
   */
  @SafeVarargs
  public VersionMatched(@NotNull final Class<? extends T>... versionClasses) {
    this(new BukkitVersion(),
      Arrays.stream(versionClasses)
        .map((Function<Class<? extends T>, VersionClass<T>>) VersionClass::new)
        .collect(Collectors.toList()));
  }

  /**
   * gets instantiated class.
   *
   * @param types the types to get.
   *
   * @return constructor of class of {@link T}.
   */
  @NotNull
  public RefConstructed<T> of(@NotNull final Object... types) {
    final var match = this.match();
    // noinspection unchecked
    return (RefConstructed<T>) new ClassOf<>(match).getConstructor(types)
      .orElseThrow(() ->
        new IllegalStateException(String.format("match() -> Couldn't find any constructor on \"%s\" version!",
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
  public RefConstructed<T> ofPrimitive(@NotNull final Object... types) {
    final var match = this.match();
    // noinspection unchecked
    return (RefConstructed<T>) new ClassOf<>(match).getPrimitiveConstructor(types)
      .orElseThrow(() ->
        new IllegalStateException(String.format("match() -> Couldn't find any constructor on \"%s\" version!",
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
      if (versionClass.match(this.version)) {
        return versionClass.versionClass();
      }
    }
    throw new IllegalStateException(String.format("match() -> Couldn't find any matched class on \"%s\" version!",
      this.version.version()));
  }
}
