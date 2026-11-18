/*
 * Copyright (c) 2023-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.factory.SingletonFactory;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

/**
 * Converts any kind of {@link Object objects} to strings.
 * <p>
 * Built-in argument converters use comma-separated operands in expressions processed by
 * {@link StringUtil#convert(String, IManagedContainer)}. Nested expression operands
 * are evaluated before the enclosing converter is invoked. Boolean converters accept only trimmed, case-insensitive
 * {@code true} and {@code false} values.
 *
 * @author Eike Stepper
 * @since 3.23
 */
@FunctionalInterface
public interface StringConverter extends Function<Object, String>
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.stringConverters"; //$NON-NLS-1$

  public static final StringConverter IDENTITY = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return StringUtil.safe(value, null);
    }
  };

  public static final StringConverter CHARS = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : value instanceof char[] ? String.valueOf((char[])value) : value.toString();
    }
  };

  public static final StringConverter SAFE = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return StringUtil.safe(value);
    }
  };

  /**
   * @since 3.28
   */
  public static final StringConverter HEX = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : HexUtil.bytesToHex((byte[])value);
    }
  };

  /**
   * @since 3.28
   */
  public static final StringConverter BASE64 = new StringConverter()
  {
    private final Encoder encoder = Base64.getEncoder();

    @Override
    public String apply(Object value)
    {
      return value == null ? null : encoder.encodeToString((byte[])value);
    }
  };

  public static final StringConverter UPPER = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : StringUtil.safe(value).toUpperCase();
    }
  };

  public static final StringConverter LOWER = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : StringUtil.safe(value).toLowerCase();
    }
  };

  /**
   * Compares two strings case-sensitively; surrounding whitespace on each operand is ignored.
   *
   * @since 3.31
   */
  public static final StringConverter EQUALS = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      WithArguments.requireCount("equals", operands, 2, 2); //$NON-NLS-1$
      return Boolean.toString(WithArguments.string("equals", operands, 0).trim() //$NON-NLS-1$
          .equals(WithArguments.string("equals", operands, 1).trim())); //$NON-NLS-1$
    }
  };

  /**
   * Compares two strings case-insensitively; surrounding whitespace on each operand is ignored. Its converter name is
   * {@code equals_ignore_case}.
   *
   * @since 3.31
   */
  public static final StringConverter EQUALS_IGNORE_CASE = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      WithArguments.requireCount("equals_ignore_case", operands, 2, 2); //$NON-NLS-1$
      return Boolean.toString(WithArguments.string("equals_ignore_case", operands, 0).trim() //$NON-NLS-1$
          .equalsIgnoreCase(WithArguments.string("equals_ignore_case", operands, 1).trim())); //$NON-NLS-1$
    }
  };

  /**
   * Tests whether the first string contains the second string, with case-sensitive matching.
   *
   * @since 3.31
   */
  public static final StringConverter CONTAINS = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      WithArguments.requireCount("contains", operands, 2, 2); //$NON-NLS-1$
      return Boolean.toString(WithArguments.string("contains", operands, 0) //$NON-NLS-1$
          .contains(WithArguments.string("contains", operands, 1))); //$NON-NLS-1$
    }
  };

  /**
   * Tests whether the first string contains the second string, ignoring case.
   *
   * @since 3.31
   */
  public static final StringConverter CONTAINS_IGNORE_CASE = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      WithArguments.requireCount("contains_ignore_case", operands, 2, 2); //$NON-NLS-1$
      String text = WithArguments.string("contains_ignore_case", operands, 0); //$NON-NLS-1$
      String part = WithArguments.string("contains_ignore_case", operands, 1); //$NON-NLS-1$
      boolean contains = false;
      for (int i = 0; i <= text.length() - part.length(); i++)
      {
        if (text.regionMatches(true, i, part, 0, part.length()))
        {
          contains = true;
          break;
        }
      }

      return Boolean.toString(contains);
    }
  };

  /**
   * Tests whether the first string starts with the second string, with case-sensitive matching.
   *
   * @since 3.31
   */
  public static final StringConverter STARTS_WITH = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      WithArguments.requireCount("starts_with", operands, 2, 2); //$NON-NLS-1$
      return Boolean.toString(WithArguments.string("starts_with", operands, 0) //$NON-NLS-1$
          .startsWith(WithArguments.string("starts_with", operands, 1))); //$NON-NLS-1$
    }
  };

  /**
   * Tests whether the first string ends with the second string, with case-sensitive matching.
   *
   * @since 3.31
   */
  public static final StringConverter ENDS_WITH = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      WithArguments.requireCount("ends_with", operands, 2, 2); //$NON-NLS-1$
      return Boolean.toString(WithArguments.string("ends_with", operands, 0) //$NON-NLS-1$
          .endsWith(WithArguments.string("ends_with", operands, 1))); //$NON-NLS-1$
    }
  };

  /**
   * Returns true when all two or more Boolean operands are true. Every nested operand is evaluated before this
   * converter is invoked, so this operation does not short-circuit.
   *
   * @since 3.31
   */
  public static final StringConverter AND = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      boolean[] values = WithArguments.booleans("and", operands, 2); //$NON-NLS-1$
      boolean result = true;
      for (boolean value : values)
      {
        result &= value;
      }

      return Boolean.toString(result);
    }
  };

  /**
   * Returns true when at least one of the two or more Boolean operands is true. Every nested operand is evaluated
   * before this converter is invoked, so this operation does not short-circuit.
   *
   * @since 3.31
   */
  public static final StringConverter OR = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      boolean[] values = WithArguments.booleans("or", operands, 2); //$NON-NLS-1$
      boolean result = false;
      for (boolean value : values)
      {
        result |= value;
      }

      return Boolean.toString(result);
    }
  };

  /**
   * Returns true when an odd number of the two or more Boolean operands are true.
   *
   * @since 3.31
   */
  public static final StringConverter XOR = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      boolean[] values = WithArguments.booleans("xor", operands, 2); //$NON-NLS-1$
      boolean result = false;
      for (boolean value : values)
      {
        result ^= value;
      }

      return Boolean.toString(result);
    }
  };

  /**
   * Negates one Boolean operand.
   *
   * @since 3.31
   */
  public static final StringConverter NOT = new WithArguments()
  {
    @Override
    public String applyArguments(String[] operands)
    {
      WithArguments.requireCount("not", operands, 1, 1); //$NON-NLS-1$
      boolean[] values = WithArguments.booleans("not", operands, 1); //$NON-NLS-1$
      return Boolean.toString(!values[0]);
    }
  };

  public static final StringConverter CAP = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : StringUtil.cap(value.toString());
    }
  };

  public static final StringConverter CAP_ALL = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : StringUtil.capAll(value.toString());
    }
  };

  public static final StringConverter UNCAP = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : StringUtil.uncap(value.toString());
    }
  };

  public static final StringConverter UNCAP_ALL = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : StringUtil.uncapAll(value.toString());
    }
  };

  public static final StringConverter ESCAPE = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : StringUtil.escape(value.toString());
    }
  };

  public static final StringConverter UNESCAPE = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : StringUtil.unescape(value.toString());
    }
  };

  /**
   * @since 3.26
   */
  public static final StringConverter STRIP_HTML = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      return value == null ? null : StringUtil.stripHTML(value.toString());
    }
  };

  public static final StringConverter NET4J_USER_PATH = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      File path = OMPlatform.INSTANCE.getUserFolder();

      String suffix = StringUtil.safe(value);
      if (suffix.length() != 0)
      {
        path = new File(path, suffix);
      }

      return path.getAbsolutePath();
    }
  };

  public static final StringConverter NET4J_STATE_PATH = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      File path = OMPlatform.INSTANCE.getStateFolder();

      String suffix = StringUtil.safe(value);
      if (suffix.length() != 0)
      {
        path = new File(path, suffix);
      }

      return path.getAbsolutePath();
    }
  };

  public static final StringConverter NET4J_CONFIG_PATH = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      File path = OMPlatform.INSTANCE.getConfigFolder();

      String suffix = StringUtil.safe(value);
      if (suffix.length() != 0)
      {
        path = new File(path, suffix);
      }

      return path.getAbsolutePath();
    }
  };

  /**
   * Looks up an OMPlatform property by name. With one argument, it returns the property value or {@code null} if absent.
   * The argument may also be {@code propertyName,defaultValue}; the first comma separates the property name and the
   * complete remaining text is the default. That default is returned only when the property is absent, not when its
   * value is empty. For example, {@code $$$system_property(transport,tcp)$$$} returns {@code tcp} when
   * {@code transport} is undefined.
   *
   * @since 3.31
   */
  public static final StringConverter SYSTEM_PROPERTY = new WithArguments()
  {
    @Override
    public String applyArguments(String[] arguments)
    {
      if (arguments == null)
      {
        return null;
      }

      WithArguments.requireCount("system_property", arguments, 1, Integer.MAX_VALUE); //$NON-NLS-1$
      String propertyName = WithArguments.string("system_property", arguments, 0); //$NON-NLS-1$
      String propertyValue = OMPlatform.INSTANCE.getProperty(propertyName);
      return propertyValue != null || arguments.length == 1 ? propertyValue : WithArguments.join(arguments, 1);
    }
  };

  public static final StringConverter PROPERTY_URI = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      if (value == null)
      {
        return null;
      }

      String str = value.toString();
      if (str == null)
      {
        return null;
      }

      URI uri = URI.create(str);

      String key = uri.getFragment();
      if (key == null)
      {
        return null;
      }

      try (InputStream stream = uri.toURL().openStream())
      {
        Properties properties = new Properties();
        properties.load(stream);
        return properties.getProperty(key);
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }
  };

  @Override
  public String apply(Object value);

  public default StringConverter compose(StringConverter before)
  {
    Objects.requireNonNull(before);
    return (value) -> apply(before.apply(value));
  }

  public default StringConverter andThen(StringConverter after)
  {
    Objects.requireNonNull(after);
    return (value) -> after.apply(apply(value));
  }

  /**
   * @author Eike Stepper
   */
  public static final class MetaFactory extends org.eclipse.net4j.util.factory.MetaFactory
  {
    private static final String PG = StringConverter.PRODUCT_GROUP;

    private static final IFactory[] CHILDREN = { //
        new SingletonFactory(PG, "identity", IDENTITY), //
        new SingletonFactory(PG, "chars", CHARS), //
        new SingletonFactory(PG, "safe", SAFE), //
        new SingletonFactory(PG, "hex", HEX), //
        new SingletonFactory(PG, "base64", BASE64), //
        new SingletonFactory(PG, "upper", UPPER), //
        new SingletonFactory(PG, "lower", LOWER), //
        new SingletonFactory(PG, "equals", EQUALS), //
        new SingletonFactory(PG, "equals_ignore_case", EQUALS_IGNORE_CASE), //
        new SingletonFactory(PG, "contains", CONTAINS), //
        new SingletonFactory(PG, "contains_ignore_case", CONTAINS_IGNORE_CASE), //
        new SingletonFactory(PG, "starts_with", STARTS_WITH), //
        new SingletonFactory(PG, "ends_with", ENDS_WITH), //
        new SingletonFactory(PG, "and", AND), //
        new SingletonFactory(PG, "or", OR), //
        new SingletonFactory(PG, "xor", XOR), //
        new SingletonFactory(PG, "not", NOT), //
        new SingletonFactory(PG, "cap", CAP), //
        new SingletonFactory(PG, "cap_all", CAP_ALL), //
        new SingletonFactory(PG, "uncap", UNCAP), //
        new SingletonFactory(PG, "uncap_all", UNCAP_ALL), //
        new SingletonFactory(PG, "escape", ESCAPE), //
        new SingletonFactory(PG, "unescape", UNESCAPE), //
        new SingletonFactory(PG, "strip_html", STRIP_HTML), //
        new SingletonFactory(PG, "net4j_user_path", NET4J_USER_PATH), //
        new SingletonFactory(PG, "net4j_state_path", NET4J_STATE_PATH), //
        new SingletonFactory(PG, "net4j_config_path", NET4J_CONFIG_PATH), //
        new SingletonFactory(PG, "system_property", SYSTEM_PROPERTY), //
        new SingletonFactory(PG, "property_uri", PROPERTY_URI), //
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

  /**
   * A StringConverter that accepts parsed argument arrays while retaining direct invocation through
   * {@link #apply(Object)}. StringUtil passes structured arguments to this interface without flattening them. A direct
   * String invocation splits on unescaped commas and resolves {@code $} escapes; a direct String array is passed to
   * {@link #applyArguments(String[])} unchanged.
   * A null direct value is passed as null to {@link #applyArguments(String[])}.
   *
   * @author Eike Stepper
   * @since 3.31
   */
  @FunctionalInterface
  public interface WithArguments extends StringConverter
  {
    /**
     * Applies this converter to already separated arguments.
     *
     * @param arguments the operands, with nested expressions already evaluated
     * @return the converted result
     */
    public String applyArguments(String[] arguments);

    @Override
    public default String apply(Object value)
    {
      if (value instanceof String[])
      {
        return applyArguments((String[])value);
      }

      if (value == null)
      {
        return applyArguments(null);
      }

      return applyArguments(splitArguments(value.toString()));
    }

    private static void requireCount(String name, String[] arguments, int minimum, int maximum)
    {
      int count = arguments == null ? 0 : arguments.length;
      if (count < minimum || count > maximum)
      {
        String expected = minimum == maximum ? "exactly " + minimum : maximum == Integer.MAX_VALUE ? "at least " + minimum : minimum + " to " + maximum; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        throw new IllegalArgumentException("Converter " + name + " expects " + expected + " argument(s), not " + count); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    private static String string(String name, String[] arguments, int index)
    {
      String argument = arguments[index];
      if (argument == null)
      {
        throw new IllegalArgumentException("Converter " + name + " does not accept null arguments"); //$NON-NLS-1$
      }

      return argument;
    }

    private static boolean[] booleans(String name, String[] arguments, int minimum)
    {
      requireCount(name, arguments, minimum, Integer.MAX_VALUE);
      boolean[] values = new boolean[arguments.length];

      for (int i = 0; i < arguments.length; i++)
      {
        String value = string(name, arguments, i).trim();
        if ("true".equalsIgnoreCase(value)) //$NON-NLS-1$
        {
          values[i] = true;
        }
        else if ("false".equalsIgnoreCase(value)) //$NON-NLS-1$
        {
          values[i] = false;
        }
        else
        {
          throw new IllegalArgumentException("Converter " + name + " expects true or false, not: " + value); //$NON-NLS-1$
        }
      }

      return values;
    }

    private static String join(String[] values, int start)
    {
      StringBuilder builder = new StringBuilder();

      for (int i = start; i < values.length; i++)
      {
        if (i > start)
        {
          builder.append(',');
        }

        builder.append(values[i]);
      }

      return builder.toString();
    }

    private static String[] splitArguments(String str)
    {
      List<String> arguments = new ArrayList<>();
      StringBuilder argument = new StringBuilder();

      for (int i = 0; i < str.length(); i++)
      {
        char c = str.charAt(i);
        if (c == '$' && i + 1 < str.length())
        {
          argument.append(str.charAt(++i));
        }
        else if (c == ',')
        {
          arguments.add(argument.toString());
          argument.setLength(0);
        }
        else
        {
          argument.append(c);
        }
      }

      arguments.add(argument.toString());
      return arguments.toArray(new String[arguments.size()]);
    }
  }
}
