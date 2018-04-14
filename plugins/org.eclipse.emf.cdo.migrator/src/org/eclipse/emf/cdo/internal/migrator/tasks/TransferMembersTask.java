package org.eclipse.emf.cdo.internal.migrator.tasks;

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class TransferMembersTask extends ExpandTemplateTask
{
  private File sourceClass;

  private String importsPlaceholder = "IMPORTS";

  private String fieldsPlaceholder = "FIELDS";

  private String methodsPlaceholder = "METHODS";

  private boolean transferImports = true;

  private final List<TransferField> transferFields = new ArrayList<TransferField>();

  private final List<TransferMethod> transferMethods = new ArrayList<TransferMethod>();

  public void setSourceClass(File sourceClass)
  {
    this.sourceClass = sourceClass;
  }

  public void setImportsPlaceholder(String importsPlaceholder)
  {
    this.importsPlaceholder = importsPlaceholder;
  }

  public void setFieldsPlaceholder(String fieldsPlaceholder)
  {
    this.fieldsPlaceholder = fieldsPlaceholder;
  }

  public void setMethodsPlaceholder(String methodsPlaceholder)
  {
    this.methodsPlaceholder = methodsPlaceholder;
  }

  public void setTransferImports(boolean transferImports)
  {
    this.transferImports = transferImports;
  }

  public TransferField createTransferField()
  {
    TransferField transferField = new TransferField();
    transferFields.add(transferField);
    return transferField;
  }

  public TransferMethod createTransferMethod()
  {
    TransferMethod transferMethod = new TransferMethod();
    transferMethods.add(transferMethod);
    return transferMethod;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    super.checkAttributes();
    assertTrue("'sourceClass' must be specified.", sourceClass != null);
    assertTrue("'sourceClass' must be point to an existing file.", sourceClass.isFile());
    assertTrue("'importsPlaceholder' must be specified.", importsPlaceholder != null && importsPlaceholder.length() != 0);
    assertTrue("'fieldsPlaceholder' must be specified.", fieldsPlaceholder != null && fieldsPlaceholder.length() != 0);
    assertTrue("'methodsPlaceholder' must be specified.", methodsPlaceholder != null && methodsPlaceholder.length() != 0);

    checkTransferMembers(transferFields);
    checkTransferMembers(transferMethods);
  }

  private void checkTransferMembers(List<? extends TransferMember> transferMembers)
  {
    for (TransferMember transferMember : transferMembers)
    {
      String pattern = transferMember.getPattern();
      assertTrue("'pattern' must be specified.", pattern != null && pattern.length() != 0);
    }
  }

  @Override
  protected String generate(String content, Map<String, String> properties) throws Exception
  {
    Collector collector = new Collector(sourceClass);

    StringBuilder imports = new StringBuilder();
    if (transferImports)
    {
      for (String name : collector.imports.keySet())
      {
        imports.append("import ");
        imports.append(name);
        imports.append(";");
        imports.append(NL);
      }
    }

    properties.put(importsPlaceholder, imports.toString());
    properties.put(fieldsPlaceholder, formatMembers(collector.fields, transferFields));
    properties.put(methodsPlaceholder, formatMembers(collector.methods, transferMethods));

    return super.generate(content, properties);
  }

  private String formatMembers(Map<String, List<String>> members, List<? extends TransferMember> transferMembers)
  {
    if (transferMembers.isEmpty())
    {
      return "";
    }

    List<Pattern> patterns = new ArrayList<Pattern>();
    for (TransferMember transferMember : transferMembers)
    {
      patterns.add(Pattern.compile(transferMember.getPattern()));
    }

    StringBuilder builder = new StringBuilder();
    boolean first = true;

    for (Map.Entry<String, List<String>> entry : members.entrySet())
    {
      String name = entry.getKey();
      for (Pattern pattern : patterns)
      {
        if (pattern.matcher(name).matches())
        {
          List<String> texts = entry.getValue();
          for (String text : texts)
          {
            if (first)
            {
              first = false;
            }
            else
            {
              builder.append(NL);
              builder.append(NL);
            }

            builder.append(text);
          }

          break;
        }
      }
    }

    return builder.toString();
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class TransferMember
  {
    private String pattern;

    public String getPattern()
    {
      return pattern;
    }

    public void setPattern(String pattern)
    {
      this.pattern = pattern;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TransferField extends TransferMember
  {
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("TransferField[");
      builder.append(getPattern());
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TransferMethod extends TransferMember
  {
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("TransferMethod[");
      builder.append(getPattern());
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Collector
  {
    private static final Pattern IMPORT_PATTERN = Pattern.compile("[\\\\t ]*import\\s+([^;]+);");

    private static final Pattern FIELD_PATTERN = Pattern
        .compile("[\\\\t ]*((public|private|protected|static|final|transient|volatile)+\\s)+[\\$_\\w\\<\\>\\[\\]]*\\s+([\\$_\\w]+)\\s*(;|=[^;]*;)");

    private static final Pattern METHOD_PATTERN = Pattern.compile(
        "[\\t ]*((public|private|protected|static|final|native|synchronized|abstract|strictfp)+\\s)+[\\$_\\w\\<\\>\\[\\]]*\\s+(([\\$_\\w]+)\\([^\\)]*\\)?)[^\\{;]*(.)");

    public final Map<String, List<String>> imports = new LinkedHashMap<String, List<String>>();

    public final Map<String, List<String>> fields = new LinkedHashMap<String, List<String>>();

    public final Map<String, List<String>> methods = new LinkedHashMap<String, List<String>>();

    public Collector(File file) throws IOException
    {
      String content = readTextFile(file);
      collect(content, IMPORT_PATTERN, 1, imports);
      collect(content, FIELD_PATTERN, 3, fields);
      collect(content, METHOD_PATTERN, 4, methods);
    }

    private static void collect(String content, Pattern pattern, int group, Map<String, List<String>> result)
    {
      Matcher matcher = pattern.matcher(content);
      while (matcher.find())
      {
        String text = matcher.group(0);
        String name = matcher.group(group);

        if (pattern == METHOD_PATTERN)
        {
          String braceOrSemicolon = matcher.group(5);
          if (braceOrSemicolon.equals("{"))
          {
            int start = matcher.end();
            int end = content.length();
            int nesting = 1;

            for (int i = start; i < end; i++)
            {
              char c = content.charAt(i);
              if (c == '{')
              {
                ++nesting;
              }
              else if (c == '}')
              {
                if (--nesting == 0)
                {
                  String body = content.substring(start, i + 1);
                  text += body;
                  break;
                }
              }
            }
          }
        }

        List<String> texts = result.get(name);
        if (texts == null)
        {
          texts = new ArrayList<String>();
          result.put(name, texts);
        }

        texts.add(text);
      }
    }
  }
}
