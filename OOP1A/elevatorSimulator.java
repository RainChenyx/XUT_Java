// 要求：利用多线程模拟电梯运行，并随机分配房间以及初始位置和目标楼层以及从初始位置到目标楼层的逗留时间，模拟 10 部电梯的运行，可选择显示任 1 部电梯的状态（等待队列，轿厢中的中人员信息）
// 功能：1. 模拟电梯运行（利用多线程） 2. 查看所有员工信息 3. 查看指定公司员工信息 4. 查看公司信息 5. 显示所有电梯的配置 6. 查看所有员工的逗留时间 7. 显示所有电梯的运行信息 或者 指定电梯的具体信息
// 题目：设计并模拟某大厦的 10 部电梯在上班高峰期间的运行状况：
// 该楼共 30 层，每层 20 个房间，每间房最多 10 人。
// 共 10 部电梯，1 部电梯直通 25 层以上，3 部电梯 10 层以下可达，4 部电梯直达 11~20 层，2 部电梯直达 21~29层,每个电梯满载20人。
// 每天早上 8 点 30 到 9 点 30 为上班高峰时段；
// 全楼共有 10 家企业，3000 人，请为他们随机分配房间以及初始位置和目标楼层以及逗留时间。
// 模拟 10 部电梯的运行，可选择显示任 1 部电梯的状态（等待队列，轿厢中的中人员信息）。
// 规定：每个电梯满载20人，开门时间为1秒，地面层为1楼，模拟时间规定100毫秒为作为现实的1秒，

