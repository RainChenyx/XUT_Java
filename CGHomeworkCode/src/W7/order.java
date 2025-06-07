package W7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class order {
    // 读取文件
    public static void main(String[] args) {
        int i;
        String[] str = new String[20];
        try(BufferedReader reader = new BufferedReader(new FileReader("in.txt"))){
            String line;
            while((line = reader.readLine()) != null)
            {
                str = line.split("\\s+");
                for(i=0; i<20;i++) {
                    for (int j = 0; j < 20; j++)
                    {
                        if (Integer.parseInt(str[j]) > Integer.parseInt(str[i]))
                        {
                            String temp = str[i];
                            str[i] = str[j];
                            str[j] = temp;
                        }
                    }
                }
            }
            for(i=0; i<20;i++)
                System.out.printf("%s ", str[i]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
