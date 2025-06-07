package OOP1A;

public class ElevatorTest {
    private Elevator elevator;

    public ElevatorTest() {
        // 初始化测试数据
        elevator = new Elevator("Elevator 1", 1, 10);
    }

    public void testElevatorInitialization() {
        System.out.println("测试电梯初始化...");
        if ("Elevator 1".equals(elevator.getNumber())) {
            System.out.println("✓ 电梯编号正确");
        } else {
            System.out.println("✗ 电梯编号错误");
        }
        if (elevator.getStart_floor() == 1) {
            System.out.println("✓ 电梯起始楼层正确");
        } else {
            System.out.println("✗ 电梯起始楼层错误");
        }
        if (elevator.getEnd_floor() == 10) {
            System.out.println("✓ 电梯终止楼层正确");
        } else {
            System.out.println("✗ 电梯终止楼层错误");
        }
        if (elevator.getCurrentFloor() == 1) {
            System.out.println("✓ 电梯当前楼层正确");
        } else {
            System.out.println("✗ 电梯当前楼层错误");
        }
        if (elevator.getWaitingCount() == 0) {
            System.out.println("✓ 电梯等待人数正确");
        } else {
            System.out.println("✗ 电梯等待人数错误");
        }
        if (elevator.getElevatorPassengers().isEmpty()) {
            System.out.println("✓ 电梯乘客列表为空");
        } else {
            System.out.println("✗ 电梯乘客列表不为空");
        }
    }

    public void testElevatorMovement() {
        System.out.println("\n测试电梯移动...");
        elevator.setCurrentFloor(5);
        if (elevator.getCurrentFloor() == 5) {
            System.out.println("✓ 电梯成功移动到指定楼层");
        } else {
            System.out.println("✗ 电梯未能移动到指定楼层");
        }
    }

    public void testWaitingQueue() {
        System.out.println("\n测试等待队列...");
        elevator.addToWaitingQueue(1, 0);
        if (elevator.getWaitingCount() == 1) {
            System.out.println("✓ 等待队列人数正确");
        } else {
            System.out.println("✗ 等待队列人数错误");
        }
        if (elevator.getFloorWaitingQueues().get(1).contains(0)) {
            System.out.println("✓ 等待队列包含正确的员工");
        } else {
            System.out.println("✗ 等待队列不包含正确的员工");
        }

        elevator.removeFromWaitingQueue(1, 0);
        if (elevator.getWaitingCount() == 0) {
            System.out.println("✓ 成功从等待队列移除员工");
        } else {
            System.out.println("✗ 未能从等待队列移除员工");
        }
    }

    public void testElevatorPassengers() {
        System.out.println("\n测试电梯乘客...");
        elevator.addToElevator(0);
        if (elevator.getElevatorPassengers().contains(0)) {
            System.out.println("✓ 成功添加乘客到电梯");
        } else {
            System.out.println("✗ 未能添加乘客到电梯");
        }

        elevator.removeFromElevator(0);
        if (!elevator.getElevatorPassengers().contains(0)) {
            System.out.println("✓ 成功从电梯移除乘客");
        } else {
            System.out.println("✗ 未能从电梯移除乘客");
        }
    }

    public void runAllTests() {
        System.out.println("开始运行Elevator类测试...\n");
        testElevatorInitialization();
        testElevatorMovement();
        testWaitingQueue();
        testElevatorPassengers();
        System.out.println("\nElevator类测试完成！");
    }

    public static void main(String[] args) {
        ElevatorTest tester = new ElevatorTest();
        tester.runAllTests();
    }
} 