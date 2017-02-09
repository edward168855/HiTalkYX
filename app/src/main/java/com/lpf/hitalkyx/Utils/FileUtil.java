package com.lpf.hitalkyx.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * Created by sky90 on 2017/2/9.
 */
public class FileUtil {
    public FileUtil() {
    }

    public static boolean checkSDcard() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static boolean copyFile(File srcFile, File destFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            in = fis.getChannel();
            out = fos.getChannel();
            in.transferTo(0L, in.size(), out);
            return true;
        } catch (Exception var15) {
            ;
        } finally {
            try {
                fis.close();
                in.close();
                fos.close();
                out.close();
            } catch (Exception var14) {
                ;
            }

        }

        return false;
    }

    public static boolean delFile(File f, boolean self) {
        boolean flag = false;

        try {
            if(!f.exists()) {
                return true;
            }

            if(f.isDirectory()) {
                File[] e = f.listFiles();

                for(int i = 0; i < e.length; ++i) {
                    flag = delFile(e[i], self);
                    if(!flag) {
                        break;
                    }
                }

                if(self) {
                    f.delete();
                }
            } else {
                flag = f.delete();
            }
        } catch (Exception var5) {
            flag = false;
        }

        return flag;
    }

    public static boolean delFile(String dir, boolean self) {
        File f = new File(dir);
        return delFile(f, self);
    }

    public static long getAvailableStore(String filePath) {
        StatFs statFs = new StatFs(filePath);
        long blocSize = (long)statFs.getBlockSize();
        long availaBlock = (long)statFs.getAvailableBlocks();
        long availableSpare = availaBlock * blocSize;
        return availableSpare;
    }

    public static void saveFileCache(byte[] fileData, String folderPath, String fileName) {
        File folder = new File(folderPath);
        folder.mkdirs();
        File file = new File(folderPath, fileName);
        ByteArrayInputStream is = new ByteArrayInputStream(fileData);
        FileOutputStream os = null;
        if(!file.exists()) {
            try {
                file.createNewFile();
                os = new FileOutputStream(file);
                byte[] e = new byte[1024];
                boolean len = false;

                int len1;
                while(-1 != (len1 = is.read(e))) {
                    os.write(e, 0, len1);
                }

                os.flush();
            } catch (Exception var12) {
                throw new RuntimeException(var12);
            } finally {
                closeIO(new Closeable[]{is, os});
            }
        }

    }

    public static boolean bitmapToFile(Bitmap bitmap, String filePath) {
        boolean isSuccess = false;
        if(bitmap == null) {
            return isSuccess;
        } else {
            File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            if(!file.exists()) {
                file.mkdirs();
            }

            BufferedOutputStream out = null;

            try {
                out = new BufferedOutputStream(new FileOutputStream(filePath), 8192);
                isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (FileNotFoundException var9) {
                var9.printStackTrace();
            } finally {
                closeIO(new Closeable[]{out});
            }

            return isSuccess;
        }
    }

    public static void closeIO(Closeable... closeables) {
        if(closeables != null && closeables.length > 0) {
            Closeable[] var4 = closeables;
            int var3 = closeables.length;

            for(int var2 = 0; var2 < var3; ++var2) {
                Closeable cb = var4[var2];

                try {
                    if(cb != null) {
                        cb.close();
                    }
                } catch (IOException var6) {
                    throw new RuntimeException(var6);
                }
            }

        }
    }

    public static String readFile(String filePath) {
        FileInputStream is = null;

        try {
            is = new FileInputStream(filePath);
        } catch (Exception var3) {
            throw new RuntimeException("readFile---->" + filePath + " not found");
        }

        return inputStream2String(is);
    }

    public static String inputStream2String(InputStream is) {
        if(is == null) {
            return null;
        } else {
            StringBuilder resultSb = null;

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                resultSb = new StringBuilder();

                String len;
                while((len = br.readLine()) != null) {
                    resultSb.append(len);
                }
            } catch (Exception var7) {
                ;
            } finally {
                closeIO(new Closeable[]{is});
            }

            return resultSb == null?null:resultSb.toString();
        }
    }

    public static String getFileMD5(File file) throws FileNotFoundException {
        if(!file.isFile()) {
            return null;
        } else {
            String value = null;
            FileInputStream in = null;
            byte[] buffer = new byte[1024];
            boolean len = true;

            try {
                in = new FileInputStream(file);
                MessageDigest e = MessageDigest.getInstance("MD5");

                while(in.read(buffer, 0, 1024) != -1) {
                    e.update(buffer);
                }

                BigInteger bi = new BigInteger(1, e.digest());
                value = bi.toString(16);
            } catch (Exception var15) {
                value = null;
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException var14) {
                        var14.printStackTrace();
                    }
                }

            }

            return value;
        }
    }
}
