package W2;

import java.util.Scanner;

public class score{
    public static void main(String[] args)
    {
        int number,fail=0,pass=0,middle=0,good=0,excellent=0,i;
        Scanner scanner = new Scanner(System.in);
        number = scanner.nextInt();
        double[] score = new double[number];
        for(i = 0; i < number; i++)
        {
            score[i] = scanner.nextDouble();
        }
        double max = getMax(score);
        double avg = getAvg(score);
        for(i = 0 ; i < number; i++)
        {
            if(score[i] < 60)
                fail++;
            else if(60 <= score[i] && score[i] < 70)
                pass++;
            else if(70 <= score[i] && score[i] < 80)
                middle++;
            else if(80 <= score[i] && score[i] < 90)
                good++;
            else if(90 <= score[i] && score[i] <= 100)
                excellent++;
        }
        Print(max, avg, fail, pass, middle, good, excellent);
    }
    public static double getMax(double[] score){
        double max = score[0];
        for(int i = 1; i < score.length; i++)
        {
            if(score[i] > max)
            {
                max = score[i];
            }
        }
        return max;
    }
    public static double getAvg(double[] score)
    {
        double sum = 0;
        for(int i = 0; i < score.length; i++)
        {
            sum += score[i];
        }
        return sum / score.length;
    }
    public static void Print(double max,double avg,int fail,int pass,int middle,int good,int excellent)
    {
        String formattedAvg = String.format("%.2f", avg);
        System.out.println("max:" + max);
        System.out.println("avg:" + formattedAvg);
        System.out.println("fail:" + fail);
        System.out.println("pass:" + pass);
        System.out.println("middle:" + middle);
        System.out.println("good:" + good);
        System.out.println("excellent:" + excellent);
    }
}
