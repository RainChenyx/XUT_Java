package W7;

import java.io.*;

public class notes {
    public static void main(String[] args)
    {
        try(BufferedReader reader = new BufferedReader(new FileReader("filein.c")))
        {
            String line;
            String[] str;
            StringBuilder all_char= new StringBuilder();
            int sum = 0;
            int comment = 0;
            int percentage;
            int i;
            boolean in_comment = false;
            while((line = reader.readLine()) != null)
            {
                line = line.trim();
                str = line.split("\\s+");
                for(i = 0; i < str.length; i++)
                {
                    all_char.append(str[i]);
                }
            }
            for(i = 0; i < all_char.length(); i++)
            {
                if(all_char.charAt(i) == '/' && all_char.charAt(i + 1) == '*')
                {
                    in_comment = true;
                    i++;
                    sum = sum + 2;
                }
                else if(all_char.charAt(i) == '*' && all_char.charAt(i + 1) == '/')
                {
                    in_comment = false;
                    i++;
                    sum = sum + 2;
                }
                else if(in_comment)
               {
                   comment++;
                   sum++;
               }
                else
                {
                    sum++;
                }
            }
            double result = (double)comment / sum;
            percentage = (int)(result * 100);
            System.out.println(percentage + "%");
        }
        catch (IOException e)
        {
            System.out.println("Error open the file");
        }
    }
}
