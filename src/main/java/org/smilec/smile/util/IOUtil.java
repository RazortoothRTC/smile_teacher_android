/**
Copyright 2012-2013 SMILE Consortium, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
**/
package org.smilec.smile.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smilec.smile.bu.Constants;

import android.util.Log;

public class IOUtil {

    private IOUtil() {
        // Empty
    }

    public static final void silentFlush(Flushable f) {
        try {
            if (f != null) {
                f.flush();
            }
        } catch (IOException e) {
            // Empty
        }
    }

    public static final void silentClose(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            // Empty
        }
    }

    public static final void silentClose(InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {}
    }

    public static final void silentClose(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {}
    }

    public static void copy(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {}
    }

    public static byte[] loadBytesFromResource(String resource) throws IOException {
        InputStream is = IOUtil.class.getResourceAsStream(resource);
        return loadBytes(is);
    }

    public static byte[] loadBytes(InputStream is) throws IOException {

        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, len);
            }
            buf = bos.toByteArray();
        }
        return buf;

    }

    public static byte[] loadBytes(File file) throws IOException {

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("File '" + file.getAbsolutePath() + "' is too large");
        }

        FileInputStream fis = new FileInputStream(file);
        try {
            return loadBytes(fis);
        } finally {
            silentClose(fis);
        }

    }

    public static void saveBytes(File file, byte[] content) throws IOException {

        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            out.write(content);
        } finally {
            silentFlush(out);
            silentClose(out);
        }

    }

    public static String loadContent(InputStream in, String encoding) throws IOException {
        try {
            byte[] bytes = loadBytes(in);
            return new String(bytes, encoding);
        } catch (OutOfMemoryError e) {
            Log.e(Constants.LOG_CATEGORY, "Error: ", e);
            return "";
        }
    }

}
