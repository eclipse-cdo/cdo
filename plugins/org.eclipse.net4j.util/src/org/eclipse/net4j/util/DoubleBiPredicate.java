package org.eclipse.net4j.util;

import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 3.23
 */
@FunctionalInterface
public interface DoubleBiPredicate
{
  public boolean test(double v1, double v2);

  public default DoubleBiPredicate negate()
  {
    return (v1, v2) -> !test(v1, v2);
  }

  public default DoubleBiPredicate and(DoubleBiPredicate other)
  {
    Objects.requireNonNull(other);
    return (v1, v2) -> test(v1, v2) && other.test(v1, v2);
  }

  public default DoubleBiPredicate or(DoubleBiPredicate other)
  {
    Objects.requireNonNull(other);
    return (v1, v2) -> test(v1, v2) || other.test(v1, v2);
  }
}
