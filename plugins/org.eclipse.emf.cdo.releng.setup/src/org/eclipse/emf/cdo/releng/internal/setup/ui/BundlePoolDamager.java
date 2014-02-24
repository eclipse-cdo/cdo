/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * @author Eike Stepper
 */
public class BundlePoolDamager
{
  public static void main(String[] args) throws Exception
  {
    Random random = new Random(System.currentTimeMillis());

    File[] files = new File("C:/Users/Stepper/.p2/pool2/plugins").listFiles();
    for (int i = 0; i < 10; i++)
    {
      File file = files[random.nextInt(files.length)];

      if (file.getName().endsWith(".jar"))
      {
        System.out.println("Damaging " + file);

        int length = (int)file.length();
        byte[] buf = new byte[length];

        FileInputStream in = new FileInputStream(file);

        try
        {
          in.read(buf);
        }
        finally
        {
          IOUtil.close(in);
        }

        FileOutputStream out = new FileOutputStream(file);

        try
        {
          out.write(buf, 0, length / 2);
        }
        finally
        {
          IOUtil.close(out);
        }
      }
    }
  }
}