package OOP1A;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class elevatorSimulator {
    public static void main(String[] args) {
        int number_company;  // 随机生成所属公司
        int[][] rooms = new int[30][20];  //  记录每层每间房间的人
        int[] numbers = new int[10];  // 记录每个公司的员工数
        Staff[] staff = new Staff[3000];  // 记录所有员工数据

        for (int i = 0; i < 3000; i++) {
            number_company = (int) (Math.random() * 10);
            int room = assignRoom(number_company + 1, rooms) + 1;  // 因为公司是随机的，所以对应的房间、目标楼层也是随机的
            int position = (int) (Math.random() * 30);  // 随机初始位置
            staff[i] = new Staff("ID:" + (i + 1), (number_company + 1), position, room / 100, room);  // 因为公司是随机的，所以对应的房间、目标楼层也是随机的
            numbers[number_company]++;
        }

        Company[] company = new Company[10];
        for (int i = 0; i < 10; i++) {
            company[i] = new Company("Company" + (i + 1), numbers[i]);
        }

        // 初始化电梯可到楼层以及电梯编号
        Elevator[] elevators = new Elevator[10];
        elevators[0] = new Elevator("Elevator 1", 25, 30);
        elevators[1] = new Elevator("Elevator 2", 1, 10);
        elevators[2] = new Elevator("Elevator 3", 1, 10);
        elevators[3] = new Elevator("Elevator 4", 1, 10);
        elevators[4] = new Elevator("Elevator 5", 11, 20);
        elevators[5] = new Elevator("Elevator 6", 11, 20);
        elevators[6] = new Elevator("Elevator 7", 11, 20);
        elevators[7] = new Elevator("Elevator 8", 11, 20);
        elevators[8] = new Elevator("Elevator 9", 21, 29);
        elevators[9] = new Elevator("Elevator 10", 21, 29);

        // 控制台操作界面
        Scanner scanner = new Scanner(System.in);
        int choice;
        boolean run = false;
        System.out.println("--------------------------------欢迎使用电梯模拟系统---------------------------------");
        while (true) {
            menu();
            System.out.print("请输入您的选择：");
            choice= scanner.nextInt();
            if (choice > 0 && choice <= 7) {
                switch (choice)
                {
                    case 1:
                        System.out.println("---------------------------------------------------------------------------------");
                        startSimulator(elevators, staff);
                        run = true;
                        break;
                    case 2:
                        System.out.println("---------------------------------------------------------------------------------");
                        for (int i = 0; i < 3000; i++)
                        {
                            System.out.printf("员工%-10s 所属公司：company %2s 初始位置：%2d 目标楼层：%2d 房间号：%4d\n", staff[i].getName(), staff[i].getCompany(), staff[i].getPosition(), staff[i].getDestination(), staff[i].getRoom());
                        }
                        break;
                    case 3:
                        System.out.println("---------------------------------------------------------------------------------");
                        int company_number;
                        while (true)
                        {
                            System.out.print("请输入您要查看的公司编号：");
                            company_number = scanner.nextInt();
                            if (company_number > 0 && company_number <= 10)
                                break;
                            else
                                System.out.println("输入有误，请重新输入！");
                        }
                        System.out.println("---------------------------------------------------------------------------------");
                        for (int i = 0; i < 3000; i++)
                        {
                            if(staff[i].getCompany() == company_number)
                                System.out.printf("员工%-10s 房间号：%4d\n", staff[i].getName(),staff[i].getRoom());
                        }
                        break;
                    case 4:
                        System.out.println("---------------------------------------------------------------------------------");
                        for (int i = 0; i < 10; i++)
                        {
                            System.out.printf("公司: %-10s 员工数：%4d\n", company[i].getName(), company[i].getNumber());
                        }
                        break;
                    case 5:
                        System.out.println("---------------------------------------------------------------------------------");
                        for (int i = 0; i < 10; i++)
                        {
                            System.out.printf("电梯%-12s 可到达楼层范围：%2d ~ %2d\n", elevators[i].getNumber(), elevators[i].getStart_floor(), elevators[i].getEnd_floor());
                        }
                        break;
                    case 6:
                        System.out.println("---------------------------------------------------------------------------------");
                        if(run)
                        {
                            for (int i = 0; i < 3000; i++) {
                                int minute = staff[i].getStayTime() / 60;
                                int second = staff[i].getStayTime() % 60;
                                System.out.printf("员工%-10s 逗留时间：%2d分%2d秒\n", staff[i].getName(), minute, second);
                            }
                        }
                        else
                            System.out.println("请先运行模拟程序！");
                        break;
                    case 7:
                        System.out.println("---------------------------------------------------------------------------------");
                        System.out.println("程序已退出！");
                        System.exit(0);
                        break;
                }
            }
            else
            {
                System.out.println("请输入正确的选项！");
            }
        }
    }

    static void menu()
    {
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("1. 模拟电梯运行");
        System.out.println("2. 查看所有员工信息");
        System.out.println("3. 查看指定公司员工信息");
        System.out.println("4. 查看公司信息");
        System.out.println("5. 显示所有电梯的配置");
        System.out.println("6. 查看所有员工的逗留时间");
        System.out.println("7. 退出");
    }

    static void startSimulator(Elevator elevators[], Staff staff[])
    {
        int return_number = 0;  //  记录返回1楼的人数
        int return_time;  //  记录返回1楼时间
        int display;
        int[] number_elevators = new int[10];  // 记录电梯中当前人数

        // 开始随机分配电梯，根据目的地随机分配可到达该楼层的电梯
        for (int i = 0; i < 3000; i++) {
            staff[i].setStayTime(0);

            if (staff[i].getPosition() == staff[i].getDestination()) {
                continue;
            }

            boolean enter = false;
            int maxAttempts = 20;
            int attempts = 0;

            if (staff[i].getPosition() != staff[i].getDestination() && staff[i].getPosition() < staff[i].getDestination() || staff[i].getDestination() != 1) {
                while (!enter && attempts < maxAttempts) {
                    int elevator_number = (int)(Math.random() * 10);
                    Elevator elevator = elevators[elevator_number];
                    if ((elevator.getStart_floor() <= staff[i].getDestination() &&
                            staff[i].getDestination() <= elevator.getEnd_floor()) &&
                            (elevator.getStart_floor() <= staff[i].getPosition() &&
                                    staff[i].getPosition() <= elevator.getEnd_floor())) {

                        elevators[elevator_number].addToWaitingQueue(staff[i].getPosition(), i);
                        enter = true;
                    }
                    attempts++;
                }

                if (!enter) {
                    return_time = (return_number / 200 + 1) * staff[i].getPosition();
                    staff[i].setStayTime(return_time);
                    staff[i].setPosition(1);
                    return_number++;
                    if(staff[i].getDestination() == 1) {
                        continue;
                    }

                    while (!enter) {
                        int elevator_number = (int)(Math.random() * 10);
                        if (elevators[elevator_number].getStart_floor() <= staff[i].getDestination() &&
                                staff[i].getDestination() <= elevators[elevator_number].getEnd_floor()) {
                            elevators[elevator_number].addToWaitingQueue(staff[i].getPosition(), i);
                            enter = true;
                        }
                    }
                }
            }

            if ((staff[i].getPosition() != staff[i].getDestination() && staff[i].getPosition() > staff[i].getDestination()) || staff[i].getDestination() == 1) {
                return_time = (return_number / 200 + 1) * staff[i].getPosition();
                staff[i].setStayTime(return_time);
                staff[i].setPosition(1);
                return_number++;

                if(staff[i].getDestination() == 1) {
                    continue;
                }

                while (!enter) {
                    int elevator_number = (int)(Math.random() * 10);
                    if (elevators[elevator_number].getStart_floor() <= staff[i].getDestination() &&
                            staff[i].getDestination() <= elevators[elevator_number].getEnd_floor()) {
                        elevators[elevator_number].addToWaitingQueue(staff[i].getPosition(), i);
                        enter = true;
                    }
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            System.out.printf("电梯%-12s 等待队列：%4d 人\n", elevators[i].getNumber(), elevators[i].getWaitingCount());
        }

        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("请输入您要显示的电梯（输入0则是显示全部,但不具体显示每个轿厢中的中人员信息）：");
            display = scanner.nextInt();  // 选择显示任 1 部电梯的状态（等待队列，轿厢中的中人员信息），0则是全部
            if(display <= 10 && display >= 0)
                break;
            else
            {
                System.out.println("请输入正确的选项！");
            }
        }

        // 使用线程池来并行运行每个电梯
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int time = 0; // 运行时间
        int MAX = 20; // 电梯满载20人
        boolean is_empty = false;
        boolean[] is_return =  new boolean[10];  // 记录电梯是否在回程状态
        for(int i = 0; i < 10; i++)
        {
            is_return[i] = false;
        }
        List<List<Integer>> destinations = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            destinations.add(new ArrayList<>());
        }

        while (!is_empty) {
            try {
                Thread.sleep(100); // 暂停100毫秒，代表现实中的1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final int currentTime = time;
            // 模拟电梯运行，每个电梯的线程任务
            for (int i = 0; i < 10; i++) {
                final int elevatorIndex = i;
                executor.submit(() -> {
                    Elevator elevator = elevators[elevatorIndex];
                    List<Integer> destinationsList = destinations.get(elevatorIndex);

                    // 1. 在第一层的处理
                    if (elevator.getCurrentFloor() == 1) {
                        List<Integer> waitingList = elevator.getFloorWaitingQueues().get(1);
                        while (number_elevators[elevatorIndex] < MAX && !waitingList.isEmpty()) {
                            int staffIndex = waitingList.remove(0);
                            elevator.addToElevator(staffIndex);
                            elevator.removeFromWaitingQueue(1, staffIndex); // 从等待队列中移除
                            destinationsList.add(staff[staffIndex].getDestination());
                            number_elevators[elevatorIndex]++;
                        }
                        // 对目标楼层进行排序和去重
                        Collections.sort(destinationsList);
                        destinationsList = new ArrayList<>(new HashSet<>(destinationsList));
                    }

                    // 2. 上升阶段的处理
                    if (!is_return[elevatorIndex]) {
                        // 如果不在服务范围内，直接上升
                        if (elevator.getCurrentFloor() < elevator.getStart_floor() ||
                                elevator.getCurrentFloor() > elevator.getEnd_floor()) {
                            elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
                        } else {
                            // 在服务范围内
                            // 首先检查当前楼层是否有乘客需要下电梯
                            Iterator<Integer> passengerIterator = elevator.getElevatorPassengers().iterator();
                            while (passengerIterator.hasNext()) {
                                int passengerIndex = passengerIterator.next();
                                if (staff[passengerIndex].getDestination() == elevator.getCurrentFloor()) {
                                    passengerIterator.remove();
                                    elevator.removeFromElevator(passengerIndex); // 从电梯乘客列表中移除
                                    number_elevators[elevatorIndex]--;
                                    destinationsList.remove(Integer.valueOf(elevator.getCurrentFloor()));
                                    staff[passengerIndex].setStayTime(staff[passengerIndex].getStayTime() + currentTime);  // 修改设置逗留时间
                                }
                            }

                            // 检查是否满载
                            if (number_elevators[elevatorIndex] < MAX) {
                                // 未满载，检查当前楼层是否有等待的乘客
                                List<Integer> waitingList = elevator.getFloorWaitingQueues().get(elevator.getCurrentFloor());
                                while (number_elevators[elevatorIndex] < MAX && !waitingList.isEmpty()) {
                                    int staffIndex = waitingList.remove(0);
                                    elevator.addToElevator(staffIndex);
                                    elevator.removeFromWaitingQueue(elevator.getCurrentFloor(), staffIndex);
                                    destinationsList.add(staff[staffIndex].getDestination());
                                    number_elevators[elevatorIndex]++;
                                }
                                // 对目标楼层进行排序和去重
                                Collections.sort(destinationsList);
                                destinationsList = new ArrayList<>(new HashSet<>(destinationsList));
                            }

                            // 如果还没到最高层，继续上升
                            if (elevator.getCurrentFloor() < elevator.getEnd_floor()) {
                                elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
                            } else {
                                // 到达最高层，开始下降
                                is_return[elevatorIndex] = true;
                            }
                        }
                    }

                    // 3. 下降阶段的处理
                    if (is_return[elevatorIndex]) {
                        if (elevator.getCurrentFloor() > 1) {
                            elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
                        } else {
                            is_return[elevatorIndex] = false;
                        }
                    }
                });
            }

            // 输出当前时间和电梯状态
            int minute = time / 60;
            int second = time % 60;
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("电梯运行时间：" + minute + " 分 " + second + " 秒");
            System.out.println("当前电梯运行状态：");
            if(display == 0) {
                for (int i = 0; i < 10; i++)
                {
                    if(elevators[i].getWaitingCount() != 0 || number_elevators[i] != 0) {
                        System.out.printf("电梯%-12s 当前楼层：%3d 轿厢中人数：%3d 等待队列：%4d 人\n",
                                elevators[i].getNumber(),
                                elevators[i].getCurrentFloor(),
                                number_elevators[i],
                                elevators[i].getWaitingCount());
                    }
                    else
                    {
                        System.out.printf("电梯%-10s已将所有人员运输完毕（无等待队列且电梯里无人），等待其他电梯运行结束\n", elevators[i].getNumber());
                    }
                }
            }
            else
            {
                if(elevators[display - 1].getWaitingCount() != 0 || number_elevators[display - 1] != 0) {
                    System.out.println("电梯" + elevators[display - 1].getNumber() +
                            " 当前楼层：" + elevators[display - 1].getCurrentFloor() +
                            " 轿厢中人数：" + number_elevators[display - 1] +
                            " 等待队列：" + elevators[display - 1].getWaitingCount() + " 人");
                    System.out.println("电梯里的所有人员信息（如果为空，则说明电梯正在下降到1楼） :");
                    List<Integer> passengersCopy = new ArrayList<>(elevators[display - 1].getElevatorPassengers());
                    for (Integer staffIndexObj : passengersCopy) {
                        if (staffIndexObj == null) continue;  // 跳过 null
                        int staffIndex = staffIndexObj;      // 自动拆箱安全
                        Staff s = staff[staffIndex];
                        System.out.printf("员工%-10s 所属公司：%2d 目标楼层：%2d 房间号：%4d\n",
                                s.getName(), s.getCompany(), s.getDestination(), s.getRoom());
                    }
                }
                else {
                    System.out.printf("电梯 %-10s 已将所有人员运输完毕（无等待队列且电梯里无人），等待其他电梯运行结束\n",elevators[display - 1].getNumber());
                }
            }
            time++;

            // 检查是否所有电梯的等待队列都为空
            boolean allArrived = true;
            int totalWaiting = 0;
            for (int i = 0; i < 10; i++) {
                totalWaiting += elevators[i].getWaitingCount();
            }
            allArrived = (totalWaiting == 0);

            // 检查所有电梯是否都为空
            boolean allElevatorsEmpty = true;
            for (int i = 0; i < 10; i++) {
                if (number_elevators[i] != 0 || elevators[i].getWaitingCount() != 0) {
                    allElevatorsEmpty = false;
                    break;
                }
            }

            // 只有当所有电梯都为空时，才结束程序
            is_empty = allArrived && allElevatorsEmpty;
        }
        System.out.println("已将所有人员运输完毕，模拟电梯运行程序结束。");
        executor.shutdown();
    }

    static int assignRoom(int company, int[][] rooms){
        int room = 0;
        boolean find = false;
        for (int i = (company - 1) * 3; i < 2 + (company - 1) * 3; i++) {
            List<Integer> availableRooms = new ArrayList<>();

            // 收集当前楼层中未满的房间编号
            for (int j = 0; j < 20; j++) {
                if (rooms[i][j] < 10) {
                    availableRooms.add(j);
                }
            }

            // 随机选择一个未满房间
            if (!availableRooms.isEmpty()) {
                int j = availableRooms.get(new Random().nextInt(availableRooms.size()));
                rooms[i][j]++;
                room = (i + 1) * 100 + j;
                return room;
            }
        }

        if (!find)
        {
            for(int i = 29; i >= 0; i--)  // 从30楼开始寻找空位
            {
                for(int j = 19; j >= 0; j--)  // 每间寻找空位
                {
                    if (rooms[i][j] < 10)
                    {
                        rooms[i][j]++;
                        room = (i+1) * 100 + j;
                        return room;
                    }
                }
            }
        }
        if (!find) System.out.println("没有空闲房间！");
        return room;
    }
}

class Company{
    private String name;  // 公司名
    private int number;  // 员工数

    public Company(String name, int number){
        this.name = name;
        this.number = number;
    }

    public String getName(){
        return name;
    }

    public int getNumber(){
        return number;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setNumber(int number){
        this.number = number;
    }
}

class Staff{
    private String name;  // 姓名
    private int company;  // 所属公司
    private int room;  //几层几号房间 xxxx (例如：0808代表8层8号房间)
    private int position;  // 初始位置，默认地面层（第0层）
    private int destination;  // 目标楼层,默认房间楼层
    private int stayTime;  // 逗留时间

    public Staff(String name, int company, int position, int destination, int room){
        this.name = name;
        this.company = company;
        this.position = position;
        this.room = room;
        this.destination = destination;
    }

    public String getName(){
        return name;
    }

    public int getCompany(){
        return company;
    }

    public int getPosition(){
        return position;
    }

    public int getDestination(){
        return destination;
    }

    public int getStayTime(){
        return stayTime;
    }

    public int getRoom(){
        return room;
    }

    public void setRoom(int room){
        this.room = room;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCompany(int company){
        this.company = company;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void setDestination(int destination){
        this.destination = destination;
    }

    public void setStayTime(int stayTime){
        this.stayTime = stayTime;
    }
}

class  Elevator{
    private String number;
    private int start_floor;  //  起始楼层
    private int end_floor;  //  终点楼层
    private int rise_time = 1;  // 上升时间一层时间为1秒
    public int currentFloor;
    private List<Integer> elevatorPassengers; // 轿厢中当前的人员信息
    private List<List<Integer>> floorWaitingQueues; // 每层楼的等待队列
    private int waitingCount; // 总等待人数

    public Elevator(String number, int start_floor, int end_floor)
    {
        this.number = number;
        this.start_floor = start_floor;
        this.end_floor = end_floor;
        this.currentFloor = 1;
        this.elevatorPassengers = new ArrayList<>();
        this.floorWaitingQueues = new ArrayList<>();
        for (int i = 0; i <= 30; i++) {
            this.floorWaitingQueues.add(new ArrayList<>());
        }
        this.waitingCount = 0;
    }

    public List<Integer> getElevatorPassengers() {
        return elevatorPassengers;
    }

    public List<List<Integer>> getFloorWaitingQueues() {
        return floorWaitingQueues;
    }

    public int getWaitingCount() {
        return waitingCount;
    }

    public void addToWaitingQueue(int floor, int staffIndex) {
        floorWaitingQueues.get(floor).add(staffIndex);
        waitingCount++;
    }

    public void removeFromWaitingQueue(int floor, int staffIndex) {
        floorWaitingQueues.get(floor).remove(Integer.valueOf(staffIndex));
        waitingCount--;
    }

    public void addToElevator(int staffIndex) {
        elevatorPassengers.add(staffIndex);
    }

    public void removeFromElevator(int staffIndex) {
        elevatorPassengers.remove(Integer.valueOf(staffIndex));
    }

    public int getRise_time()
    {
        return rise_time;
    }

    public int getStart_floor() {
        return start_floor;
    }

    public int getEnd_floor() {
        return end_floor;
    }

    public String getNumber() {
        return number;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }
}