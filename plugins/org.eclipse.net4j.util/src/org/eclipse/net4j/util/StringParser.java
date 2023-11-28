/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.factory.SingletonFactory;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Eike Stepper
 * @since 3.23
 */
@FunctionalInterface
public interface StringParser<T> extends Function<String, T>
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.stringParsers"; //$NON-NLS-1$

  public static final StringParser<String> IDENTITY = str -> str;

  public static final StringParser<Character> CHARACTER = str -> str.charAt(0);

  public static final StringParser<Boolean> BOOLEAN = Boolean::parseBoolean;

  public static final StringParser<Byte> BYTE = str -> str.startsWith("0x") ? Byte.parseByte(str.substring(2), 16)
      : str.startsWith("0b") ? Byte.parseByte(str.substring(2), 2) : Byte.parseByte(str);

  public static final StringParser<Short> SHORT = str -> str.startsWith("0x") ? Short.parseShort(str.substring(2), 16)
      : str.startsWith("0b") ? Short.parseShort(str.substring(2), 2) : Short.parseShort(str);

  public static final StringParser<Integer> INTEGER = str -> str.startsWith("0x") ? Integer.parseInt(str.substring(2), 16)
      : str.startsWith("0b") ? Integer.parseInt(str.substring(2), 2) : Integer.parseInt(str);

  public static final StringParser<Long> LONG = str -> str.startsWith("0x") ? Long.parseLong(str.substring(2), 16)
      : str.startsWith("0b") ? Long.parseLong(str.substring(2), 2) : Long.parseLong(str);

  public static final StringParser<Float> FLOAT = Float::parseFloat;

  public static final StringParser<Double> DOUBLE = Double::parseDouble;

  public static final StringParser<BigInteger> BIG_INTEGER = str -> str.startsWith("0x") ? new BigInteger(str.substring(2), 16)
      : str.startsWith("0b") ? new BigInteger(str.substring(2), 2) : new BigInteger(str);

  public static final StringParser<BigDecimal> BIG_DECIMAL = BigDecimal::new;

  public static final StringParser<Path> PATH = str -> FileSystems.getDefault().getPath(str);

  public static final StringParser<File> FILE = File::new;

  @Override
  public T apply(String str);

  public default StringParser<T> safe()
  {
    return str -> str == null ? null : apply(str);
  }

  public static <T> StringParser<T> safe(StringParser<T> other)
  {
    Objects.requireNonNull(other);
    return other.safe();
  }

  /**
   * @author Eike Stepper
   */
  public static final class EnumStringParser<T extends Enum<T>> implements StringParser<T>
  {
    public static final boolean DEFAULT_CASE_SENSITIVE = false;

    private final Class<T> enumType;

    private final boolean caseSensitive;

    public EnumStringParser(Class<T> enumType, boolean caseSensitive)
    {
      this.enumType = enumType;
      this.caseSensitive = caseSensitive;
    }

    public EnumStringParser(Class<T> enumType)
    {
      this(enumType, DEFAULT_CASE_SENSITIVE);
    }

    public Class<T> getEnumType()
    {
      return enumType;
    }

    public boolean isCaseSensitive()
    {
      return caseSensitive;
    }

    @Override
    public T apply(String str)
    {
      if (!caseSensitive)
      {
        for (T t : enumType.getEnumConstants())
        {
          if (t.name().equalsIgnoreCase(str))
          {
            return t;
          }
        }

        throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + str);
      }

      return Enum.valueOf(enumType, str);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class MetaFactory extends org.eclipse.net4j.util.factory.MetaFactory
  {
    private static final String PG = StringParser.PRODUCT_GROUP;

    private static final IFactory[] CHILDREN = { //
        new SingletonFactory(PG, "identity", IDENTITY), //
        new SingletonFactory(PG, String.class.getName(), IDENTITY), //

        new SingletonFactory(PG, char.class.getName(), CHARACTER), //
        new SingletonFactory(PG, Character.class.getName(), CHARACTER), //

        new SingletonFactory(PG, boolean.class.getName(), BOOLEAN), //
        new SingletonFactory(PG, Boolean.class.getName(), BOOLEAN), //

        new SingletonFactory(PG, byte.class.getName(), BYTE), //
        new SingletonFactory(PG, Byte.class.getName(), BYTE), //

        new SingletonFactory(PG, short.class.getName(), SHORT), //
        new SingletonFactory(PG, Short.class.getName(), SHORT), //

        new SingletonFactory(PG, int.class.getName(), INTEGER), //
        new SingletonFactory(PG, Integer.class.getName(), INTEGER), //

        new SingletonFactory(PG, long.class.getName(), LONG), //
        new SingletonFactory(PG, Long.class.getName(), LONG), //

        new SingletonFactory(PG, float.class.getName(), FLOAT), //
        new SingletonFactory(PG, Float.class.getName(), FLOAT), //

        new SingletonFactory(PG, double.class.getName(), DOUBLE), //
        new SingletonFactory(PG, Double.class.getName(), DOUBLE), //

        new SingletonFactory(PG, BigInteger.class.getName(), BIG_INTEGER), //
        new SingletonFactory(PG, BigDecimal.class.getName(), BIG_DECIMAL), //

        new SingletonFactory(PG, Path.class.getName(), PATH), //
        new SingletonFactory(PG, File.class.getName(), FILE), //
    };

    public MetaFactory()
    {
      super(PG);
    }

    @Override
    public IFactory[] create(String description) throws ProductCreationException
    {
      return CHILDREN;
    }
  }
}
