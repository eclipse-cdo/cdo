package org.eclipse.net4j.util;


public final class Pair<T1, T2>
{
  private final T1 first;

  private final T2 second;

  public Pair(T1 first, T2 second)
  {
    this.first = first;
    this.second = second;
  }

  public T1 getFirst()
  {
    return first;
  }

  public T2 getSecond()
  {
    return second;
  }

  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof Pair)) return false;
    Pair that = (Pair) o;

    Object f1 = getFirst();
    Object f2 = that.getFirst();
    if (f1 == f2 || (f1 != null && f1.equals(f2)))
    {
      Object s1 = getSecond();
      Object s2 = that.getSecond();
      if (s1 == s2 || (s1 != null && s1.equals(s2))) return true;
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
  }

  @Override
  public String toString()
  {
    return "Pair[" + first + ", " + second + "]";
  }
}
