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
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

/**
 * Converts any kind of {@link Object objects} to strings.
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

  public static final StringConverter SYSTEM_PROPERTY = new StringConverter()
  {
    @Override
    public String apply(Object value)
    {
      if (value == null)
      {
        return null;
      }

      String str = value.toString();
      return OMPlatform.INSTANCE.getProperty(str);
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
        new SingletonFactory(PG, "upper", UPPER), //
        new SingletonFactory(PG, "lower", LOWER), //
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
}
