package W7;

import java.io.*;

public class print {
    public static void main(String[] args) {
        int count = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader("student.txt"))){
            String line;
            while((line = reader.readLine()) != null)
            {
                count++;
                System.out.printf("%02d: %s\n",count,line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
