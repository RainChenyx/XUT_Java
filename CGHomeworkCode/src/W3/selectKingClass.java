package W3;

public class selectKingClass
{
    public int n,k,m;
    public selectKingClass(int n,int k,int m)
    {
        this.n=n;
        this.k=k-1;
        this.m=m;
    }
    public void king()
    {
        int index = k;
        int count=0,loop=0;
        int[] monkeys = new int[n];
        for (int i = 0; i < n; i++)
        {
            monkeys[i] = i+1;
        }
        while (count != n-1)
        {
            if(monkeys[index] != 0)
            {
                loop++;
                if (loop == m) {
                    System.out.printf("%d ", monkeys[index]);
                    monkeys[index] = 0;
                    loop = 0;
                    count++;
                }
            }
            index= (index+1)%n;
        }
        for (int i = 0; i < n; i++) {
            if (monkeys[i] != 0) {
                System.out.println();
                System.out.println(monkeys[i]);
            }
        }
    }
}
