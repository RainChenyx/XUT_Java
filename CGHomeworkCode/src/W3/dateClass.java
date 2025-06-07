package W3;

public class dateClass {
    public int year, month, day;
    public dateClass(int year, int month, int day) {
        if (year > 1)
            this.year = year;
        else
            this.year = 2000;
        if(month>=1 && month<=12)
            this.month = month;
        else
            this.month = 1;
        this.day = detailDay(this.year,this.month,day);
    }
    public int detailDay(int year,int month,int day)
    {
        int daymax = 0;
        if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
            daymax = 31;
        if(month == 4 || month == 6 || month == 9 || month == 11)
            daymax = 30;
        if(month == 2)
            if((year%100 == 0 && year%400 == 0) || (year%100 != 0 && year%4 == 0))
            {
                daymax = 29;
            }
        if (day <= daymax)
            return day;
        else
            return 1;
    }
    public void Print() {
        System.out.println(year + "/" + month + "/" + day);
    }
}
