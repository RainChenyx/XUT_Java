package OOP1A;

public class StaffTest {
    private Staff staff;

    public StaffTest() {
        // 初始化测试数据
        staff = new Staff("ID:1", 1, 1, 5, 501);
    }

    public void testStaffInitialization() {
        System.out.println("测试员工初始化...");
        if ("ID:1".equals(staff.getName())) {
            System.out.println("✓ 员工ID正确");
        } else {
            System.out.println("✗ 员工ID错误");
        }
        if (staff.getCompany() == 1) {
            System.out.println("✓ 员工所属公司正确");
        } else {
            System.out.println("✗ 员工所属公司错误");
        }
        if (staff.getPosition() == 1) {
            System.out.println("✓ 员工初始位置正确");
        } else {
            System.out.println("✗ 员工初始位置错误");
        }
        if (staff.getDestination() == 5) {
            System.out.println("✓ 员工目标楼层正确");
        } else {
            System.out.println("✗ 员工目标楼层错误");
        }
        if (staff.getRoom() == 501) {
            System.out.println("✓ 员工房间号正确");
        } else {
            System.out.println("✗ 员工房间号错误");
        }
    }

    public void testStaffSetters() {
        System.out.println("\n测试员工属性设置...");
        staff.setName("ID:2");
        staff.setCompany(2);
        staff.setPosition(2);
        staff.setDestination(6);
        staff.setRoom(502);
        staff.setStayTime(10);

        if ("ID:2".equals(staff.getName())) {
            System.out.println("✓ 员工ID设置正确");
        } else {
            System.out.println("✗ 员工ID设置错误");
        }
        if (staff.getCompany() == 2) {
            System.out.println("✓ 员工所属公司设置正确");
        } else {
            System.out.println("✗ 员工所属公司设置错误");
        }
        if (staff.getPosition() == 2) {
            System.out.println("✓ 员工位置设置正确");
        } else {
            System.out.println("✗ 员工位置设置错误");
        }
        if (staff.getDestination() == 6) {
            System.out.println("✓ 员工目标楼层设置正确");
        } else {
            System.out.println("✗ 员工目标楼层设置错误");
        }
        if (staff.getRoom() == 502) {
            System.out.println("✓ 员工房间号设置正确");
        } else {
            System.out.println("✗ 员工房间号设置错误");
        }
        if (staff.getStayTime() == 10) {
            System.out.println("✓ 员工逗留时间设置正确");
        } else {
            System.out.println("✗ 员工逗留时间设置错误");
        }
    }

    public void runAllTests() {
        System.out.println("开始运行Staff类测试...\n");
        testStaffInitialization();
        testStaffSetters();
        System.out.println("\nStaff类测试完成！");
    }

    public static void main(String[] args) {
        StaffTest tester = new StaffTest();
        tester.runAllTests();
    }
} 