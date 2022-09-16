package test;

import java.io.FileInputStream;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) throws Exception
    {
        InputStream is = null;
        try
        {
            is = new FileInputStream("D:\\Programming\\Java\\IrisePlayer\\test.txt");
            System.out.println("Char : " + (char) is.read());
            is.mark(0);//设置流位置重新为0
            System.out.println("Char : " + (char) is.read());
            if(is.markSupported())
            {
                is.reset();
                System.out.println("Char : " + (char) is.read());
            }
        }
        catch(Exception e)
        {}
        finally
        {
            if(is != null) is.close();
        }
    }
}
