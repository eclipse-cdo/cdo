package org.eclipse.net4j.util;

import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 3.23
 */
@FunctionalInterface
public interface LongBiPredicate
{
  public boolean test(long v1, long v2);

  public default LongBiPredicate negate()
  {
    return (v1, v2) -> !test(v1, v2);
  }

  public default LongBiPredicate and(LongBiPredicate other)
  {
    Objects.requireNonNull(other);
    return (v1, v2) -> test(v1, v2) && other.test(v1, v2);
  }

  public default LongBiPredicate or(LongBiPredicate other)
  {
    Objects.requireNonNull(other);
    return (v1, v2) -> test(v1, v2) || other.test(v1, v2);
  }
}
