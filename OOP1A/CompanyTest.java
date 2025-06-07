package OOP1A;

public class CompanyTest {
    private Company company;

    public CompanyTest() {
        // 初始化测试数据
        company = new Company("Company1", 100);
    }

    public void testCompanyInitialization() {
        System.out.println("测试公司初始化...");
        if ("Company1".equals(company.getName())) {
            System.out.println("✓ 公司名称正确");
        } else {
            System.out.println("✗ 公司名称错误");
        }
        if (company.getNumber() == 100) {
            System.out.println("✓ 公司员工数量正确");
        } else {
            System.out.println("✗ 公司员工数量错误");
        }
    }

    public void testCompanySetters() {
        System.out.println("\n测试公司属性设置...");
        company.setName("Company2");
        company.setNumber(200);

        if ("Company2".equals(company.getName())) {
            System.out.println("✓ 公司名称设置正确");
        } else {
            System.out.println("✗ 公司名称设置错误");
        }
        if (company.getNumber() == 200) {
            System.out.println("✓ 公司员工数量设置正确");
        } else {
            System.out.println("✗ 公司员工数量设置错误");
        }
    }

    public void runAllTests() {
        System.out.println("开始运行Company类测试...\n");
        testCompanyInitialization();
        testCompanySetters();
        System.out.println("\nCompany类测试完成！");
    }

    public static void main(String[] args) {
        CompanyTest tester = new CompanyTest();
        tester.runAllTests();
    }
} 