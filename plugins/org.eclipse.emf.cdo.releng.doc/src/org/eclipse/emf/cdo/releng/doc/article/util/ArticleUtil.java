/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class ArticleUtil
{
  private ArticleUtil()
  {
  }

  public static boolean isDocumented(Doc doc)
  {
    Tag[] tags = doc.inlineTags();
    return tags != null && tags.length != 0;
  }

  public static boolean isTagged(Doc doc, String tag)
  {
    Tag[] tags = doc.tags(tag);
    return tags != null && tags.length != 0;
  }

  public static boolean isTagged(RootDoc root, Doc doc, String tag)
  {
    boolean snippet = isTagged(doc, tag);
    if (snippet)
    {
      return true;
    }

    if (doc instanceof ProgramElementDoc)
    {
      ProgramElementDoc programElementDoc = (ProgramElementDoc)doc;
      ClassDoc containingClass = programElementDoc.containingClass();
      if (containingClass != null)
      {
        snippet = isTagged(root, containingClass, tag);
        if (snippet)
        {
          return true;
        }
      }

      PackageDoc containingPackage = programElementDoc.containingPackage();
      if (containingPackage != null)
      {
        snippet = isTagged(root, containingPackage, tag);
        if (snippet)
        {
          return true;
        }
      }
    }
    else if (doc instanceof PackageDoc)
    {
      PackageDoc packageDoc = (PackageDoc)doc;
      PackageDoc parentPackage = getParentPackage(root, packageDoc);
      if (parentPackage != null)
      {
        snippet = isTagged(root, parentPackage, tag);
        if (snippet)
        {
          return true;
        }
      }
    }

    return false;
  }

  public static boolean isIgnore(Doc doc)
  {
    return isTagged(doc, "@ignore");
  }

  public static boolean isSnippet(RootDoc root, Doc doc)
  {
    return isTagged(root, doc, "@snippet");
  }

  public static boolean isFactory(MethodDoc doc)
  {
    return isTagged(doc, "@factory");
  }

  public static String getSimplePackageName(PackageDoc packageDoc)
  {
    String name = packageDoc.name();
    int lastDot = name.lastIndexOf('.');
    if (lastDot != -1)
    {
      return name.substring(lastDot + 1);
    }

    return name;
  }

  public static PackageDoc getParentPackage(RootDoc root, PackageDoc packageDoc)
  {
    String name = packageDoc.name();
    int lastDot = name.lastIndexOf('.');
    if (lastDot != -1)
    {
      return root.packageNamed(name.substring(0, lastDot));
    }

    return null;
  }

  public static void close(Closeable closeable)
  {
    if (closeable != null)
    {
      try
      {
        closeable.close();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
  }

  public static String readFile(File file)
  {
    Reader reader = null;
    char[] buffer = new char[(int)file.length()];

    try
    {
      reader = new FileReader(file);
      reader.read(buffer);
      return new String(buffer);
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
    finally
    {
      close(reader);
    }
  }

  public static boolean containsFile(File folder, File file)
  {
    if (!folder.isDirectory())
    {
      return false;
    }

    File parent = file.getParentFile();
    if (parent == null)
    {
      return false;
    }

    if (parent.equals(folder))
    {
      return true;
    }

    return containsFile(folder, parent);
  }

  public static File canonify(File file)
  {
    try
    {
      return file.getCanonicalFile();
    }
    catch (IOException ex)
    {
      throw new ArticleException(ex);
    }
  }

  public static String createLink(File source, File target)
  {
    List<String> sourceSegments = getSegments(source);
    List<String> targetSegments = getSegments(target);

    int minSize = Math.min(sourceSegments.size(), targetSegments.size());
    for (int i = 0; i < minSize; i++)
    {
      if (sourceSegments.get(0).equals(targetSegments.get(0)))
      {
        sourceSegments.remove(0);
        targetSegments.remove(0);
      }
      else
      {
        break;
      }
    }

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < sourceSegments.size() - 1; i++)
    {
      builder.append("../");
    }

    boolean first = true;
    for (String segment : targetSegments)
    {
      if (first)
      {
        first = false;
      }
      else
      {
        builder.append("/");
      }

      builder.append(segment);
    }

    return builder.toString();
  }

  private static List<String> getSegments(File file)
  {
    List<String> result = new ArrayList<String>();
    getSegments(file, result);
    return result;
  }

  private static void getSegments(File file, List<String> result)
  {
    File parent = file.getParentFile();
    if (parent != null)
    {
      getSegments(parent, result);
    }

    result.add(file.getName());
  }

  public static String makeConsoleLink(Doc doc)
  {
    SourcePosition position = doc.position();
    if (position == null)
    {
      return doc.name();
    }

    return makeConsoleLink(doc, position);
  }

  public static String makeConsoleLink(Doc doc, SourcePosition position)
  {
    if (doc instanceof ClassDoc)
    {
      return makeConsoleLink((ClassDoc)doc, "class", position);
    }

    if (doc instanceof MethodDoc)
    {
      MethodDoc methodDoc = (MethodDoc)doc;
      return makeConsoleLink(methodDoc.containingClass(), methodDoc.name(), position);
    }

    return makeConsoleLink("", position);
  }

  public static String makeConsoleLink(ClassDoc classDoc, String methodName, SourcePosition position)
  {
    String typeName = classDoc.containingPackage().name() + "." + classDoc.typeName().replace('.', '$');
    return makeConsoleLink(typeName + "." + methodName, position);
  }

  public static String makeConsoleLink(String prefix, SourcePosition position)
  {
    String result = position.file().getName() + ":" + position.line();
    if (prefix != null && prefix.length() != 0)
    {
      result = prefix + "(" + result + ")";
    }

    return result;
  }
}
