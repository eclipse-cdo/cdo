package org.eclipse.net4j.util;


public class BitHelper
{
  public static final int LOWEST_BIT = 0x0000001;

  public static int getMask(int bits, int rightPad)
  {
    int mask = LOWEST_BIT;

    // Create bit mask
    for (int i = 0; i < bits - 1; i++)
    {
      mask <<= 1;
      mask |= LOWEST_BIT;
    }

    return mask << rightPad;
  }

  public static int getMask(int bits)
  {
    return getMask(bits, 0);
  }
}
