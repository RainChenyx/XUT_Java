// 模拟电梯运行测试类
package OOP1A;

public class elevatorSimulatorTest {
    private Staff[] staff;
    private Elevator[] elevators;
    private Company[] companies;
    private int[][] rooms;

    public elevatorSimulatorTest() {
        // 初始化测试数据
        staff = new Staff[10]; // 使用较小的数量进行测试
        elevators = new Elevator[10];
        companies = new Company[10];
        rooms = new int[30][20];

        // 初始化电梯
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

        // 初始化公司
        for (int i = 0; i < 10; i++) {
            companies[i] = new Company("Company" + (i + 1), 1);
        }

        // 初始化员工
        for (int i = 0; i < 10; i++) {
            staff[i] = new Staff("ID:" + (i + 1), (i % 10) + 1, 1, 5, 501);
        }
    }

    public void testElevatorInitialization() {
        System.out.println("测试电梯初始化...");
        if (elevators[0].getCurrentFloor() == 1) {
            System.out.println("✓ 电梯初始楼层正确");
        } else {
            System.out.println("✗ 电梯初始楼层错误");
        }
        if (elevators[0].getStart_floor() == 25) {
            System.out.println("✓ 电梯起始楼层范围正确");
        } else {
            System.out.println("✗ 电梯起始楼层范围错误");
        }
        if (elevators[0].getEnd_floor() == 30) {
            System.out.println("✓ 电梯终止楼层范围正确");
        } else {
            System.out.println("✗ 电梯终止楼层范围错误");
        }
        if (elevators[0].getWaitingCount() == 0) {
            System.out.println("✓ 电梯初始等待人数正确");
        } else {
            System.out.println("✗ 电梯初始等待人数错误");
        }
        if (elevators[0].getElevatorPassengers().isEmpty()) {
            System.out.println("✓ 电梯初始乘客列表为空");
        } else {
            System.out.println("✗ 电梯初始乘客列表不为空");
        }
    }

    public void testStaffInitialization() {
        System.out.println("\n测试员工初始化...");
        if ("ID:1".equals(staff[0].getName())) {
            System.out.println("✓ 员工ID正确");
        } else {
            System.out.println("✗ 员工ID错误");
        }
        if (staff[0].getCompany() == 1) {
            System.out.println("✓ 员工所属公司正确");
        } else {
            System.out.println("✗ 员工所属公司错误");
        }
        if (staff[0].getPosition() == 1) {
            System.out.println("✓ 员工初始位置正确");
        } else {
            System.out.println("✗ 员工初始位置错误");
        }
        if (staff[0].getDestination() == 5) {
            System.out.println("✓ 员工目标楼层正确");
        } else {
            System.out.println("✗ 员工目标楼层错误");
        }
        if (staff[0].getRoom() == 501) {
            System.out.println("✓ 员工房间号正确");
        } else {
            System.out.println("✗ 员工房间号错误");
        }
    }

    public void testAddToWaitingQueue() {
        System.out.println("\n测试添加等待队列...");
        elevators[0].addToWaitingQueue(1, 0);
        if (elevators[0].getWaitingCount() == 1) {
            System.out.println("✓ 等待队列人数正确");
        } else {
            System.out.println("✗ 等待队列人数错误");
        }
        if (elevators[0].getFloorWaitingQueues().get(1).contains(0)) {
            System.out.println("✓ 等待队列包含正确的员工");
        } else {
            System.out.println("✗ 等待队列不包含正确的员工");
        }
    }

    public void testAddToElevator() {
        System.out.println("\n测试添加乘客到电梯...");
        elevators[0].addToElevator(0);
        if (elevators[0].getElevatorPassengers().contains(0)) {
            System.out.println("✓ 电梯乘客列表包含正确的员工");
        } else {
            System.out.println("✗ 电梯乘客列表不包含正确的员工");
        }
    }

    public void testElevatorMovement() {
        System.out.println("\n测试电梯移动...");
        elevators[0].setCurrentFloor(2);
        if (elevators[0].getCurrentFloor() == 2) {
            System.out.println("✓ 电梯成功移动到指定楼层");
        } else {
            System.out.println("✗ 电梯未能移动到指定楼层");
        }
    }

    public void testCompanyInitialization() {
        System.out.println("\n测试公司初始化...");
        if ("Company1".equals(companies[0].getName())) {
            System.out.println("✓ 公司名称正确");
        } else {
            System.out.println("✗ 公司名称错误");
        }
        if (companies[0].getNumber() == 1) {
            System.out.println("✓ 公司员工数量正确");
        } else {
            System.out.println("✗ 公司员工数量错误");
        }
    }

    public void testRoomAssignment() {
        System.out.println("\n测试房间分配...");
        int room = elevatorSimulator.assignRoom(1, rooms);
        if (room > 0 && room <= 3000) {
            System.out.println("✓ 房间分配在有效范围内");
        } else {
            System.out.println("✗ 房间分配超出有效范围");
        }
    }

    public void runAllTests() {
        System.out.println("开始运行所有测试...\n");
        testElevatorInitialization();
        testStaffInitialization();
        testCompanyInitialization();
        testRoomAssignment();
        testAddToWaitingQueue();
        testAddToElevator();
        testElevatorMovement();
        System.out.println("\n所有测试完成！");
    }

    public static void main(String[] args) {
        elevatorSimulatorTest tester = new elevatorSimulatorTest();
        tester.runAllTests();
    }
}
